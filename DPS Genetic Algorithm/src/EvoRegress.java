//Class that runs all the evo-regression stuff. Contains the main() method
import java.util.*;


class EvoRegress {








    public static void main(String[] args) {

        //Initialize variables

        //Strings with path to dataset and output destination

    	String dataPath =
    		    "C:\\Users\\Chris\\Desktop\\Chris\\Learning & Research\\School\\Spring-2015\\MATH 4779\\DPS Genetic Workspace\\Genetic-Algorithm\\DPS Genetic Algorithm\\src\\sampledata.csv";

    	String outPath =
        "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm";

    		//String containing name of objective variable
        String objectiveVar = "Obj";


            //Dictionary to contain Phenotype IDs and their associated boolean variable lists
        //HashMap<String, boolean[]> phenoTracker = new HashMap<String, boolean[]>();


            //Control variables for main while function
        boolean converged = false;
        int generation = 0;
        int maxGeneration = 4;
        double trainingProportion = 0.9;
        double battleProportion = 0.8;

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
        boolean[] noBools = new boolean[numVars];	//No need to initialize since booleans default to false
        genePool[0] = new Phenotype(noBools, mutationRate, mutationDepth, false);
        	//And the all-phenotype
        boolean[] allBools = new boolean[numVars];
    	for (int i=0; i<numVars; i++){
    		allBools[i] = true;
    	}
        genePool[1] = new Phenotype(allBools, mutationRate, mutationDepth, false);
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
            System.out.println("Generation "+generation);

            //Create training environment
                //We start with a sample proportion of 0.4
            Environment trainEnv = new Environment();
            trainEnv.setObjective(objectiveVar);
            trainEnv.getNewSample(env.getDataSet(), env.getObjData(), env.getLabels(), trainingProportion);
            

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
                    notFought--;
                        //Generate # for which remaining unfought gene battles current gene
                    int fightNth  = (int) (Math.random() * notFought);
                    int vsIndex = fighter;
                    int numPassed = 0;
                        //Find the gene to fight
                    while(vsIndex<numPhenotypes){
                        if (!hasFought[vsIndex]){
                            if (numPassed == fightNth){
                                break;
                            }
                            numPassed++;
                        }
                        vsIndex++;
                    }
                    hasFought[vsIndex] = true;
                    notFought--;
                    System.out.println(fighter+" vs "+vsIndex);
	                    //Fighter fights vs gene at vsIndex unless they have same id
	                if (    !(genePool[fighter].getId().equals(genePool[vsIndex].getId()))){
	                		//Create a 'battleground' to rate the r2 values for each fighting phenotype
	                    Environment battleGround = new Environment();
	                    battleGround.setObjective(objectiveVar);
	                    battleGround.getNewSample(env.getDataSet(), env.getObjData(), env.getLabels(), battleProportion);
	                    	//Score the fighters and compare
	                    if (genePool[fighter].score(battleGround) > genePool[vsIndex].score(battleGround))
	                    {
	                    	System.out.println("#"+fighter+" wins with "+genePool[fighter].score(battleGround)+" vs "+genePool[vsIndex].score(battleGround)+"!");
	                    	genePool[vsIndex] = genePool[fighter].reproduce();
	                    	if(genePool[vsIndex].getIsMutant()){
	                    		System.out.println("This  : "+genePool[fighter].getId());
	                    		System.out.println("Became: "+genePool[vsIndex].getId());
	                    	}
	                    } else{
	                    	System.out.println("#"+vsIndex+" wins with "+genePool[vsIndex].score(battleGround)+" vs "+genePool[fighter].score(battleGround)+"!");
	                        genePool[fighter] = genePool[vsIndex].reproduce();
	                    	if(genePool[fighter].getIsMutant()){
	                    		System.out.println("This  : "+genePool[vsIndex].getId());
	                    		System.out.println("Became: "+genePool[fighter].getId());
	                    	}
	                    }
	                }
	                else System.out.println("They were identical and didn't fight");
	                	//If all phenotypes have fought, start the new generation
	                if(notFought==0){
                    	break;
                    }

                    
                }
            }
            generation++;
        }




    }


}

