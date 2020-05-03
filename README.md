[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bedatadriven.spss/spss-reader/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bedatadriven.spss/spss-reader)

# SPSS Reader

Java library to read SPSS files.

## Adding to your project

    <dependency>
      <groupId>com.bedatadriven.spss</groupId>
      <artifactId>spss-reader</artifactId>
      <version>1.3</version>
    </dependency>
    
## Reading an SPSS File
 
    SpssDataFileReader reader = new SpssDataFileReader("mydata.sav");
    
    // Print variables present in the file
    for(SpssVariable variable : reader.getVariables()) {
        System.out.println(variable.getLabel());
    }
    
    // Read the cases
    while(reader.readNextCase()) {
        double var0 = reader.getDoubleValue(0);
        String var1 = reader.getStringValue(1);
    }
    
## Changelog

### 1.3 - 2020-02-04

- Support for very long strings [@ElmervcElmervc](https://github.com/Elmervc).
- Support for extended number of cases record [@ElmervcElmervc](https://github.com/Elmervc).
- Support for write and print formats [@gdecaso](https://github.com/gdecaso).

