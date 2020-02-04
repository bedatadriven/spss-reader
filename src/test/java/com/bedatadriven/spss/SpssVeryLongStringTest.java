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

public class SpssVeryLongStringTest {

  private SpssDataFileReader reader;


  @Before
  public void setUp() throws IOException {
    InputStream is = SpssVeryLongStringTest.class.getResourceAsStream("/verylongstring.sav");
    assertNotNull("InputStream", is);
    reader = new SpssDataFileReader(new DataInputStream(is));
  }

  @Test
  public void testReadHeader() throws IOException {

    assertEquals("variable count", 11, reader.getVariables().size());
    assertEquals("case count", 2, reader.getNumCases());
    assertEquals("compressed", true, reader.isCompressed());

    assertEquals("some_num1", reader.getVariableName(0));
    assertEquals("some_very_long_string", reader.getVariableName(2));
    assertEquals("some_num2", reader.getVariableName(10));
  }

  @Test
  public void testVeryLongString() throws IOException {

    assertTrue("read first case", reader.readNextCase());

    assertEquals(1, (int)reader.getDoubleValue(0));
    assertEquals("Some short text", reader.getStringValue(1));

    assertFalse(reader.isVeryLongString(0));
    assertFalse(reader.isVeryLongStringSegment(0));
    assertFalse(reader.isVeryLongString(1));
    assertFalse(reader.isVeryLongStringSegment(1));
    
    assertTrue(reader.isVeryLongString(2));
    
    for(int i = 3; i < 10; i++) {
      assertFalse(reader.isVeryLongString(i));
      assertTrue(reader.isVeryLongStringSegment(i));
    }
    
    //small test for encoding, apostrophe should be in the string when read as UTF-8 
    assertTrue("The encoding of the reader doesn't seem to be set to UTF-8, which the document specifies", reader.getVeryLongStringValue(2).contains("string’s"));
    
    
    assertEquals(1845, reader.getVeryLongStringValue(2).length());
    assertTrue(reader.getVeryLongStringValue(2).startsWith("This string counts 1845 characters"));
    assertTrue(reader.getVeryLongStringValue(2).endsWith("are unused."));
    //test merge positions
    assertTrue(reader.getVeryLongStringValue(2).contains("called a very long string, as a collection of strings"));
    assertTrue(reader.getVeryLongStringValue(2).contains("very long string with a width of"));
    
    
    assertFalse(reader.isVeryLongString(10));
    assertFalse(reader.isVeryLongStringSegment(10));
    assertEquals(2, (int)reader.getDoubleValue(10));

    reader.readNextCase();

    assertEquals(2, (int)reader.getDoubleValue(0));
    assertEquals("This case has an empty very long string", reader.getStringValue(1));

    assertFalse(reader.isVeryLongString(0));
    assertFalse(reader.isVeryLongString(1));

    assertTrue(reader.isVeryLongString(2));

    assertFalse(reader.isVeryLongString(3));
    
    assertFalse(reader.isVeryLongString(10));

    assertEquals(0 ,reader.getVeryLongStringValue(2).length());

    assertEquals(3, (int)reader.getDoubleValue(10));

  }
}
