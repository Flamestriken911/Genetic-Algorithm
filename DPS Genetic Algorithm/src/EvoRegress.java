//Class that runs all the evo-regression stuff. Contains the main() method

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class EvoRegress {

    static void main(String[] args) {

        //Initialize variables
            //Strings with path to dataset and output destination
        String dataPath;
        String outPath;
            //String containing name of objective variable
        String objectiveVar;
            //Dictionary to contain Phenotype IDs and their associated boolean variable lists
        Map<String, boolean[]> phenoTracker = new HashMap<String, boolean[]>();
            //Control variables for main while function
        boolean converged;
        int maxIter;


        //Start the genetic algorithm
            //Run until convergence or max iterations
        while (!converged && i < maxIter) {

            //Create training environment
            Environment trainEnv = new Environment(/*args*/);

            //Train the phenotypes on this environment
                //Note that only one member of each phenotype id must be trained
            train(/*args*/);

            //Match up phenotypes for 'battle' and put them in a training environment
            matchup( /*list of un-fought phenotypes*/, /*Battle Environment*/);
        }


    }


}

