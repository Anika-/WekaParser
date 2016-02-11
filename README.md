# WekaParser

This code, receives already transformed data, originated by Solr, and transforms it into input for Weka.
In this sample code, the input and output path are specified inside the Main() routine, the file "A1.xml" is the input, 
while the line 
->>>> parserxml.writeHeader(header, "Files\\A1-CompleteAbstractpreprocessedV1.0.csv");
represents the path to the result file, it should be a .csv

The routine inside Main(), is hard coded and part of a proof of concept, it is important to realize that it has to be 
improoved and automated.
