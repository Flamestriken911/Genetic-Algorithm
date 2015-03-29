//Class for the 'environments' the phenotypes will be trained and tested on

class Environment {

    //Initialize variables
        //Reference to dataset to draw upon 
    public double[][] dataSet;
    private String[] labels;
    private int keptRows;
    private String objective;   
    private double[] objData;


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


    //Method to fill environment from a CSV
    public void getFromCSV(String dataFile, boolean hasLabels){
            //Create a new CSV reader to read in the data
        ReadCSV reader = new ReadCSV(dataFile, hasLabels);
            //Move the data into this environment
        this.getNewSample(reader.getData(), 1.0);
            //Keep array of labels for the columns if applicable
        if (hasLabels){
            this.setLabels(reader.getLabels());
        }
    }
    

    //Method returning the 'dataSet' property
    public double[][] getDataSet() {
    	return this.dataSet;
    }
    //Method to return the number of kept rows
    public int getNumRows(){
        return this.keptRows;
    }


    //Method to set the name of the objective variable
    public void setObjVar(String newObjVar){
        this.objective = newObjVar;
    }


    //Method to separate out the objective var from the other variables
    public void separateObjVar(){
        this.setObjData(this.getColByName(this.objective));
        int objCol = this.findColByName(this.objective);
            //New dataset will have one fewer column than original
        double[][] otherData = new double[this.dataSet.length][this.dataSet[0].length-1];
        String[] otherLabels = new String[this.labels.length-1];
        	//remove objective var column from dataset and add to objData
        int currentCol=0;
        for (int i=0; i<this.dataSet.length; i++){
        	currentCol=0;
        	for (int j=0; j<this.dataSet[0].length; j++){
	        	if(j != objCol) {
	                otherData[i][currentCol] = this.dataSet[i][j];
	                currentCol++;
	            } else {
	            	this.objData[i] = this.dataSet[i][j];
	            }
	        }
        }
    	//remove objective var from labels
        currentCol=0;
        for (int i=0; i<this.labels.length; i++){
        	if(i != objCol) {
                otherLabels[currentCol] = this.labels[i];
                currentCol++;
            }
        }
        this.dataSet = otherData;
        this.labels = otherLabels;
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
    public double[] getColByName(String colName){
        return this.getColumn(this.findColByName(colName));
    }


    //Methods to set and get the labels property of the environment
    public void setLabels(String[] newLabels){
        this.labels = newLabels;
    }
    public String[] getLabels(){
        return this.labels;
    }


	public double[] getObjData() {
		return objData;
	}
	public void setObjData(double[] objData) {
		this.objData = objData;
	}
	public String getObjective() {
		return this.objective;
	}
	public void setObjective(String newObj) {
		this.objective = newObj;
	}
    
}
