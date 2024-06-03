package com.bedatadriven.spss;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SpssDataFileWriter {


    private final SpssOutputStream outStream;

    SpssDataFileWriter(SpssOutputStream outStream) throws IOException {
        this.outStream = outStream;
    }

}
