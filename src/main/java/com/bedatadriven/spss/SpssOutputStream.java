package com.bedatadriven.spss;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class SpssOutputStream {

    private DataOutputStream file;
    private boolean needToFlipBytes;
    private Charset charset = Charset.forName("Cp1252");

    public SpssOutputStream(OutputStream is) {
        file = new DataOutputStream(is);
    }

    public DataOutputStream getFile() {
        return file;
    }

    public void setFile(DataOutputStream file) {
        this.file = file;
    }

    public boolean isNeedToFlipBytes() {
        return needToFlipBytes;
    }

    public void setNeedToFlipBytes(boolean needToFlipBytes) {
        this.needToFlipBytes = needToFlipBytes;
    }

    public void writeBytes(byte[] bytes) throws IOException {
        file.write(bytes);
    }

    public void writeString(String string, int maxLength) throws IOException {
        byte[] bytes = string.getBytes(charset);
        byte[] padded = Arrays.copyOf(bytes, maxLength);
        Arrays.fill(bytes, bytes.length, maxLength, (byte)' ');
        file.write(padded);
    }

    public void writeDouble(double value) throws IOException {
        long l = Double.doubleToRawLongBits(value);
        l = Long.reverseBytes(l);
        file.writeLong(l);
    }

    public void writeInt(int value) throws IOException {
        file.write(Integer.reverseBytes(value));
    }
}
