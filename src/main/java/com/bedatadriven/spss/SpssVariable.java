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

/**
 *
 */
package com.bedatadriven.spss;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpssVariable {

  static final int RECORD_TYPE = 2;

  private static final Charset ASCII = Charset.forName("ASCII");


  private int typeCode;

  /**
   * Variable format for print operations
   */
  private SpssVariableFormat printFormat;

  /**
   * Variable format for write operations
   */
  private SpssVariableFormat writeFormat;

  /**
   * Maximum 8-byte name of the variable
   */
  String shortName;

  /**
   * Optional longer (>8 bytes) name of the variable, encoded
   * in the file's specified character encoding
   */
  byte[] longName;

  /**
   * True if the variable is stored as a number, false if the
   * variable contains text data
   */
  private boolean numeric;

  /**
   * For variables stored as text, the length of the
   * field in bytes
   */
  int stringLength;
  
  /**
   * For variables stored as very long string value.
   * Such strings get stored using multiple variables (as text),
   * with each variable holding 255 bytes of the total string
   * length, and the last variable the remainder. 
   */
  int veryLongStringLength = -1;
  
 /**
  * For variables that are subsequent segments of a
  * preceding very long string variable.
  */
  boolean isVeryLongStringSegment = false;

  /**
   * The variable's label
   */
  private byte[] label;

  /**
   * The offset within the physical case record where this variable is stored
   * (not necessarily equal to the logical index, some variables require multiple
   * records within the physical case record)
   */
  private int positionInRecord;

  private final SpssInputStream inputStream;

  /**
   * Logical index of the variable
   */
  private int logicalIndex;

  /**
   * True if this variable has a range of values that
   * are treated as "user-missing"
   */
  private boolean hasMissingRange;
  private double missingMin;
  private double missingMax;

  /**
   * List of discrete values to be treated as "user-missing"
   */
  private List<Double> missing;

  /**
   * List of discrete string values to be treated as "user-missing"
   */
  private List<String> missingStrings;

  /**
   * Labels for numeric values
   */
  Map<Double, byte[]> valueLabels;

  Map<String, byte[]> stringValueLabels;

  SpssVariable(SpssInputStream inputStream, int position, int logicalIndex) throws IOException {
    this.inputStream = inputStream;
    this.logicalIndex = logicalIndex;
    this.positionInRecord = position;

    typeCode = inputStream.readInt();

    if (typeCode == 0) {
      numeric = true;
      stringLength = -1;
    } else {
      numeric = false;
      stringLength = typeCode;
    }


    int hasLabel = inputStream.readInt();

    /* Missing Values
       *   0 = no missing values
       *   1 = 1 discrete missing value
       *   2 = 2 discrete missing values
       *   3 = 3 discrete missing values
       *  -2 = range of missing values
       *  -3 = range of missing values and one discrete missing value
       */
    int missingCode = inputStream.readInt();
    missing = new ArrayList<>();
    missingStrings = new ArrayList<>();

    printFormat = new SpssVariableFormat(inputStream.readInt());
    writeFormat = new SpssVariableFormat(inputStream.readInt());

    // we're assuming that the short name is always ASCII-encoded
    // but should verify...
    byte[] shortNameBytes = inputStream.readBytes(8);


    shortName = (new String(shortNameBytes, ASCII)).trim();


    if (hasLabel == 1) {
      readLabel();
    }
    if (missingCode > 0) {
      for (int i = 0; i < missingCode; i++) {
        if (numeric) {
          missing.add(inputStream.readDouble());
        } else {
          missingStrings.add(new String(inputStream.readBytes(8)));
        }
      }
    } else if (missingCode < 0) {
      hasMissingRange = true;
      missingMin = inputStream.readDouble();
      missingMax = inputStream.readDouble();

      if (missingCode == -3) {
        missing.add(inputStream.readDouble());
      }
    }
  }

  private void readLabel() throws IOException {
    int labelLength = inputStream.readInt();
    byte[] bytes = inputStream.readBytes(SpssInputStream.alignSize(labelLength, 4));
    label = new byte[labelLength];
    System.arraycopy(bytes, 0, label, 0, labelLength);
  }

  public boolean isMissing(double value) {
    if (hasMissingRange && (missingMin <= value && value <= missingMax)) {
      return true;
    } else {
      return missing.contains(value);
    }
  }

  public String getVariableName() {
    if (longName != null) {
      return inputStream.stringFromBytes(longName);
    } else {
      return shortName;
    }
  }

  public int getTypeCode() {
    return typeCode;
  }

  public SpssVariableFormat getPrintFormat() {
    return printFormat;
  }

  public SpssVariableFormat getWriteFormat() {
    return writeFormat;
  }

  int getPositionInRecord() {
    return positionInRecord;
  }

  public String getShortName() {
    return shortName;
  }

  public String getVariableLabel() {
    return inputStream.stringFromBytes(label);
  }

  public boolean isNumeric() {
    return numeric;
  }
  
  public boolean isVeryLongString() {
    return veryLongStringLength > -1;
  }
  
  public boolean isVeryLongStringSegment() {
    return isVeryLongStringSegment;
  }

  public int getIndex() {
    return logicalIndex;
  }

  public boolean isHasMissingRange() {
    return hasMissingRange;
  }

  public double getMissingMin() {
    return missingMin;
  }

  public double getMissingMax() {
    return missingMax;
  }

  public List<Double> getMissing() {
    return missing;
  }

  public List<String> getMissingStrings() {
    return missingStrings;
  }

  public Map<Double, String> getValueLabels() {
    Map<Double, String> map = new HashMap<>();
    if (valueLabels != null) {
      for (Map.Entry<Double, byte[]> entry : valueLabels.entrySet()) {
        map.put(entry.getKey(), inputStream.stringFromBytes(entry.getValue()));
      }
    }
    return map;
  }

    public Map<String, String> getStringValueLabels() {
        Map<String, String> map = new HashMap<>();
        if (stringValueLabels != null) {
            for (Map.Entry<String, byte[]> entry : stringValueLabels.entrySet()) {
                map.put(entry.getKey(), inputStream.stringFromBytes(entry.getValue()));
            }
        }
        return map;
    }
}