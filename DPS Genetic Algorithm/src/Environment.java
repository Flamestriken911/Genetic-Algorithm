//class for the 'environments' (subsets of data) on which the phenotypes will be trained and tested

class environment {

    //initialize variables
    public double[][] dataset;          //the raw data
    private string[] labels;            //names of the variables in the data
    private int keptrows;               //number of rows kept when subsetting data
    private string objective;           //name of the dependent variable
    private double[] objdata;           //data column of the dependent variable
    private double keepproportion=1;    //proportion of the data to keep when subsetting data


    //method to fill the environment with a new sample of some dataset
    public void getnewsample(double[][] data, double proportion) {
       	this.keepproportion = proportion;
    	//if the proportion is 1, then dataset is the full array provided
    	if (this.keepproportion==1.0){
    		this.dataset = data;
    		this.keptrows = data.length;
    	}
    	//if proportion is less than 1, then only keep a random subset of that dataset
    	else {
	    	//make array of booleans to decide which rows to keep
	    	int datarows = data.length;
	    		//track exact number of rows kept
	    	this.keptrows = 0;
	    	boolean[] keeplist = new boolean[datarows];
	    	for (int i=0; i<datarows; i++) {
	    		keeplist[i] = (false);
	    		if(math.random() < this.keepproportion) {
	        		keeplist[i] = (true);
	        		this.keptrows++;
	    		}
	    	}
	    	//put kept rows into the dataset property
	    	this.dataset = new double[keptrows][data[0].length];
	    	int datasetrow = 0;
	    	for (int i=0; i<datarows; i++){
	    		if (keeplist[i]){
	    			this.dataset[datasetrow] = data[i];
	    			datasetrow++;
	    		}
	    	}
    	}
    }


  //method to fill the environment with a new sample of some dataset, with objective variable column and labels
  //'proportion' is the approximate proportion of dataset to keep
  public void getnewsample(double[][] data, double[] objectivedata, string[] labels, double proportion) {
  	//if the proportion is 1, then dataset is the full array provided
  if (proportion==1.0){
  	this.dataset = data;
  	this.objdata = objectivedata;
  	this.labels = labels;
  	this.keptrows = data.length;
  }
  //if proportion <1, then only keep a random subset of that dataset
  else {
  	//make array of booleans to decide which rows to keep
  	int datarows = data.length;
  		//track exact number of the remaining rows
  	this.keptrows = 0;
  	boolean[] keeplist = new boolean[datarows];
  	for (int i=0; i<datarows; i++) {
  		keeplist[i] = (false);
  		if(math.random() < proportion) {
      		keeplist[i] = (true);
      		this.keptrows++;
  		}
  	}
  	//put kept rows into the dataset and objdata properties
  	this.dataset = new double[keptrows][data[0].length];
  	this.objdata = new double[keptrows];
  	int datasetrow = 0;
  	for (int i=0; i<datarows; i++){
  		if (keeplist[i]){
  			this.dataset[datasetrow] = data[i];
  			this.objdata[datasetrow] = objectivedata[i];
  			datasetrow++;
  		}
  	}
  }
  }


    //method to read in data from a csv file
    public void getfromcsv(string datafile, boolean haslabels){
            //create a new csv reader to read in the data
        readcsv reader = new readcsv(datafile, haslabels);
            //move the data into this environment
        this.getnewsample(reader.getdata(), 1.0);
            //keep array of labels for the columns if applicable
        if (haslabels){
            this.setlabels(reader.getlabels());
        }
    }
    

    //method returning the raw data stored in the environment
    public double[][] getdataset() {
    	return this.dataset;
    }
    //method to return the number of kept rows
    public int getnumrows(){
        return this.keptrows;
    }


    //method to set the name of the objective variable
    public void setobjvar(string newobjvar){
        this.objective = newobjvar;
    }


    //method to separate out the objective var from the other variables
    public void separateobjvar(){
        this.setobjdata(this.getcolbyname(this.objective));
        int objcol = this.findcolbyname(this.objective);
        removecol(objcol);    
    }


    //method to get a column in a flat array
    public double[] getcolumn(int colnum){
        double[] returncolumn = new double[this.dataset.length];
        for (int i=0; i<this.dataset.length; i++){
            returncolumn[i] = dataset[i][colnum];
        }
        return returncolumn;
    }
    

    //method to find the column number for the variable named in the function argument
    public int findcolbyname(string varname){
        int i;
    	for (i=0; i<labels.length; i++){
            if(varname.equals(labels[i])) break;
        }
        return i;
    }


    //method to get flat column for named variable
    public double[] getcolbyname(string colname){
        return this.getcolumn(this.findcolbyname(colname));
    }

    
    //method to remove a column and its label from dataset entirely
    public string removecol(int col){
    	string removedlabel = this.labels[col];
    	double[][] newdata = new double[this.dataset.length][this.dataset[0].length-1];
    	string[] newlabels = new string[this.labels.length-1];
    	int currentcol=0;
    	for (int i=0;i<this.dataset[0].length;i++){
    		if(i!=col)	{
    			for (int j=0;j<this.dataset.length;j++){
    					newdata[j][currentcol] = this.dataset[j][i];
    			}
    			newlabels[currentcol] = this.labels[i];
    			currentcol++;
    		}
    	}
    	this.dataset = newdata;
    	this.labels = newlabels;
    	return removedlabel;
    }
    
    
    //function to find probability of all 0s or all 1s in a data row
    	//will return -1 if column is non-binary
    public double calcfailrate(int col, double keeprate){
    	double[] coltocheck = this.getcolumn(col);
    	int rows = coltocheck.length;
    	double failrate = -1;
    	int num1s = 0;
    	int num0s = 0;
    	for (int i=0; i<rows; i++){
    		if(coltocheck[i]==0) num0s++;
    		else if(coltocheck[i]==1) num1s++;
    		else return -1;
    	}
    	double proball0 = (double) num0s / rows;
    	double proball1 = (double) num1s / rows;
    	failrate = math.pow(proball0, math.ceil(rows*keeprate)) + math.pow(proball1, math.ceil(rows*keeprate));
    	return failrate;
    }
    
    //function to remove columns if they are more likely to have all the same value than a given threshold
    string[] usefailurethreshold(double keeprate, double threshold){
    	string[] removedlist = new string[this.labels.length];
    	int numremoved = 0;
    	int i = this.labels.length-1;
    	while (i>=0){
    		double failrate = this.calcfailrate(i, keeprate);
    		if(failrate > threshold){
    			removedlist[numremoved] = this.removecol(i) + " : " + failrate;
    			numremoved++;
    		}
    		i--;
    	}
    	return removedlist;
    }
    

    //methods to set and get variable names
    public void setlabels(string[] newlabels){
        this.labels = newlabels;
    }
    public string[] getlabels(){
        return this.labels;
    }

    //Methods to set and get dependent variable data
	public double[] getObjData() {
		return objData;
	}
	public void setObjData(double[] objData) {
		this.objData = objData;
	}

    //Methods to set and get the name of the dependent variable
	public String getObjective() {
		return this.objective;
	}
	public void setObjective(String newObj) {
		this.objective = newObj;
	}
    
}
