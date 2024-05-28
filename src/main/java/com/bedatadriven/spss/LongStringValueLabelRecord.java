package com.bedatadriven.spss;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongStringValueLabelRecord {

    public static final int INTEGER_BYTES = 4;
    private final Map<String, Map<String, byte[]>> recordsMap = new HashMap<>();

    LongStringValueLabelRecord(final ExtendedRecordHeader header,
                               final SpssInputStream inputStream) throws IOException {
        int bytesRead = 0;

        while (bytesRead < header.getRecordCount()) {
            int varNameLen = inputStream.readInt();
            byte[] varName = inputStream.readBytes(varNameLen);
            int varWidth = inputStream.readInt();
            int labelCount = inputStream.readInt();

            Map<String, byte[]> labelsMap = new HashMap<>();
            recordsMap.put(new String(varName), labelsMap);

            for (int j = 0; j < labelCount; j++) {
                int valueLen = inputStream.readInt();
                byte[] value = inputStream.readBytes(valueLen);
                int labelLen = inputStream.readInt();
                byte[] labels = inputStream.readBytes(labelLen);
                labelsMap.put(new String(value).trim(), labels);

                bytesRead += INTEGER_BYTES + valueLen + INTEGER_BYTES +  labelLen;
            }
            bytesRead += INTEGER_BYTES * 3 + varNameLen;
        }
    }

    void parseInto(List<SpssVariable> variables, Map<String, SpssVariable> variableNames) {
        for (Map.Entry<String, SpssVariable> entry : variableNames.entrySet()) {
            if (recordsMap.containsKey(entry.getKey())) {
                entry.getValue().stringValueLabels = recordsMap.get(entry.getKey());
            }
        }
    }

}
