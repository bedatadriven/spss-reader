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
import java.util.HashMap;
import java.util.Map;

/**
 * The ValueLabels record contains a set of value-label pairs, but is not
 * linked directly to a variable.
 * <p/>
 * The link between a ValueLabels record and one or more is defined by
 * a subsequent ValueLabelsVariables record.
 *
 */
class ValueLabelsRecord {

  static final int RECORD_TYPE = 3;

  private Map<Double, byte[]> labels = new HashMap<>();

  ValueLabelsRecord(SpssInputStream inputStream) throws IOException {
    int count = inputStream.readInt();
    for (int i = 0; i < count; i++) {
      labels.put(inputStream.readDouble(), readValueLabel(inputStream));
    }
  }

  private byte[] readValueLabel(SpssInputStream inputStream) throws IOException {
    int actualLength = inputStream.readUnsignedByte();
    int lengthWithPadding = calculateLengthInStream(actualLength);
    byte[] label = inputStream.readBytes(actualLength);
    inputStream.skipBytes(lengthWithPadding - actualLength);
    return label;
  }

  /**
   * Variable labels may take up additional space in the stream to preserve
   * 8-byte alignment.
   * <p/>
   * If for example, the label "YES" will actually occupy 8-bytes in the stream.
   * The label "Don't know" will occupy 16-bytes.
   *
   * @param length defined length of the label
   * @return
   */
  private int calculateLengthInStream(int length) {
    int mod = (length + 1) % 8;
    int aligned = length; // null terminated
    if (mod != 0) {
      aligned = aligned + (8 - mod);
    }
    return aligned;
  }

  public Map<Double, byte[]> getLabels() {
    return labels;
	}
}
