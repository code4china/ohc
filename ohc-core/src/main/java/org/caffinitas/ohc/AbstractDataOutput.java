/*
 *      Copyright (C) 2014 Robert Stupp, Koeln, Germany, robert-stupp.de
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.caffinitas.ohc;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;

abstract class AbstractDataOutput implements DataOutput
{
    public void write(byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }

    public void writeBoolean(boolean v) throws IOException
    {
        write(v ? 1 : 0);
    }

    public void writeByte(int v) throws IOException
    {
        write(v);
    }

    public void writeShort(int v) throws IOException
    {
        write((v >>> 8) & 0xFF);
        write(v & 0xFF);
    }

    public void writeChar(int v) throws IOException
    {
        write((v >>> 8) & 0xFF);
        write(v & 0xFF);
    }

    public void writeInt(int v) throws IOException
    {
        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>> 8) & 0xFF);
        write(v & 0xFF);
    }

    public void writeLong(long v) throws IOException
    {
        write((int) ((v >>> 56) & 0xFF));
        write((int) ((v >>> 48) & 0xFF));
        write((int) ((v >>> 40) & 0xFF));
        write((int) ((v >>> 32) & 0xFF));
        write((int) ((v >>> 24) & 0xFF));
        write((int) ((v >>> 16) & 0xFF));
        write((int) ((v >>> 8) & 0xFF));
        write((int) (v & 0xFF));
    }

    public void writeFloat(float v) throws IOException
    {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException
    {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeBytes(String s) throws IOException
    {
        int len = s.length();
        for (int i = 0 ; i < len ; i++)
            write((byte) s.charAt(i));
    }

    public void writeChars(String s) throws IOException
    {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            write((v >>> 8) & 0xFF);
            write(v & 0xFF);
        }
    }

    public void writeUTF(String str) throws IOException
    {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535)
            throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes");

        writeShort(utflen);

        int i;
        for (i=0; i<strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) break;
            writeByte(c);
        }

        for (;i < strlen; i++){
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                writeByte(c);

            } else if (c > 0x07FF) {
                writeByte(0xE0 | ((c >> 12) & 0x0F));
                writeByte(0x80 | ((c >>  6) & 0x3F));
                writeByte(0x80 | (c & 0x3F));
            } else {
                writeByte(0xC0 | ((c >>  6) & 0x1F));
                writeByte(0x80 | (c & 0x3F));
            }
        }
    }

    public static int writeUTFLen(String str)
    {
        int strlen = str.length();
        int utflen = 0;
        int c;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535)
            throw new RuntimeException("encoded string too long: " + utflen + " bytes");

        return utflen+2;
    }
}
