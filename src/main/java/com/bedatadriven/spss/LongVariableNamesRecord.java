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
import java.util.Map;

class LongVariableNamesRecord {

  static final int EXTENDED_RECORD_TYPE = 13;

  private byte[] longNames;

  LongVariableNamesRecord(ExtendedRecordHeader header, SpssInputStream inputStream) throws IOException {
    longNames = inputStream.readBytes(header.getTotalLength());
  }

  void parseInto(List<SpssVariable> variables, Map<String, SpssVariable> variableNames) {

    // longNames is in the format of
    // SHORT1=Longer name\tSHORT2=Another longer name...

    int longNamesLength = longNames.length;
    String shortName = null;
    byte[] longName = null;
    int tokenStart = 0;
    int varIndex = 0;

    for (int i = 0; i <= longNamesLength; i++) {
      if (i < longNamesLength && longNames[i] == '=') {
        // here we continue to assume that shortName is ASCII-encoded
        // shouldn't make a difference in terms of lookup, but if
        // the handling of encoding is changed in Variable() than
        // it needs to be changed here as well...
        shortName = new String(longNames, tokenStart, i - tokenStart);
        tokenStart = i + 1;
      } else if (i == longNamesLength || longNames[i] == '\t') {
        longName = new byte[i - tokenStart];
        System.arraycopy(longNames, tokenStart, longName, 0, i - tokenStart);

        if (variables.get(varIndex).shortName.equals(shortName)) {
          variables.get(varIndex).longName = longName;
        } else {
          variableNames.get(shortName).longName = longName;
        }
        tokenStart = i + 1;
        varIndex++;
      }
    }
  }
}
