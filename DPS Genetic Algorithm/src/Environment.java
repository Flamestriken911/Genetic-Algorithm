//Class for the 'environments' the phenotypes will be trained and tested on

class Environment {

    //Initialize variables
        //Reference to dataset to draw upon 
    private double[][] dataSet;
    

    //Method to fill the environment with a new sample of some dataset
    	//'proportion' is the approximate proportion of dataset to keep
    public void getNewSample(double[][] data, double proportion) {
       	//If the proportion is 1, then dataSet is the full array provided
    	if (proportion==1){
    		this.dataSet = data;
    	}
    	//If proportion <1, then only keep a random subset of that dataset
    	else {
	    	//Make array of booleans to decide which rows to keep
	    	int dataRows = data.length;
	    		//Track exact number of the remaining rows
	    	int keptRows = 0;
	    	boolean[] keepList = new boolean[dataRows];
	    	for (int i=0; i<dataRows; i++) {
	    		keepList[i] = (false);
	    		if(Math.random() < proportion) {
	        		keepList[i] = (true);
	        		keptRows++;
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
}


