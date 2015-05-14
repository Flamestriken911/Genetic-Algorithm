// This is the main program for the genetic algorithm and regression project
// This program does the following:
//      1.  Read in historical data from an external comma-delimited file
//      2.  Scan columns of the data and remove any that are too poorly populated for use
//      3.  Run the genetic algorithm for a specifed number of generations
//      4.  Take the final phenotype (linear model) and perform a linear regression with it on the full supplied dataset
//      5.  Ouput the results of (4) to a file

import java.util.*;             //Library with necessary utility functions
import java.io.FileWriter;      //Library with tools to write data to files
import java.io.IOException;     //Library with tools for error handling

class EvoRegress {

    public static void main(String[] args) {



//                  The following variables can be specified by the user                            



        //The variables below hold the filepaths used by the program
            //All paths should be to csv files

    	String dataPath = ".\\Teacher-School Data\\data_teachers_run mod.csv";                                          //Historical data fed to the model
//    	String dataPath = ".\\Student-Teacher Data\\M ready HSonly ssprev npprev inc - Small - Useable - No SS.csv";    //Historical data fed to the model
    	String removedColsPath = ".\\Output\\Removed Cols.csv"; //Holds the names of columns removed due to being poorly populated
    	String outPath = ".\\Output\\generation_history.csv";	//Records the population makeup over generations
    	String regResultsPath = ".\\Output\\results.csv";       //Holds the coefficients for the final linear model output by this program

        //The variables below control the behavior of the algorithm

//      String objectiveVar = "NP";                             //String containing name of objective variable
        String objectiveVar = "retention";                      //String containing name of objective variable
       
            //Control variables for main while function
        boolean fratricide = true;                              //Whether chance for one to mutate when identical phenotypes fight
        int maxGeneration = 50;                               //The algorithm stops after this many generations
        double trainingProportion = 0.30;                       //This is the proportion of the data used to 'train' generations
        double battleProportion = 0.30;                         //This is the proportion of the data used to 'score' each phenotype
        int numPhenotypes = 1000;                               //Size of the population. Must be an even number
        double mutationRate = 0.2;                              //Probability that a phenotype will produce a mutated offspring
        int mutationDepth = 5;;                                 //Maximum number of changes from a single mutation





            //Read in the data from the CSV into an environment
        Environment env = new Environment();
        env.getFromCSV(dataPath, true);
        env.setObjective(objectiveVar);
        env.separateObjVar();
        	//Remove columns likely to be all 1s or 0s in sample matrices
        String[] failedCols = env.useFailureThreshold(Math.min(trainingProportion, battleProportion),.00001);
        System.out.println(Arrays.toString(failedCols));    //Print to console the columns that were removed
        //Output the results to a csv
        FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(removedColsPath);
			//Write the CSV file header
			fileWriter.append("Variable");
			//Add a new line separator after the header
			fileWriter.append("\n");
			
			//Write a row to the CSV file
			int k=0;
			while (failedCols[k] != null) {
				fileWriter.append(failedCols[k]);
				fileWriter.append("\n");
				k++;
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
		}

        	//Save the number of independent variables into a variable
        int numVars = env.dataSet[0].length;
        
        
        //Create initial list of phenotypes
        Phenotype[] genePool = new Phenotype[numPhenotypes];
            //All phenotypes are generated randomly
          for (int i=0; i<numPhenotypes; i++){
        	boolean[] varbools = new boolean[numVars];
            for (int j=0; j<numVars; j++){
                varbools[j] = (Math.random()<0.5);  //Every variable has a 50% chance of inclusion
            }
            genePool[i] = new Phenotype(varbools, mutationRate, mutationDepth, false);
        }

        
        //Start the genetic algorithm
            //Run until max iterations
        int generation = 0;
        while (generation < maxGeneration) {
            System.out.println("Generation "+generation);   //Print the current generation to console

            //Create training environment
            Environment trainEnv = new Environment();
            trainEnv.setObjective(objectiveVar);
            trainEnv.getNewSample(env.getDataSet(), env.getObjData(), env.getLabels(), trainingProportion); //Get a new random sample from the main dataset
            
            //Train the phenotypes on this environment
            for (Phenotype gene : genePool){
                gene.train(trainEnv);
            }
            
            //Match up phenotypes for 'battle' and put them in a training environment
                //Note that boolean arrays initialize to all false values
            boolean[] hasFought = new boolean[numPhenotypes];	//Array of boolean values of whether phenotype of that index has fought already
            int notFought = numPhenotypes;	                    //Number that haven't fought yet
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
	                    	}
	                    } else{
	                    	System.out.println("#"+vsIndex+" wins with "+genePool[vsIndex].score(battleGround)+" vs "+genePool[fighter].score(battleGround)+"!");
	                        genePool[fighter] = genePool[vsIndex].reproduce();
	                    	if(genePool[fighter].getIsMutant()){
	                    	}
	                    }
	                }
	                else{
	                	System.out.println("They were identical and didn't fight");
	                	if(fratricide)  genePool[fighter] = genePool[vsIndex].reproduce();;
	                }
	                	//If all phenotypes have fought, start the new generation
	                if(notFought==0){
                    	break;
                    }

                    
                }
            }
            
            //Get generation data ready for output
            String idList[] = new String[numPhenotypes];
            int idNums[] = new int[numPhenotypes];
            int i=0;
            int j;
            int numAdded = 0;
            String currentID;
                //For each phenotype add it to the list if it isn't already there, or increment the count if a copy is present already
            for (i=0; i<genePool.length; i++){
            	currentID = genePool[i].getId();
            	j=0;
            	while (j<i){
            		if (currentID.equals(idList[j])){
            			idNums[j] +=1;
            			break;
            		}
            		j++;
            	}
            	if (j==i){
            		idList[numAdded] = currentID;
            		idNums[numAdded] = 1;
            		numAdded++;
            	}
            }

            //Output the results to a csv
            fileWriter = null;
    		
    		try {
    			if(generation==0){
	    			fileWriter = new FileWriter(outPath);	
	    			//Write the CSV file header
	    			fileWriter.append("ID,NumberPhenotypes");
    			}else{
	    			fileWriter = new FileWriter(outPath,true);
    			}
    			//Add a new line separator after the header
    			fileWriter.append("\n");
    			//Write a row to the CSV file
    			for (i=0; i<numAdded; i++) {   				
    				fileWriter.append(String.valueOf(generation));
    				fileWriter.append(",\'");
    				fileWriter.append(idList[i]);
    				fileWriter.append("\',");
    				fileWriter.append(String.valueOf(idNums[i]));
    				fileWriter.append("\n");
    			}
    			
    			System.out.println("CSV file was created successfully !!!");
    			
    		} catch (Exception e) {
    			System.out.println("Error in CsvFileWriter !!!");
    			e.printStackTrace();
    		} finally {
    			
    			try {
    				fileWriter.flush();
    				fileWriter.close();
    			} catch (IOException e) {
    				System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
    			}
    			
    		}
            
            generation++;   //Increment the generation count
            
            //Ask if program should continue after final generation
            if(generation==maxGeneration){
	            Scanner reader = new Scanner(System.in);
	            System.out.println("Would you like to continue running? y/n:");
	            if(reader.next().equals("y")){
	            	System.out.println("How many more generations?");
		            maxGeneration += reader.nextInt();   
	            }else{
	            	reader.close();
	            }
            }            
        }

        //Get final generation data ready for output
        String idList[] = new String[numPhenotypes];
        int idNums[] = new int[numPhenotypes];
        int i=0;
        int j;
        int numAdded = 0;
        String currentID;
                //For each phenotype add it to the list if it isn't already there, or increment the count if a copy is present already
        for (i=0; i<genePool.length; i++){
        	currentID = genePool[i].getId();
        	j=0;
        	while (j<i){
        		if (currentID.equals(idList[j])){
        			idNums[j] +=1;
        			break;
        		}
        		j++;
        	}
        	if (j==i){
        		idList[numAdded] = currentID;
        		idNums[numAdded] = 1;
        		numAdded++;
        	}
        }

		//Finally, run the regression on the full available dataset for the largest phenotype
			//Then output the coefficients for each variable in a file
		//Start by finding the index of the largest phenotype
		int numOfLargest = 0;
		int maxIndex = 0;
		for (i=0; i<idNums.length; i++){
			if(numOfLargest<idNums[i]){
				numOfLargest = idNums[i];
				maxIndex = i;
			}
		}
		//Now run the regression and get the coefficients for the phenotype
		double finalCoeffs[] = genePool[maxIndex].train(env);
        double r_squared = genePool[maxIndex].score(env);
            System.out.println(r_squared);

		//Output the results to a csv
        fileWriter = null;
		
		try {
			fileWriter = new FileWriter(regResultsPath);

			//Write the CSV file header
			fileWriter.append("Intercept");	
			String[] labels = env.getLabels();
			for(i=0; i<labels.length; i++){
				fileWriter.append(",");
				fileWriter.append(labels[i]);
			}
			
			//Add a new line separator after the header
			fileWriter.append("\n");
			
			//Write coeffs to the CSV file
			fileWriter.append(String.valueOf(finalCoeffs[0]));	
			for(i=1; i<finalCoeffs.length; i++){
				fileWriter.append(",");
				fileWriter.append(String.valueOf(finalCoeffs[i]));	
			}
			//Add two new line separators and the r-squared value
			fileWriter.append("\n\nR-squared: "+String.valueOf(r_squared));
		
			System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
		}
		
    }
}

