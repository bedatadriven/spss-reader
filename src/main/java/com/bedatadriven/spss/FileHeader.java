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

class FileHeader {

  public static final int PRODUCT_NAME_LENGTH = 60;
  public static final int LAYOUT_2 = 2;
  public static final int LAYOUT_3 = 3;
  public static final int FILE_LABEL_LENGTH = 64;
  private String productName;
  private int compression;
  private int weightVariablePosition;
  private byte[] creationDate;
  private byte[] creationTime;
  private byte[] fileLabel;

  private int caseSize;
  private int numCases;
  private double bias;   // no idea what this

  public FileHeader(SpssInputStream inputStream) throws IOException {

    productName = new String(inputStream.readBytes(PRODUCT_NAME_LENGTH));
    int layoutCode = inputStream.readInt();
    inputStream.setNeedToFlipBytes((layoutCode != LAYOUT_2 && layoutCode != LAYOUT_3));

    caseSize = inputStream.readInt();

    compression = inputStream.readInt();
    weightVariablePosition = inputStream.readInt();

    numCases = inputStream.readInt();
    
    bias = inputStream.readDouble();
    creationDate = inputStream.readBytes(9);
    creationTime = inputStream.readBytes(8);
    fileLabel = inputStream.readBytes(FILE_LABEL_LENGTH);

    inputStream.skipBytes(3); // alignment

  }

  /**
   * @return The number of *records* per case, not the number of variables. Some variables
   *         require more than one record, for instance text. (I think)
   */
  public int getCaseSize() {
    return caseSize;
  }

  public int getNumCases() {
    return numCases;
  }

  public int getWeightVariableRecordIndex() {
    return weightVariablePosition;
  }
  
  public boolean isCompressed() {
    return compression > 0;
  }
  public int getCompression() {
    return compression;
  }

}
