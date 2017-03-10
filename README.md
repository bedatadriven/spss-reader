[![Build Status](https://jenkins.bedatadriven.com/view/All/job/spss-reader/badge/icon)](https://jenkins.bedatadriven.com/view/All/job/spss-reader/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bedatadriven.spss/spss-reader/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bedatadriven.spss/spss-reader)


# SPSS Reader

Java library to read SPSS files.

## Adding to your project

    <dependency>
       <groupId>com.bedatadriven</groupId>
       <artifactId>spss</artifactId>
       <version>1.2</version>
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
    
    
