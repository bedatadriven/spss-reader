
# SPSS Reader

Java library to read SPSS files.

## Adding to your project

    <dependency>
       <groupId>com.bedatadriven</groupId>
       <artifactId>spss</artifactId>
       <version>1.0.1</version>
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
    
    
