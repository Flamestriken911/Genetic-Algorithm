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
        int maxIter = 2;
        int iter = 0;

            //Read in the data from the CSV
        ReadCSV reader = new ReadCSV();
            int numRows = 8;
            boolean[] colsToKeep = {true, true, true, true, true};
            double rowChance = 1.0;
            boolean hasLabels = true;
        reader.readFile(dataPath, numRows, colsToKeep, rowChance, hasLabels);


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
        while (!converged && iter < maxIter) {

            //Create training environment
                //We start with a sample proportion of 0.2
            Environment trainEnv = new Environment();
            trainEnv.getNewSample(reader.getData(),.2)

            //Train the phenotypes on this environment
            train(/*args*/);

            //Match up phenotypes for 'battle' and put them in a training environment
            matchup( /*list of un-fought phenotypes*/, /*Battle Environment*/);
        }


    }


}

