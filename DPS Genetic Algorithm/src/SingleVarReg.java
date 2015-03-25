//This code gets the coefficients and r2 values for single-var regressions
    //Repeated for each variable in data
import java.io.*;
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

class SingleVarReg {

static String objectiveVar = "SCHOOL_NUMBER";
static String dataFile     = 
    "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\math minitest.csv";
static String outFile      = 
    "C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm\\src\\sampleoutput.csv";
static boolean hasLabels   = true;


    public static void main(String[] args) {
    	
        //Read in the data from the CSV file and put it into an environment
        ReadCSV data = new ReadCSV();
        data.readFile(dataFile, hasLabels); 
        Environment env = new Environment();
            //Put entire dataset into environment
        env.getNewSample(data.getData(), 1.0);
            //Keep array of labels for the columns
        env.setLabels(data.getLabels());

            //Get the objective variable as an array
    	double[] Y = env.getNamedCol(objectiveVar);
    	double[][] XY = new double[Y.length][2];
    	double[] X = new double[Y.length];

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
        for  (String var : env.getLabels()){
            reg.clear();
            if(!objectiveVar.equals(var)){
                X = env.getNamedCol(var);
                for (int i=0; i<229694-1; i++){
                	XY[i][0] = X[i];
                	XY[i][1] = Y[i];
                }
                reg.addData(XY);
                RegressionResults results = reg.regress();
                    //Write coeffs and r2 to file
                try {
					bw.write(var + ", " + Arrays.toString(results.getParameterEstimates()) + ", " + results.getRSquared());
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
