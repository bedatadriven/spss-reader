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

class MissingValuesHeader {

  static final int EXTENDED_RECORD_TYPE = 4;

  private double systemMissingValue;
  private double highestValue;
  private double lowestValue;

  MissingValuesHeader(SpssInputStream inputStream) throws IOException {
    systemMissingValue = inputStream.readDouble();
    highestValue = inputStream.readDouble();
    lowestValue = inputStream.readDouble();
  }

  public double getSystemMissingValue() {
    return systemMissingValue;
  }

  public void setSystemMissingValue(double systemMissingValue) {
    this.systemMissingValue = systemMissingValue;
  }

  public double getHighestValue() {
    return highestValue;
  }

  public void setHighestValue(double highestValue) {
    this.highestValue = highestValue;
  }

  public double getLowestValue() {
    return lowestValue;
  }

  public void setLowestValue(double lowestValue) {
    this.lowestValue = lowestValue;
  }
}
