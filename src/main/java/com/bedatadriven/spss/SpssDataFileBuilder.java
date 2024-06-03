package com.bedatadriven.spss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SpssDataFileBuilder {

    public static final double DEFAULT_BIAS = 100.0;
    public static final String DEFAULT_PRODUCT_NAME = "com.bedatadriven.spss 2.0";

    private String productName = DEFAULT_PRODUCT_NAME;

    private String fileLabel = "";

    private List<SpssVariable> variables = new ArrayList<>();

    private int numCases = 0;

    private int weightVariablePosition = 0;

    private double bias = DEFAULT_BIAS;

    private byte[] creationDate = new byte[] { 49, 52, 32, 74, 117, 110, 32, 49, 48 };

    private byte[] creationTime = new byte[] { 49, 52, 58, 51, 52, 58, 50, 50 };


    public SpssDataFileBuilder setProductName(String name) {
        this.productName = name;
        return this;
    }

    public SpssDataFileWriter startWriting(SpssOutputStream output) throws IOException {
        writeHeader(output);
        writeVariables(output);

        throw new UnsupportedEncodingException("TODO");
    }


    private void writeHeader(SpssOutputStream output) throws IOException {
        output.writeBytes(SpssDataFileReader.MAGIC_BYTES);
        output.writeString(productName, FileHeader.PRODUCT_NAME_LENGTH);
        output.writeInt(FileHeader.LAYOUT_3);
        output.writeInt(variables.size());
        output.writeInt(weightVariablePosition);
        output.writeInt(numCases);
        output.writeDouble(bias);
        output.writeBytes(creationDate);
        output.writeBytes(creationTime);
        output.writeString(fileLabel, FileHeader.FILE_LABEL_LENGTH);
        // Alignment
        output.writeBytes(new byte[3]);
        writeVariables(output);

        // Write termination
        output.writeInt(SpssDataFileReader.TERMINATION_RECORD_TYPE);
        output.writeBytes(new byte[4]);
    }

    private void writeVariables(SpssOutputStream output) throws IOException {
        for (SpssVariable variable : variables) {
            writeVariable(output, variable);
        }
    }

    private void writeVariable(SpssOutputStream output, SpssVariable variable) throws IOException {

        output.writeInt(SpssVariableReader.RECORD_TYPE);

        if(variable.getType() == SpssVariable.Type.NUMERIC) {
            output.writeInt(0);
        } else {
            output.writeInt(variable.getStringLength());
        }

        // TODO: Label
        output.writeInt(0);

        /* Missing Values
         *   0 = no missing values
         *   1 = 1 discrete missing value
         *   2 = 2 discrete missing values
         *   3 = 3 discrete missing values
         *  -2 = range of missing values
         *  -3 = range of missing values and one discrete missing value
         */
        // TODO
        output.writeInt(0);

        // Formats
        output.writeInt(variable.getPrintFormat().encode());
        output.writeInt(variable.getWriteFormat().encode());

        // we're assuming that the short name is always ASCII-encoded
        // but should verify...
        output.writeString(variable.getName(), SpssVariableReader.SHORT_NAME_LENGTH);
    }

}
