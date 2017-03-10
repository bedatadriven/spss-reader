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

import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;


/**
 * This test verifies that the SPSS reader behaves properly when the underlying
 * stream does not always read all the bytes requested. This may happen because
 * the underlying stream does not block, or for other reasons beyond our understanding.
 *
 * Specifically, this test should make SpssDDataFileReader play nicely with the GAEVFS
 * system.
 */
public class SpssDataFileReaderNonBlockingTest {


  @Test
  public void nonBlockingRead() throws IOException {

    InputStream is = SpssDataFileReaderTest.class.getResourceAsStream("/testdata.sav");
    assertNotNull("InputStream", is);
    SpssDataFileReader reader = new SpssDataFileReader(new DataInputStream(
        new MockNonBlockingInputStream(is)));

    while(reader.readNextCase()) {}

  }

  /**
   * Mimics an InputStream that reads at most 10 bytes at a time.
   */
  public static class MockNonBlockingInputStream extends InputStream {

    private final InputStream inner;

    public MockNonBlockingInputStream(InputStream inner) {
      this.inner = inner;
    }

    @Override
    public int read(byte[] b) throws IOException {
      return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int adjustedLen = len;
      if(len > 10) {
        adjustedLen = 10;
      }
      return super.read(b, off, adjustedLen);
    }

    @Override
    public int read() throws IOException {
      return inner.read();
    }
  }
}

