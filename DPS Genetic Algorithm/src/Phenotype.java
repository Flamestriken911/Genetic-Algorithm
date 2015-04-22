//Code for the 'phenotypes' of the evolutionary algorithm
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;


class Phenotype {

    private boolean[] varSelection;
    private String id;
    //mutationRate is frequency of mutations and mutationDepth is max number of possible changes per mutation
    private double mutationRate;
    private int mutationDepth;
    private boolean isMutant = false;
    private int numVars;
    private double[] varCoeffs;
    public OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();


//Class constructor
        //Init vars and mutate
    public Phenotype(boolean[] varBools, double mutRate, int mutDepth, boolean enableMutate) {

        this.varSelection = varBools;
        this.mutationRate = mutRate;
        this.mutationDepth = mutDepth;


        //Mutate if enabled & triggered, and track whether it is a mutant
        if (enableMutate && Math.random() < mutationRate) {
            this.mutate();
            this.isMutant = true;
        }


        //Keep track of how many columns we have
        this.numVars=0;
        int varTracker = 0;
        for (int i=0; i<varSelection.length; i++){
            if(varSelection[i]==true) {
            	varTracker++;
            }
        }
        this.numVars = varTracker;
        
        //update ID
        this.id = "";
        for(int i=0; i<this.varSelection.length; i++){
        	if(this.varSelection[i]){
        		this.id +="1";
        	}
        	else{
        		this.id +="0";
        	}
        }
    }

    
//Code for reproduce method
    //Returns a new copy of itself, with possibility of mutation
    public Phenotype reproduce() {
    	boolean[] childVarSelection = Arrays.copyOf(this.varSelection, this.varSelection.length);
        Phenotype child = new Phenotype(childVarSelection, mutationRate, mutationDepth, true);
        return child;
    }


    //Code for mutate method
    private void mutate() {
        //Randomly select number of mutations to make
        int numMutations = (int) ( Math.random() * mutationDepth + 1 );
        //Make selected number of changes
        //Note that currently a change can be made and then be un-made by the subsequent change within a single 'round' of mutation
        for (int i=0; i<numMutations; i++) {
            //Randomly select an index to change, then switch its value
            int mutateIndex = (int) ( Math.random() * varSelection.length );
            //We use xor to cleanly switch the boolean
            varSelection[mutateIndex] ^= true;
        }
    }


    //Getter methods
        //ID
    public String getId() {
        return this.id;
    }
        //Mutation rate
    public double getMutationRate() {
        return mutationRate;
    }
        //Mutation depth
    public int getMutationDepth() {
        return mutationDepth;
    }
    //Number of selected independent variables
public int getNumVars() {
    return numVars;
}
        //Variable selection
    public boolean[] getVarSelection() {
        return varSelection;
    }
        //Mutant indicator
    public boolean getIsMutant() {
        return isMutant;
    }


    //Setter methods
        //ID
    public void setId(String newID) {
        id = newID;
    }
        //Mutation rate
    public void setMutationRate(double newMutationRate) {
        mutationRate = newMutationRate;
    }
        //Mutation depth
    public void setMutationDepth(int newMutationDepth) {
        mutationDepth = newMutationDepth;
    }
        //Variable selection
    public void setVarSelection(boolean[] newVarSelection) {
        varSelection = newVarSelection;
    }
        //Mutant indicator
    public void setIsMutant(boolean newIsMutant) {
        isMutant = newIsMutant;
    }


    //Train the phenotype on the provided environment
    public double[] train(Environment env){
        //Get the y[] and x[][] data from the environment
        double[] Y = env.getObjData();
        //Keep only the necessary columns for X
        double[][] X = new double[Y.length][this.numVars];
        int i=0;
        for(int currentCol=0; currentCol<this.varSelection.length; currentCol++){
            if(varSelection[currentCol]){
            	double[] dataCol = env.getColumn(currentCol);
            	for (int j=0; j<Y.length; j++){
            		X[j][i] = dataCol[j];
            	}
                i++;
                if(i>this.numVars) break;
            }
        }
        this.reg.newSampleData(Y, X);
        this.varCoeffs = this.reg.estimateRegressionParameters();
        //Now replace the data with 'null' data to reduce memory demands for idle phenotypes
        double nullY[] = {0,0};
        double nullX[][] = {{1},{0}};
        this.reg.newSampleData(nullY,nullX);
        //Get an array with the coefficients in the right spots
        double coeffsArray[] = new double[this.numVars];
        int numAdded = 0;
        for (i=0; i<coeffsArray.length; i++){
        	if(this.varSelection[i]){
        		coeffsArray[i] = this.varCoeffs[numAdded];
        		numAdded++;
        	}
        	else{
        		coeffsArray[i] = 0;
        	}
        }
        return coeffsArray;
    }

    public double score(Environment env){
        //Get the y[] and x[][] data from the environment
        double[] Y = env.getObjData();
        //Keep only the necessary columns for X
        double[][] X = new double[Y.length][this.numVars];
        int i=0;
        for(int currentCol=0; currentCol<this.varSelection.length; currentCol++){
            if(varSelection[currentCol]){
            	double[] dataCol = env.getColumn(currentCol);
            	for (int j=0; j<Y.length; j++){
            		X[j][i] = dataCol[j];
            	}
                i++;
                if(i>this.numVars) break;
            }
        }
        this.reg.newSampleData(Y, X);
        double score = reg.calculateAdjustedRSquared();
        //Now replace the data with 'null' data to reduce memory demands for idle phenotypes
        double nullY[] = {0,0};
        double nullX[][] = {{1},{0}};
        this.reg.newSampleData(nullY,nullX);
        return score;
    }

}















