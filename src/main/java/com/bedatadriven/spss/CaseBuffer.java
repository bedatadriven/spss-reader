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


class CaseBuffer {
  private String[] stringValues;
  private double[] doubleValues;
  private int[] trailingSpaces;

  CaseBuffer(int variableCount) {
    stringValues = new String[variableCount];
    doubleValues = new double[variableCount];
    trailingSpaces = new int[variableCount];
  }

  void set(int index, String value) {
    stringValues[index] = value;
  }

  void set(int index, double value) {
    doubleValues[index] = value;
  }
  
  void setTrailingSpaces(int index, int trailing) {
    trailingSpaces[index] = trailing;
  }

  void setMissing(int index) {
    doubleValues[index] = Double.NaN;
    stringValues[index] = null;
  }

  public String getStringValue(int variableIndex) {
    return stringValues[variableIndex];
  }
  
  private void appendStringSegmentValue(int variableIndex, StringBuilder sb) {
    String str = getStringValue(variableIndex);
    if(str != null) {
      sb.append( getStringValue(variableIndex));
      for(int i=0; i<trailingSpaces[variableIndex]; i++) {
        sb.append(' ');
      }
    }
  }
  
  public String getLongStringValue(int variableIndex, int segments) {
    StringBuilder sb = new StringBuilder();
    int currentIdx = variableIndex;

    while (0 < segments--) {
      if(currentIdx >= stringValues.length) {
        break;
      }
      appendStringSegmentValue(currentIdx, sb);
      currentIdx++;
    }
    return sb.toString().trim();
  }

  public Double getDoubleValue(int variableIndex) {
    return doubleValues[variableIndex];
  }


  public boolean isSystemMissing(int variableIndex) {
    return Double.isNaN(doubleValues[variableIndex]);
  }
}
