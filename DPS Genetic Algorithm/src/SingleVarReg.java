//This code gets the coefficients and r2 values for single-var regressions
    //Repeated for each variable in data

class SingleVarReg {

String dataFile = "";
String objectiveVar = "";


public static void main(String[] args) {

    //Read in the data from the CSV file and put it into an environment
    ReadCSV data = new ReadCSV();
    data.readFile(dataFile, dataRows, dataCols, true); 
    Environment env = new Environment();
        //Put entire dataset into environment
    env.getNewSample(data.getData(), 1.0);
        //Keep array of labels for the columns
    String[] labels = data.getLabels();

    

}






}
