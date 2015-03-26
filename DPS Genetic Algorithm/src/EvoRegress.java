//Class that runs all the evo-regression stuff. Contains the main() method
import java.util.*;


class EvoRegress {






        //Initialize variables

            //Strings with path to dataset and output destination

        String dataPath =
            "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm\\sampledata.csv";

        String outPath =
            "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm";


    static void main(String[] args) {

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
        env.getFromCSV(dataFile, true);
        env.objective = objectiveVar;
        env.separateObjVar();



        //Get initial list of phenotypes
        int numPhenotypes = 10;
        double mutationRate = 0.5;
        int mutationDepth = 1;
        Phenotype[] genePool = new Phenotype[numPhenotypes];
            //Always have the zero-phenotype and the all-phenotype
            genePool[0] = new Phenotype({false, false, false, false}, mutationRate, mutationDepth, 0);
            genePool[1] = new Phenotype({true, true, true, true}, mutationRate, mutationDepth, 0);
            //All other phenotypes are generated randomly
            for (int i=2; i<numPhenotypes; i++){
                boolean[] varbools = new boolean[colsToKeep-1];
                for (int j=0; j<colsToKeep; j++){
                    varbools[j] = (Math.random()<0.5);
                }
                genePool[i] = new Phenotype(varbools, mutationRate, mutationDepth, 0);
            }


        //Start the genetic algorithm
            //Run until convergence or max iterations
        while (!converged && generation < maxGeneration) {

            //Create training environment
                //We start with a sample proportion of 0.2
            Environment trainEnv = new Environment();
            trainEnv.getNewSample(env.getDataSet(), trainingProportion);
            trainEnv.objective = objectiveVar;
            trainEnv.separateObjVar();


            //Train the phenotypes on this environment
            for (Phenotype gene : genePool){
                gene.train(trainEnv);
            }


            //Match up phenotypes for 'battle' and put them in a training environment
                //Note that boolean arrays initialize to all false values
            boolean[] hasFought = new boolean[numPhenotypes];
            int notFought = numPhenotypes;
            for (int fighter=0; i<numPhenotypes; i++){
                if (!hasFought[fighter]){
                    hasFought[fighter] = true;
                    Environment battleGround = new Environment();
                    battleGround.getNewSample(env.getDataSet(), battleProportion);
                    battleGround.objective = objectiveVar;
                    battleGround.separateObjVar();
                        //Generate # for which remaining unfought gene battles current gene
                    int fightNth  = (Math.random() * notFought) -1;
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
                    if (    !(genePool[fighter].id == genePool[fighter].id) ){
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

