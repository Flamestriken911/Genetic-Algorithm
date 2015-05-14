//Class for reading input from CSV files

	//NOTE: Currently can't take labels with commas, or any non-numeric data


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadCSV {

    private double[][] data;    //Stores the data read from the file
    private String[] labels;    //Stores variable labels from the file, if any
    int numRows;                //Number of rows read from file (not including labels)
    int numCols;                //Number of columns (variables) in data from file
	
	
	
    //Class constructor
        //csvFile is the name of the file to read
        //hasLabels indicates whether the first row of the CSV contains labels
  public ReadCSV(String csvFile, boolean hasLabels) {
	int[] rowsCols = getNumRowsCols(csvFile);
	this.numRows = rowsCols[0];
	this.numCols = rowsCols[1];
	
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
    //We assume all (non-label) data will be type double
    int currentRow = 0;

    this.data = new double[this.numRows-1][this.numCols];
    this.labels = new String[this.numCols];

    //Attempt to read the file
	try {
 
		br = new BufferedReader(new FileReader(csvFile));
		boolean grabbedLabels = false;
		while ((line = br.readLine()) != null) {    //While there's still stuff to read...
 
			
	        // Use comma as separator between data values
			String[] rowData = line.split(cvsSplitBy);
 
            if (!(hasLabels && !grabbedLabels)) {   //If we already read any labels, or there are no labels, grab the data rows
                //Access data via rowData[column#]
                for (int currentCol=0; currentCol<rowData.length; currentCol++) {
                	this.data[currentRow][currentCol] = Double.parseDouble(rowData[currentCol]);
                	
                }
                currentRow++;
            }
            else {  //Grab labels if we haven't already
                for (int currentCol=0; currentCol<rowData.length; currentCol++) {
                	this.labels[currentCol] = rowData[currentCol];
                	grabbedLabels = true;
                }
            }


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
  

  //Method to find the number of rows and columns in a csv file
  public int[] getNumRowsCols(String csvFile) {

	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
    int[] returnNull = {0,0};

    //Attempt to read in each line
	try {
		
		br = new BufferedReader(new FileReader(csvFile));
		int numRow = 0;
        int numCol = 0;
        while ((line = br.readLine()) != null) {
 
	        // Use comma as separator
			String[] rowData = line.split(cvsSplitBy);
 
                if (numRow==0){
                	numCol = rowData.length;
                }
                numRow++;
		}
        int[] returnRowCol = {numRow, numCol};
        return returnRowCol;
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
    return returnNull;
  }

}
