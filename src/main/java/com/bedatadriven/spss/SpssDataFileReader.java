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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Provides read-only access to SPSS files,
 * both compressed and uncompressed.
 */
public class SpssDataFileReader {

  private SpssInputStream inputStream;

  private String encoding;

  /**
   * The current row index
   */
  int currentRowIndex;

  /**
   * Values
   */
  CaseBuffer currentCase;


  private FileHeader fileHeader;
  private VersionHeader versionInfo;
  private MissingValuesHeader missingValueHeader;

  /**
   * The way variable definitions are stored require
   * a bit of explanation...
   * <p/>
   * Record = { 1=VAR1, 2=..., 3=..., 4=VAR2, 5=VAR3, 6=... }
   * Here we call the physical position within the file the "position"
   * and the logical order as the "index"
   */
  private SpssVariable[] variableRecords;

  /**
   * This is a *logical* collection of variables.
   * <p/>
   * There may be fewer variables in this collection than
   * physical records in <code>variableRecords</code>
   */
  private List<SpssVariable> variables;


  /**
   * Map of "short" variable name to in-memory Variable data structure
   */
  private Map<String, SpssVariable> variableNames;


  private CaseReader caseReader;


  public SpssDataFileReader(String path) throws IOException {
    this(new SpssInputStream(new FileInputStream(path)));
  }

  public SpssDataFileReader(File file) throws IOException {
    this(new SpssInputStream(new FileInputStream(file)));
  }

  public SpssDataFileReader(InputStream input) throws IOException {
    this(new SpssInputStream(input));
  }

  private SpssDataFileReader(SpssInputStream inStream) throws IOException {
    inputStream = inStream;

    String fileType = new String(inputStream.readBytes(4));
    if (!"$FL2".equals(fileType)) {
      throw new IOException("Expected record type != $FL2; not an SPSS file");
    }

    fileHeader = new FileHeader(inputStream);

    variableRecords = new SpssVariable[fileHeader.getCaseSize()];
    variables = new ArrayList<>();
    variableNames = new HashMap<>();

    // we need to define something of a stack
    int recordType;
    ValueLabelsRecord currentValueLabels = null;
    int currentVariablePosition = 0;

    // read dictionary records
    do {
      recordType = inputStream.readInt();

      switch (recordType) {
        case SpssVariable.RECORD_TYPE:
          // this is some sort of continuation record...
          // it doesn't seem to contain any information
          SpssVariable variable = new SpssVariable(inputStream, currentVariablePosition, variables.size());
          if (variable.getTypeCode() != -1) {
            addVariable(variable);
          }
          currentVariablePosition++;
          break;

        case ValueLabelsRecord.RECORD_TYPE:
          currentValueLabels = new ValueLabelsRecord(inputStream);
          break;

        case 4: // apply current value label to variables
          int variableCount = inputStream.readInt();
          for (int i = 0; i < variableCount; i++) {
            int variablePosition = inputStream.readInt() - 1;  // we use a zero-based array, spss file is one-based
            variableRecords[variablePosition].valueLabels = currentValueLabels.getLabels();
          }
          break;

        case DocumentRecord.RECORD_TYPE:
          new DocumentRecord(inputStream);
          break;

        case ExtendedRecordHeader.RECORD_TYPE: // extended records
          ExtendedRecordHeader header = new ExtendedRecordHeader(inputStream);

          // NB: total length of the record is recordSize * recordCount

          switch (header.getSubType()) {
            case VersionHeader.EXTENDED_RECORD_TYPE:
              versionInfo = new VersionHeader(inputStream);
              break;

            case MissingValuesHeader.EXTENDED_RECORD_TYPE:  // Missing values codes?
              missingValueHeader = new MissingValuesHeader(inputStream);
              break;

            case LongVariableNamesRecord.EXTENDED_RECORD_TYPE: // Long (>8 bytes) explorer names
              LongVariableNamesRecord names = new LongVariableNamesRecord(header, inputStream);
              names.parseInto(variables, variableNames);
              break;

            case 20: // encoding
              encoding = new String(inputStream.readBytes(header.getTotalLength())).trim();
              break;

            //case 5:  // Variable sets
            //case 6:  // Trends
            //case 11: // Display - don't know what this
            case 14: // very long strings
              LongStringRecord r = new LongStringRecord(header, inStream);
              r.parseInto(variables, variableNames);
              break;
            //case 21: // Value Label Strings - don't know what this is
            default:
              // skip record
              inputStream.skipBytes(header.getTotalLength());
              break;
          }
          break;
        case 999: // termination record
          inputStream.skipBytes(4);
          break;
        default:
          break; // err... we shouldn't get here.
      }

    } while (recordType != 999);

    currentCase = new CaseBuffer(variables.size());

    if (!isCompressed()) {
      caseReader = new CaseReader(inputStream, variables, missingValueHeader, fileHeader.getNumCases(), currentCase);
    } else {
      caseReader = new CompressedCaseReader(inputStream, variables, missingValueHeader, fileHeader.getNumCases(), currentCase);
    }
  }

