//Class for the 'environments' the phenotypes will be trained and tested on

class Environment {

    //Initialize variables
        //Reference to dataset to draw upon 
    private double[][] dataSet;
    private String[] labels;
    private int keptRows;
    

    //Method to fill the environment with a new sample of some dataset
    	//'proportion' is the approximate proportion of dataset to keep
    public void getNewSample(double[][] data, double proportion) {
       	//If the proportion is 1, then dataSet is the full array provided
    	if (proportion==1.0){
    		this.dataSet = data;
    		this.keptRows = data.length;
    	}
    	//If proportion <1, then only keep a random subset of that dataset
    	else {
	    	//Make array of booleans to decide which rows to keep
	    	int dataRows = data.length;
	    		//Track exact number of the remaining rows
	    	this.keptRows = 0;
	    	boolean[] keepList = new boolean[dataRows];
	    	for (int i=0; i<dataRows; i++) {
	    		keepList[i] = (false);
	    		if(Math.random() < proportion) {
	        		keepList[i] = (true);
	        		this.keptRows++;
	    		}
	    	}
	    	//Put kept rows into the dataSet property
	    	this.dataSet = new double[keptRows][data[0].length];
	    	int dataSetRow = 0;
	    	for (int i=0; i<dataRows; i++){
	    		if (keepList[i]){
	    			this.dataSet[dataSetRow] = data[i];
	    			dataSetRow++;
	    		}
	    	}
    	}
    }

    
    //Method returning the 'dataSet' property
    public double[][] getDataSet() {
    	return this.dataSet;
    }


    //Method to get a column in a flat array
    public double[] getColumn(int colNum){
        double[] returnColumn = new double[this.keptRows];
        for (int i=0; i<this.keptRows; i++){
            returnColumn[i] = dataSet[i][colNum];
        }
        return returnColumn;
    }
    

    //Find the column number for the variable named in the function argument
    public int findColByName(String varName){
        int i;
    	for (i=0; i<labels.length; i++){
            if(varName.equals(labels[i])) break;
        }
        return i;
    }

    //Method to get flat column for named variable
    double[] getNamedCol(String colName){
        return this.getColumn(this.findColByName(colName));
    }


    //Methods to set and get the labels property of the environment
    public void setLabels(String[] newLabels){
        this.labels = newLabels;
    }
    public String[] getLabels(){
        return this.labels;
    }
    
}


