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

class CaseReader {

  protected final List<SpssVariable> variables;
  protected final SpssInputStream inputStream;
  protected final MissingValuesHeader missingValues;
  protected final CaseBuffer currentRow;
  protected final int numCases;

  protected int currentRowIndex;

  public CaseReader(SpssInputStream inputStream, List<SpssVariable> variables,
                    MissingValuesHeader missingValues, int numCases, CaseBuffer currentRow) {
    this.inputStream = inputStream;
    this.variables = variables;
    this.missingValues = missingValues;
    this.currentRow = currentRow;
    this.numCases = numCases;

    currentRowIndex = -1;

    // mark the position of the start of case records so we can
    // restart if requested
    inputStream.getFile().mark(Integer.MAX_VALUE);
  }

  public final boolean readNext() throws IOException {
    if (currentRowIndex + 1 < numCases) {
      readRow();
      currentRowIndex++;
      return true;
    } else {
      return false;
    }
  }

  protected void readRow() throws IOException {
    for (SpssVariable var : variables) {
      if (var.isNumeric()) {
        double value = inputStream.readDouble();
        if (value == missingValues.getSystemMissingValue()) {
          currentRow.setMissing(var.getIndex());
        } else {
          currentRow.set(var.getIndex(), value);
        }
      } else {
        byte[] value = inputStream.readBytes(SpssInputStream.alignSize(var.stringLength, 8));
        currentRow.set(var.getIndex(), inputStream.stringFromBytes(value));
      }
    }
  }

  public void restart() throws IOException {
    // move to beginning of data block
    inputStream.getFile().reset();
    currentRowIndex = -1;
  }

}
