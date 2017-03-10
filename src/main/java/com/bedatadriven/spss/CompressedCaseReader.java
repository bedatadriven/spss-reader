/*
 * Copyright 2017 BeDataDriven Groep BV
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

import java.io.IOException;
import java.util.List;

class CompressedCaseReader extends CaseReader {

  /**
   * If the flag is less than this value, then the value itself is an integer
   * stored as the flag. Subtract 100 from the flag to obtain the value.
   */
  private static final int MIN_STORAGE_FLAG = 252;

  /**
   * Indicates that the next 8 bytes of the string are non-whitespace
   * and are stored in the next 8-byte in the stream
   */
  private static final int STRING_FLAG = 253;

  /**
   * Indicates that the next value is stored as a double-floating
   * point in the next 8-byte block in the stream (uncompressed)
   */
  private static final int DOUBLE_FLAG = 253;

  /**
   * Indicates that the next 8 bytes of the string are whitespace.
   * (nothing is stored in the stream)
   */
  private static final int WHITESPACE_FLAG = 254;

  /**
   * Indicates that the next variable has a "system-missing" value.
   * (nothing is stored in the stream)
   */
  private static final int SYSMIS_FLAG = 255;

  private static final int END_OF_BLOCK = 8;

  private int flagIndex;
  private int[] flags = new int[8];

  public CompressedCaseReader(SpssInputStream inputStream,
                              List<SpssVariable> variables, MissingValuesHeader missingValues,
                              int numCases, CaseBuffer currentRow) {

    super(inputStream, variables, missingValues, numCases, currentRow);

    flagIndex = END_OF_BLOCK;
  }

  public void restart() throws IOException {
    super.restart();
    flagIndex = END_OF_BLOCK;
  }

  private int readNextStorageFlag() throws IOException {
    if (flagIndex >= END_OF_BLOCK) {
      for (int i = 0; i != 8; ++i) {
        flags[i] = inputStream.readUnsignedByte();
      }
      flagIndex = 0;
    }
    return flags[flagIndex++];
  }

  @Override
  protected void readRow() throws IOException {

    int storageFlag = 0;

    for (SpssVariable var : variables) {
      if (var.isNumeric()) {
        storageFlag = readNextStorageFlag();

        if (storageFlag < MIN_STORAGE_FLAG) {
          currentRow.set(var.getIndex(), (double) (storageFlag - 100));
        } else if (storageFlag == DOUBLE_FLAG) {
          currentRow.set(var.getIndex(), inputStream.readDouble());
        } else if (storageFlag == SYSMIS_FLAG) {
          currentRow.setMissing(var.getIndex());
        }
      } else {
        StringBuilder buffer = new StringBuilder();
        int totalBytesRead = 0;
        int whiteSpaceCount = 0;
        do {
          // the "compressed" strings are stored
          // in 8-byte segments. If the segment is all
          // spaces, the storage type = 254 and no other
          // data is written. Otherwise, it's 253 and 8-bytes
          // are stored.
          //
          // Most of the time, the 8-space blocks are trailing
          // spaces so we don't want to bother adding it unless
          // its followed by non-space characters
          storageFlag = readNextStorageFlag();
          if (storageFlag == STRING_FLAG) {
            while (whiteSpaceCount > 0) {
              buffer.append(' ');
              whiteSpaceCount--;
            }

            buffer.append(inputStream.stringFromBytes(inputStream.readBytes(8)));
          } else if (storageFlag == WHITESPACE_FLAG) {
            whiteSpaceCount += 8;
          }
          totalBytesRead += 8;

        } while (totalBytesRead < var.stringLength);

        currentRow.set(var.getIndex(), buffer.toString().trim());
      }
    }
  }
}
