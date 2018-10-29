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

import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class SpssDataFileReaderTest {

  private SpssDataFileReader reader;


  @Before
  public void setUp() throws IOException {
    InputStream is = SpssDataFileReaderTest.class.getResourceAsStream("/testdata.sav");
    assertNotNull("InputStream", is);
    reader = new SpssDataFileReader(new DataInputStream(is));
  }

  @Test
  public void testReadHeader() throws IOException {

    assertEquals("variable count", 23, reader.getVariables().size());
    assertEquals("case count", 25, reader.getNumCases());
    assertEquals("compressed", true, reader.isCompressed());

    assertEquals("ID", reader.getVariableName(0));
    assertEquals("Interviewer", reader.getVariableName(4));

    assertEquals("In what language(s) can you read a newspaper? - Cannot read",
        reader.getVariables().get(6).getVariableLabel());

    assertEquals("Urban", reader.getValueLabels(1).get(1.0));
    assertEquals("Government Servant Senior Level Officer", reader.getValueLabels("d7b").get(3.0));
  }

  @Test
  public void testReadCompressed() throws IOException {

    assertTrue("read first case", reader.readNextCase());

    assertEquals(14, (int)reader.getDoubleValue("ID"));
    assertEquals(101, (int)reader.getDoubleValue("District"));

    assertTrue(reader.isSystemMissing("q1"));
    assertFalse(reader.isSystemMissing("d6_2"));

    assertEquals(0.0173893, reader.getDoubleValue("S1_IP"), 0.00001);

    assertEquals("pouring rain", reader.getStringValue("s_1"));

    reader.readNextCase();

    assertEquals("The weather is             very nice", reader.getStringValue("s_1"));

    int caseIndex = 2;

    while (caseIndex < 15) {
      reader.readNextCase();
      caseIndex++;
    }

    assertEquals(55, (int)reader.getDoubleValue("ID"));
    assertEquals(20098.33, reader.getDoubleValue("Sample_Weight"), 0.01);

  }



  @Test
  public void readAll() throws IOException {
    int casesRead = 0;
    while (reader.readNextCase()) {
      Double x = reader.getDoubleValue("ID");
      casesRead++;
    }
    assertEquals("number of cases read", reader.getNumCases(), casesRead);
  }

  @Test
  public void variableWithNoValueLabelsReturnsEmptySet() throws IOException {

    assertTrue("empty value label set returned", reader.getValueLabels("Interviewer").size() == 0);
  }

  @Test
  public void variableWithNoLabelReturnsNull() {

    assertNull("label for 'Urban'", reader.getVariables().get(1).getVariableLabel());

  }

  @Test
  public void variablePrintFormatIsReadCorrectly() {
    SpssVariableFormat printFormat = reader.getVariables().get(0).getPrintFormat();
    assertEquals(0, printFormat.getDecimalPlaces());
    assertEquals(4, printFormat.getColumnWidth());
    assertEquals(5, printFormat.getType()); // 5 is "NUMERIC"
  }

  @Test
  public void variableWriteFormatIsReadCorrectly() {
    SpssVariableFormat writeFormat = reader.getVariables().get(0).getWriteFormat();
    assertEquals(0, writeFormat.getDecimalPlaces());
    assertEquals(4, writeFormat.getColumnWidth());
    assertEquals(5, writeFormat.getType()); // 5 is "NUMERIC"
  }
}
