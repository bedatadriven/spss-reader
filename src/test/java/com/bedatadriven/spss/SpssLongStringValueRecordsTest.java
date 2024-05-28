package com.bedatadriven.spss;

import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class SpssLongStringValueRecordsTest {

    private SpssDataFileReader reader;

    @Before
    public void setUp() throws IOException {
        InputStream is = SpssLongStringValueRecordsTest.class.getResourceAsStream("/longstringvaluerecord.sav");
        assertNotNull("InputStream", is);
        reader = new SpssDataFileReader(new DataInputStream(is));
    }

    @Test
    public void testReadValueLabels() {
        List<SpssVariable> variables = reader.getVariables();

        assertEquals(2, variables.size());

        SpssVariable variable1 = variables.get(0);
        assertNotNull(variable1.getStringValueLabels());
        assertFalse(variable1.getStringValueLabels().isEmpty());

        assertTrue(variable1.getStringValueLabels().containsKey("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals("Long Label", new String(variable1.stringValueLabels.get("ABCDEFGHIJKLMNOPQRSTUVWXYZ")));
        assertTrue(variable1.getStringValueLabels().containsKey("123456798asdf352asd4f968ad"));
        assertEquals("Longer Label", new String(variable1.stringValueLabels.get("123456798asdf352asd4f968ad")));

        SpssVariable variable2 = variables.get(1);
        assertNotNull(variable2.getStringValueLabels());
        assertFalse(variable2.getStringValueLabels().isEmpty());

        assertTrue(variable2.getStringValueLabels().containsKey("1"));
        assertEquals("Male", new String(variable2.stringValueLabels.get("1")));
        assertTrue(variable2.getStringValueLabels().containsKey("3"));
        assertEquals("Other", new String(variable2.stringValueLabels.get("3")));
        assertTrue(variable2.getStringValueLabels().containsKey("ABCDEFGHIJKLMNOPQRSTUVWZYX"));
        assertEquals("Female", new String(variable2.stringValueLabels.get("ABCDEFGHIJKLMNOPQRSTUVWZYX")));
    }

}
