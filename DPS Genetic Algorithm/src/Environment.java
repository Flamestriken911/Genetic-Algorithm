//Class for the 'environments' the phenotypes will be trained and tested on

class Environment {

    //Initialize variables
        //Reference to dataset to draw upon 
    public double[][] dataSet;
    private String[] labels;
    private int keptRows;
    private String objective;   
    private double[] objData;
    private double keepProportion=1;


    //Method to fill the environment with a new sample of some dataset
    	//'proportion' is the approximate proportion of dataset to keep
    public void getNewSample(double[][] data, double proportion) {
       	this.keepProportion = proportion;
    	//If the proportion is 1, then dataSet is the full array provided
    	if (this.keepProportion==1.0){
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
	    		if(Math.random() < this.keepProportion) {
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
  //Method to fill the environment with a new sample of some dataset, with objective variable column and labels
  //'proportion' is the approximate proportion of dataset to keep
  public void getNewSample(double[][] data, double[] objectiveData, String[] labels, double proportion) {
  	//If the proportion is 1, then dataSet is the full array provided
  if (proportion==1.0){
  	this.dataSet = data;
  	this.objData = objectiveData;
  	this.labels = labels;
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
  	//Put kept rows into the dataSet and objData properties
  	this.dataSet = new double[keptRows][data[0].length];
  	this.objData = new double[keptRows];
  	int dataSetRow = 0;
  	for (int i=0; i<dataRows; i++){
  		if (keepList[i]){
  			this.dataSet[dataSetRow] = data[i];
  			this.objData[dataSetRow] = objectiveData[i];
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
        removeCol(objCol);    
    }


    //Method to get a column in a flat array
    public double[] getColumn(int colNum){
        double[] returnColumn = new double[this.dataSet.length];
        for (int i=0; i<this.dataSet.length; i++){
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

    
    //Method to remove a column and its label
    public String removeCol(int col){
    	String removedLabel = this.labels[col];
    	double[][] newdata = new double[this.dataSet.length][this.dataSet[0].length-1];
    	String[] newlabels = new String[this.labels.length-1];
    	int currentCol=0;
    	for (int i=0;i<this.dataSet[0].length;i++){
    		if(i!=col)	{
    			for (int j=0;j<this.dataSet.length;j++){
    					newdata[j][currentCol] = this.dataSet[j][i];
    			}
    			newlabels[currentCol] = this.labels[i];
    			currentCol++;
    		}
    	}
    	this.dataSet = newdata;
    	this.labels = newlabels;
    	return removedLabel;
    }
    
    
    //Function to find probability of all 0s or all 1s in a data row
    	//Will return -1 if column is non-binary
    public double calcFailRate(int col, double keepRate){
    	double[] colToCheck = this.getColumn(col);
    	int rows = colToCheck.length;
    	double failRate = -1;
    	int num1s = 0;
    	int num0s = 0;
    	for (int i=0; i<rows; i++){
    		if(colToCheck[i]==0) num0s++;
    		else if(colToCheck[i]==1) num1s++;
    		else return -1;
    	}
    	double probAll0 = (double) num0s / rows;
    	double probAll1 = (double) num1s / rows;
    	failRate = Math.pow(probAll0, Math.ceil(rows*keepRate)) + Math.pow(probAll1, Math.ceil(rows*keepRate));
    	return failRate;
    }
    
    //Function to remove columns if they are more likely to fail than a given threshold
    String[] useFailureThreshold(double keepRate, double threshold){
    	String[] removedList = new String[this.labels.length];
    	int numRemoved = 0;
    	int i = this.labels.length-1;
    	while (i>=0){
    		double failrate = this.calcFailRate(i, keepRate);
    		if(failrate > threshold){
    			removedList[numRemoved] = this.removeCol(i) + " : " + failrate;
    			numRemoved++;
    		}
    		i--;
    	}
    	return removedList;
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
