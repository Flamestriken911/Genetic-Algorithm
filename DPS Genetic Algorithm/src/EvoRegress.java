//Class that runs all the evo-regression stuff. Contains the main() method
import java.util.*;


class EvoRegress {








    static void main(String[] args) {

        //Initialize variables

        //Strings with path to dataset and output destination

    	String dataPath =
        "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm\\sampledata.csv";

    	String outPath =
        "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm";

    		//String containing name of objective variable
        String objectiveVar = "Objective";


            //Dictionary to contain Phenotype IDs and their associated boolean variable lists
        //HashMap<String, boolean[]> phenoTracker = new HashMap<String, boolean[]>();


            //Control variables for main while function
        boolean converged = false;
        int generation = 0;
        int maxGeneration = 2;
        double trainingProportion = 0.2;
        double battleProportion = 0.1;

            //Read in the data from the CSV into an environment
        Environment env = new Environment();
        env.getFromCSV(dataPath, true);
        env.setObjective(objectiveVar);
        env.separateObjVar();
        	//Save the number of independent variables
        int numVars = env.dataSet[0].length;



        //Get initial list of phenotypes
        int numPhenotypes = 10;
        double mutationRate = 0.5;
        int mutationDepth = 1;
        Phenotype[] genePool = new Phenotype[numPhenotypes];
            //Always have the zero-phenotype 
        boolean[] varBools = new boolean[numVars];
        for (int i=0; i<numVars; i++){
        	varBools[i] = false;
        }
        	//And the all-phenotype
        genePool[0] = new Phenotype(varBools, mutationRate, mutationDepth, false);
    	for (int i=0; i<numVars; i++){
    		varBools[i] = true;
    	}
        genePool[1] = new Phenotype(varBools, mutationRate, mutationDepth, false);
            //All other phenotypes are generated randomly
        for (int i=2; i<numPhenotypes; i++){
        	boolean[] varbools = new boolean[numVars];
            for (int j=0; j<numVars; j++){
                varbools[j] = (Math.random()<0.5);
            }
            genePool[i] = new Phenotype(varbools, mutationRate, mutationDepth, false);
        }


        //Start the genetic algorithm
            //Run until convergence or max iterations
        while (!converged && generation < maxGeneration) {

            //Create training environment
                //We start with a sample proportion of 0.2
            Environment trainEnv = new Environment();
            trainEnv.getNewSample(env.getDataSet(), trainingProportion);
            trainEnv.setObjective(objectiveVar);
            trainEnv.separateObjVar();


            //Train the phenotypes on this environment
            for (Phenotype gene : genePool){
                gene.train(trainEnv);
            }


            //Match up phenotypes for 'battle' and put them in a training environment
                //Note that boolean arrays initialize to all false values
            boolean[] hasFought = new boolean[numPhenotypes];
            int notFought = numPhenotypes;
            for (int fighter=0; fighter<numPhenotypes; fighter++){
                if (!hasFought[fighter]){
                    hasFought[fighter] = true;
                    Environment battleGround = new Environment();
                    battleGround.getNewSample(env.getDataSet(), battleProportion);
                    battleGround.setObjective(objectiveVar);
                    battleGround.separateObjVar();
                        //Generate # for which remaining unfought gene battles current gene
                    int fightNth  = (int) (Math.random() * notFought);
                    int vsIndex = 0;
                    int numPassed = 0;
                        //Find the gene to fight
                    while(numPassed<fightNth){
                        if (!hasFought[vsIndex]){
                            numPassed++;
                            if (numPassed == fightNth){
                                break;
                            }
                        }
                        vsIndex++;
                    }
                    hasFought[vsIndex] = true;

                        //Fighter fights vs gene at vsIndex unless they have same id
                    if (    !(genePool[fighter].getId() == genePool[fighter].getId()) ){
                        if (genePool[fighter].score(battleGround) > genePool[vsIndex].score(battleGround))
                        {
                            genePool[vsIndex] = genePool[fighter].reproduce();
                        } else{
                            genePool[fighter] = genePool[vsIndex].reproduce();
                        }
                    }
                }
            }
            generation++;
        }


    }


}

