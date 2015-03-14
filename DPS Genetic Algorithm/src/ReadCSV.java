//Class for reading input from CSV files

	//NOTE: Still need to add automatically finding number of rows in the CSV file, to avoid requiring user input for numRows
		//Probably best allowing numRows to be an optional argument; if it isn't provided, the program finds numRows itself


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadCSV {

    private double[][] data;
    private String[] labels;
	
	
	
  //Use boolean array to determine variables to keep, and a float in (0,1] for option to keep only a proportion of rows
  //hasLabels indicates whether the first row of the CSV contains labels
  public void getData(String csvFile, int numRows, boolean[] colsToKeep, double rowChance, boolean hasLabels) {
 
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
    //For now, assume all data will be type double
    int rowNum = 0;
    int numCols = 0;
    for (boolean col : colsToKeep){
    	if(col){
    		numCols++;
    	};
    };
    this.data = new double[numRows][numCols];
    this.labels = new String[numCols];

    
    //Attempt to read in each line with probability P=(rowChance)
	try {
 
		br = new BufferedReader(new FileReader(csvFile));
		
		while ((line = br.readLine()) != null) {
 
	        // Use comma as separator
			String[] rowData = line.split(cvsSplitBy);
 
            if (!hasLabels || rowNum >0) {
                //Access data via rowData[column#]
                for (int colNum=0; colNum<rowData.length; colNum++) {
                	this.data[rowNum][colNum] = Double.parseDouble(rowData[colNum]);
                }
            }
            else {
                for (int colNum=0; colNum<rowData.length; colNum++) {
                	this.labels[colNum] = rowData[colNum];
                }
            }

            rowNum++;

		}
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

  }

  
  //Getter methods for data and labels
  public double[][] getData(){
	  return data;
  };
  public String[] getLabels(){
	  return labels;
  };
  
}
