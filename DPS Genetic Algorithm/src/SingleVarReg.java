//This code gets the coefficients and r2 values for single-var regressions
    //Repeated for each variable in data
import java.io.*;
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

class SingleVarReg {

static String objectiveVar = "SCHOOL_NUMBER";
static String dataFile     = 
    "C:\\Users\\Chris\\Desktop\\Chris\\Learning & Research\\School\\Spring-2015\\MATH 4779\\DPS Genetic Workspace\\Genetic-Algorithm\\DPS Genetic Algorithm\\src\\sampledata.csv";
static String outFile      = 
    "C:\\Users\\Chris\\Desktop\\Chris\\Learning & Research\\School\\Spring-2015\\MATH 4779\\DPS Genetic Workspace\\Genetic-Algorithm\\DPS Genetic Algorithm\\src\\sampleoutput.csv";
static boolean hasLabels   = true;


    public static void main(String[] args) {
    	
        //Read in the data from the CSV file and put it into an environment
        Environment env = new Environment();
            //Put entire dataset into environment
        env.getFromCSV(dataFile, hasLabels);
        env.setObjVar("Obj");
        env.separateObjVar();
        
        
            //Get the objective variable as an array
    	double[] Y = env.getObjData();

            //Make a new regression instance
        SimpleRegression reg = new SimpleRegression();

            //Prepare to write output
        File fout = new File(outFile);
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(fos != null){

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			//Loop through (non-objective) vars and regress on each
        for  (String varName : env.getLabels()){
            System.out.println(varName);
            reg.clear();
            double[] X = env.getColByName(varName);
            double[][] XY = new double[Y.length][2];
            for (int i=0; i<Y.length; i++){
            	XY[i][0]=X[i];
            	XY[i][1]=Y[i];
            }
            System.out.println(Arrays.deepToString(XY));
            reg.addData(XY);
            RegressionResults results = reg.regress();
                //Write coeffs and r2 to file
            try {
			bw.write(varName + ", " + Arrays.toString(results.getParameterEstimates()) + ", " + results.getRSquared());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}




}
