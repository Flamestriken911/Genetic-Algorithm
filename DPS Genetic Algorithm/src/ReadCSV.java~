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
	
	
	
  //hasLabels indicates whether the first row of the CSV contains labels
  public void readFile(String csvFile, int numRows, int numCols boolean hasLabels) {
 
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
    //For now, assume all data will be type double
    int currentRow = 0;

    this.data = new double[numRows][numCols];
    this.labels = new String[numCols];

    
    //Attempt to read in each line
	try {
 
		br = new BufferedReader(new FileReader(csvFile));
		
		while ((line = br.readLine()) != null) {
 
	        // Use comma as separator
			String[] rowData = line.split(cvsSplitBy);
 
            if (currentRow >0 || !hasLabels) {
                //Access data via rowData[column#]
                for (int currentCol=0; currentCol<rowData.length; currentCol++) {
                	this.data[currentRow][currentCol] = Double.parseDouble(rowData[currentCol]);
                }
            }
            else {
                for (int currentCol=0; currentCol<rowData.length; currentCol++) {
                	this.labels[currentCol] = rowData[currentCol];
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
