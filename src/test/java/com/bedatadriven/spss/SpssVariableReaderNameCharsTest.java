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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SpssVariableReaderNameCharsTest {
	private SpssDataFileReader reader;

	@Before
	public void openReader() throws IOException {
		InputStream is = SpssDataFileReaderTest.class.getResourceAsStream("/intel.sav");
		assertNotNull("InputStream", is);
		reader = new SpssDataFileReader(new DataInputStream(is));
	}

	@Test
	public void nameIsCorrect() {
		String name = reader.getVariables().get(28).getVariableName();  
		
		assertThat(name, equalTo("@2.Onthecomputeronscreendemosmonitortoppersetc\u00A0"));
	}
}
