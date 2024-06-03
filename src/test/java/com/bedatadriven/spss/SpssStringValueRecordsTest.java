package com.bedatadriven.spss;

import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class SpssStringValueRecordsTest {

    private SpssDataFileReader reader;

    @Before
    public void setUp() throws IOException {
        InputStream is = SpssStringValueRecordsTest.class.getResourceAsStream("/stringvaluerecord.sav");
        assertNotNull("InputStream", is);
        reader = new SpssDataFileReader(new DataInputStream(is));
    }

    @Test
    public void testReadValueLabels() {
        List<SpssVariable> variables = reader.getVariables();
        assertEquals(15, variables.size());

        Map<String, SpssVariable> variableMap = new HashMap<>();
        for (SpssVariable variable : variables) {
            variableMap.put(variable.getVariableName(), variable);
        }

        SpssVariable q1 = variableMap.get("Q1");
        assertNotNull(q1.getValueLabels());
        assertFalse(q1.getValueLabels().isEmpty());

        assertTrue(q1.getValueLabels().containsKey(1.0));
        assertEquals("Male", q1.getValueLabels().get(1.0));
        assertTrue(q1.getValueLabels().containsKey(2.0));
        assertEquals("Female", q1.getValueLabels().get(2.0));
        
        SpssVariable q2 = variableMap.get("Q2");
        assertNotNull(q2.getValueLabels());
        assertFalse(q2.getValueLabels().isEmpty());

        assertTrue(q2.getValueLabels().containsKey(1.0));
        assertEquals("18 -25", q2.getValueLabels().get(1.0));
        assertTrue(q2.getValueLabels().containsKey(2.0));
        assertEquals("26 - 30", q2.getValueLabels().get(2.0));
        assertTrue(q2.getValueLabels().containsKey(3.0));
        assertEquals("31 - 40", q2.getValueLabels().get(3.0));
        assertTrue(q2.getValueLabels().containsKey(4.0));
        assertEquals("41 - 50", q2.getValueLabels().get(4.0));
        assertTrue(q2.getValueLabels().containsKey(5.0));
        assertEquals("51 - 70", q2.getValueLabels().get(5.0));

        for (int i = 1; i <= 5; i++) {
            SpssVariable q3Choice = variableMap.get("Q3M" + i);
            assertNotNull(q3Choice.getStringValueLabels());
            assertFalse(q3Choice.getStringValueLabels().isEmpty());

            assertTrue(q3Choice.getStringValueLabels().containsKey("CM"));
            assertEquals("Camera", q3Choice.getStringValueLabels().get("CM"));
            assertTrue(q3Choice.getStringValueLabels().containsKey("SS"));
            assertEquals("Screen Size", q3Choice.getStringValueLabels().get("SS"));
            assertTrue(q3Choice.getStringValueLabels().containsKey("RM"));
            assertEquals("RAM", q3Choice.getStringValueLabels().get("RM"));
            assertTrue(q3Choice.getStringValueLabels().containsKey("BL"));
            assertEquals("Battery Life", q3Choice.getStringValueLabels().get("BL"));
            assertTrue(q3Choice.getStringValueLabels().containsKey("PR"));
            assertEquals("Processor", q3Choice.getStringValueLabels().get("PR"));
        }
    }

}
