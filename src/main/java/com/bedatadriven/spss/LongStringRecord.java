/*
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

class LongStringRecord {

  static final int EXTENDED_RECORD_TYPE = 14;
  private final SpssInputStream inputStream;
  private byte[] longStrings;

  LongStringRecord(ExtendedRecordHeader header, SpssInputStream inputStream) throws IOException {
    this.inputStream = inputStream;
    longStrings = inputStream.readBytes(header.getTotalLength());
  }

  void parseInto(List<SpssVariable> variables, Map<String, SpssVariable> variableNames) {

    // longNames is in the format of
    // SHORT1=Longer name\tSHORT2=Another longer name...

    int longStringsLength = longStrings.length;
    String shortName = null;
    int strLen = 0;
    int tokenStart = 0;
    int varIndex = 0;

    for (int i = 0; i <= longStringsLength; i++) {
      if (i < longStringsLength && longStrings[i] == '=') {
        // here we continue to assume that shortName is ASCII-encoded
        // shouldn't make a difference in terms of lookup, but if
        // the handling of encoding is changed in Variable() than
        // it needs to be changed here as well...
        shortName = new String(longStrings, tokenStart, i - tokenStart);
        tokenStart = i + 1;
      } else if (i != tokenStart && (i == longStringsLength || longStrings[i] == '\t')) {
        byte[] byteArr = new byte[i - tokenStart];
        System.arraycopy(longStrings, tokenStart, byteArr, 0, i - tokenStart);
        String strVal = inputStream.stringFromBytes(byteArr);
        strLen = Integer.parseInt(strVal.replaceAll("\\u0000", ""));

        if (variables.get(varIndex).shortName.equals(shortName)) {
          variables.get(varIndex).longStringLength = strLen;
        } else {
          variableNames.get(shortName).longStringLength = strLen;
        }
        tokenStart = i + 1;
        varIndex++;
      }
    }
  }
}
