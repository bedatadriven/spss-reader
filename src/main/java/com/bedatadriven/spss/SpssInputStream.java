/*
 * Copyright 2017-2019 BeDataDriven Groep BV
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.bedatadriven.spss;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SpssInputStream {
  private DataInputStream file;
  private boolean needToFlipBytes;
  private final Charset charset;

  public SpssInputStream(InputStream is, Charset charset) {
    this.file = new DataInputStream(is);
    this.charset = charset;
  }

  public SpssInputStream(InputStream is) {
    this(is, Charset.forName("Cp1252"));
  }

  public DataInputStream getFile() {
    return file;
  }

  public void setFile(DataInputStream file) {
    this.file = file;
  }

  public boolean isNeedToFlipBytes() {
    return needToFlipBytes;
  }

  public void setNeedToFlipBytes(boolean needToFlipBytes) {
    this.needToFlipBytes = needToFlipBytes;
  }

  /**
   * @param count Number of bytes
   * @return A newly-created array containing the specified number of bytes
   * @throws IOException If the EOF is reached before the specified number of bytes could be read
   */
  public byte[] readBytes(int count) throws IOException {
    byte[] buffer = new byte[count];
    getFile().readFully(buffer);
    return buffer;
  }

  /**
   * @return A 32-bit integer reader directly from the current stream position, without any adjustments
   *         to byte order
   */
  public int readRawInt() throws IOException {
    return file.readInt();
  }


  /**
   * @return A 32-bit integer read from the current stream position, adjusting byte order if necessary
   */
  public int readInt() throws IOException {

    int i = getFile().readInt();

    if (isNeedToFlipBytes()) {
      return Integer.reverseBytes(i);
    } else {
      return i;
    }
  }

  /**
   * @return An 8-byte real number from the current stream position,
   *         adjusting byte order if necessary
   */
  public double readDouble() throws IOException {
    double d = getFile().readDouble();
    if (isNeedToFlipBytes()) {
      long l = Double.doubleToRawLongBits(d);
      l = Long.reverseBytes(l);
      return Double.longBitsToDouble(l);
    } else {
      return d;
    }
  }

  public int readUnsignedByte() throws IOException {
    return getFile().readUnsignedByte();
  }

  public void skipBytes(int count) throws IOException {
    readBytes(count);
  }

  /**
   * Encodes a series of bytes into a character stream, taking
   * into account the datafile's character set, if specified. Otherwise
   * the default charset is used.
   *
   */
  public String stringFromBytes(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    return new String(bytes, charset);
  }

  static int alignSize(int i, int bytes) {
    int mod = (i % bytes);
    if (mod == 0) {
      return i;
    } else {
      return i + (bytes - mod);
    }
  }
}