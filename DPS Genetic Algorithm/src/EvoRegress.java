//Class that runs all the evo-regression stuff. Contains the main() method
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

class EvoRegress {








    public static void main(String[] args) {

        //Initialize variables

        //Strings with path to dataset and output destination

    	String dataPath = "M - Modified - Small.csv";
    	String removedColsPath = "Removed Cols.csv";
    	String outPath = "output.csv";
    	String regResultsPath = "results.csv";

    		//String containing name of objective variable
        String objectiveVar = "NP";
       

            //Dictionary to contain Phenotype IDs and the number of each in gene pool
        //HashMap<String, int> phenoTracker = new HashMap<String, int>();


            //Control variables for main while function
        boolean converged = false;
        boolean twinsMutate = true; //Whether chance for one to mutate when identical phenotypes fight
        int generation = 0;
        int maxGeneration = 10;
        double trainingProportion = 0.2;
        double battleProportion = 0.15;
        int numPhenotypes = 10;	//Has to be even number for pairings to work
        double mutationRate = 0.25;
        int mutationDepth = 4;

            //Read in the data from the CSV into an environment
        Environment env = new Environment();
        env.getFromCSV(dataPath, true);
        env.setObjective(objectiveVar);
        env.separateObjVar();
        	//Remove columns likely to be all 1s or 0s in sample matrices
        String[] failedCols = env.useFailureThreshold(trainingProportion,.00001);
        System.out.println(Arrays.toString(failedCols));
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

        	//Save the number of independent variables
        int numVars = env.dataSet[0].length;
        
        
        //Get initial list of phenotypes
        Phenotype[] genePool = new Phenotype[numPhenotypes];
            //Always have the zero-phenotype 
        boolean[] noBools = new boolean[numVars];	//No need to initialize since booleans default to false
        genePool[0] = new Phenotype(noBools, mutationRate, mutationDepth, false);
/*        	//And the all-phenotype
        boolean[] allBools = new boolean[numVars];
    	for (int i=0; i<numVars; i++){
    		allBools[i] = true;
    	}
        genePool[1] = new Phenotype(allBools, mutationRate, mutationDepth, false);
*/
            //All other phenotypes are generated randomly
//        for (int i=2; i<numPhenotypes; i++){
          for (int i=1; i<numPhenotypes; i++){
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
	                	if(twinsMutate)  genePool[fighter] = genePool[vsIndex].reproduce();;
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

		//Output the results to a csv
        fileWriter = null;
		
		try {
			fileWriter = new FileWriter(regResultsPath);

			//Write the CSV file header
			fileWriter.append(Arrays.toString(env.getLabels()));
			
			//Add a new line separator after the header
			fileWriter.append("\n");
			
			//Write coeffs to the CSV file
			fileWriter.append(Arrays.toString(finalCoeffs));
		
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

