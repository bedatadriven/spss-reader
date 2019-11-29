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

class VersionHeader {

  static final int EXTENDED_RECORD_TYPE = 3;

  private int versionMajor;
  private int versionMinor;
  private int revision;
  private int machine;
  private int floating;
  private int compression;
  private int endianness;
  private int character;

  public VersionHeader(SpssInputStream inputStream) throws IOException {
    versionMajor = inputStream.readInt();
    versionMinor = inputStream.readInt();
    revision = inputStream.readInt();
    machine = inputStream.readInt();
    floating = inputStream.readInt();
    compression = inputStream.readInt();
    endianness = inputStream.readInt();
    character = inputStream.readInt();
  }

}
