//Class that runs all the evo-regression stuff. Contains the main() method
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

class EvoRegress {

    public static void main(String[] args) {

        //Initialize variables

        //Strings with path to dataset and output destination
    		//Always have one dataPath variable commented out, depending on which model you want to run
    	String dataPath = ".\\Teacher-School Data\\data_teachers_run mod.csv";
//    	String dataPath = ".\\Student-Teacher Data\\M ready HSonly ssprev npprev inc - Small - Useable - No SS.csv";
    	String removedColsPath = ".\\Output\\Removed Cols.csv";
    	String outPath = ".\\Output\\generation_history.csv";	//Holds the historical data of population makeup over generations
    	String regResultsPath = ".\\Output\\results.csv";

    		//String containing name of objective variable
//        String objectiveVar = "NP";
        String objectiveVar = "retention";
       
            //Dictionary to contain Phenotype IDs and the number of each in gene pool
        //HashMap<String, int> phenoTracker = new HashMap<String, int>();

            //Control variables for main while function
        boolean converged = false;
        boolean fratricide = true; //Whether chance for one to mutate when identical phenotypes fight
        int generation = 0;
        int maxGeneration = 1000;
        double trainingProportion = 0.30;
        double battleProportion = 0.30;
        int numPhenotypes = 100;	//Has to be even number for pairings to work
        double mutationRate = 0.2;
        int mutationDepth = 5;

            //Read in the data from the CSV into an environment
        Environment env = new Environment();
        env.getFromCSV(dataPath, true);
        env.setObjective(objectiveVar);
        env.separateObjVar();
        	//Remove columns likely to be all 1s or 0s in sample matrices
        String[] failedCols = env.useFailureThreshold(Math.min(trainingProportion, battleProportion),.00001);
        System.out.println(Arrays.toString(failedCols));
        //Output the results to a csv
        FileWriter fileWriter = null;
		
        //Write any removed variables to file
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

        	//Save the number of independent variables
        int numVars = env.dataSet[0].length;
        
        
        //Get initial list of phenotypes
        Phenotype[] genePool = new Phenotype[numPhenotypes];
            //All phenotypes are generated randomly
          for (int i=0; i<numPhenotypes; i++){
        	boolean[] varbools = new boolean[numVars];
            for (int j=0; j<numVars; j++){
                varbools[j] = (Math.random()<0.5);
            }
            genePool[i] = new Phenotype(varbools, mutationRate, mutationDepth, false);
        }

        
        //Start the genetic algorithm
            //Run until max iterations
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
            boolean[] hasFought = new boolean[numPhenotypes];	//Array of boolean values of whether phenotype of that index has fought already
            int notFought = numPhenotypes;	//Number that haven't fought yet
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
            
            generation++;
            
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
		System.out.println(genePool[maxIndex].score(env));

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

