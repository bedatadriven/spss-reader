package com.bedatadriven.spss;

public class SpssVariable {

    public enum Type {
        NUMERIC,
        STRING
    }

    private final String name;

    private final String label;

    private final Type type;

    private final int stringLength;

    private final SpssVariableFormat printFormat;

    private final SpssVariableFormat writeFormat;

    public SpssVariable(String name, String label, Type type, int stringLength, SpssVariableFormat printFormat, SpssVariableFormat writeFormat) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.stringLength = stringLength;
        this.printFormat = printFormat;
        this.writeFormat = writeFormat;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean hasLabel() {
        return !label.isEmpty();
    }

    public Type getType() {
        return type;
    }

    public int getStringLength() {
        return stringLength;
    }

    public SpssVariableFormat getPrintFormat() {
        return printFormat;
    }

    public SpssVariableFormat getWriteFormat() {
        return writeFormat;
    }
}
