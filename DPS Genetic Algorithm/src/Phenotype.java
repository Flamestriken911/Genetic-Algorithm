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
    private OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();


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


        this.numVars = 0;
        for (boolean var : varSelection){
            if(var) this.numVars++;
        }
        this.id = Arrays.toString(this.varSelection);
    }

    
//Code for reproduce method
    //Returns a new copy of itself, with possibility of mutation
    public Phenotype reproduce() {
        Phenotype child = new Phenotype(varSelection, mutationRate, mutationDepth, true);
        return child;
    }


    //Code for mutate method
    private void mutate() {
        //Randomly select number of mutations to make
        int numMutations = (int) ( Math.random() * mutationDepth + 1 );
        int i;
        //Make selected number of changes
        //Note that currently a change can be made and then be un-made by the subsequent change within a single 'round' of mutation
        for ( i=1; i<=numMutations; i++) {
            //Randomly select an index to change, then switch its value
            int mutateIndex = (int) ( Math.random() * varSelection.length );
            //We use xor to cleanly switch the boolean
            varSelection[mutateIndex] ^= true;
        }
    }


    //Getter methods
        //ID
    public String getId() {
        id = Arrays.toString(varSelection);
        return id;
    }
        //Mutation rate
    public double getMutationRate() {
        return mutationRate;
    }
        //Mutation depth
    public int getMutationDepth() {
        return mutationDepth;
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
    public void train(Environment env){
        //Get the y[] and x[][] data from the environment
        double[] Y = env.getObjData();
        //Keep only the necessary columns for X
        double[][] X = new double[Y.length][this.numVars];
        int currentCol = 0;
        int i=0;
        while(currentCol<numVars){
            if(varCoeffs[i]){
                X[currentCol] = env.dataSet[i];
                currentCol++;
            }
            i++;
        }

        this.reg.newSampleData(Y, X);
        this.varCoeffs = this.reg.estimateRegressionParameters();
    }

    public double score(Environment env){
        //Get the y[] and x[][] data from the environment
        double[] Y = env.getObjData();
        //Keep only the necessary columns for X
        double[][] X = new double[Y.length][this.numVars];
        int currentCol = 0;
        int i=0;
        while(currentCol<numVars){
            if(varCoeffs[i]){
                X[currentCol] = env.dataSet[i];
                currentCol++;
            }
            i++;
        }
        this.reg.newSampleData(Y, X);
        return reg.calculateAdjustedRSquared();
    }

}