  private void addVariable(SpssVariable variable) {
    variables.add(variable);
    variableRecords[variable.getPositionInRecord()] = variable;
    variableNames.put(variable.getShortName(), variable);
  }

  public List<SpssVariable> getVariables() {
    return Collections.unmodifiableList(variables);
  }

  /**
   * @return The number of cases
   */
  public int getNumCases() {
    return fileHeader.getNumCases();
  }

  public MissingValuesHeader getMissingValuesHeader() {
    return missingValueHeader;
  }

  /**
   * @return True if this datafile is compressed, false if its uncompressed
   */
  public boolean isCompressed() {
    return fileHeader.isCompressed();
  }

  public String getVariableName(int index) {
    return inputStream.stringFromBytes(variables.get(index).longName).trim();
  }

  public int getVariableIndex(String name) {
    for (SpssVariable variable : variables) {
      if (variable.getVariableName().equals(name)) {
        return variable.getIndex();
      }
    }
    return -1;
  }

  /**
   * @return The (logical) index of the weight variable, or -1 if the datafile is not weighted
   */
  public int getWeightVariableIndex() {
    if (fileHeader.getWeightVariableRecordIndex() >= 0) {
      return variableRecords[fileHeader.getWeightVariableRecordIndex()].getIndex();
    }
    return -1;
  }


  public double getDoubleValue(String variableName) {
    return getDoubleValue(getVariableIndex(variableName));
  }

  public double getDoubleValue(int variableIndex) {
    return currentCase.getDoubleValue(variableIndex);
  }

  public String getStringValue(String variableName) {
    return getStringValue(getVariableIndex(variableName));
  }

  public String getStringValue(int variableIndex) {
    return currentCase.getStringValue(variableIndex);
  }

  public boolean isSystemMissing(String variableName) {
    return isSystemMissing(getVariableIndex(variableName));
  }

  public boolean isSystemMissing(int variableIndex) {
    return currentCase.isSystemMissing(variableIndex);
  }

  public Map<Double, String> getValueLabels(String variableName) {
    return getValueLabels(getVariableIndex(variableName));
  }

  public Map<Double, String> getValueLabels(int variableIndex) {
    Map<Double, String> labels = new HashMap<>();

    if (variables.get(variableIndex).valueLabels != null) {
      for (Map.Entry<Double, byte[]> entry : variables.get(variableIndex).valueLabels.entrySet()) {
        labels.put(entry.getKey(), new String(entry.getValue()));
      }
    }
    return labels;
  }

  public boolean isValueMissing(int variableIndex, double value) {
    return variables.get(variableIndex).isMissing(value);
  }

  public boolean readNextCase() throws IOException {
    return caseReader.readNext();
  }
}
