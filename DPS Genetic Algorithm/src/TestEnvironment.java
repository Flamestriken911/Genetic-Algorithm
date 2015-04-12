//Class to test the Environment class
import java.util.Arrays;

public class TestEnvironment {

	public static void main(String[] args) {
/*		
			double[][] data = 
			{
				{	1,	1,	2,	0	},
				{	2,	2,	3,	1	},
				{	3,	3,	4,	0	},
				{	4,	4,	5,	0	},
				{	5,	5,	6,	0	},
				{	6,	1,	7,	0	},
				{	7,	2,	1,	1	},
				{	8,	3,	2,	0	},
				{	9,	4,	3,	0	},
				{	10,	5,	4,	0	},
				{	11,	1,	5,	1	},
				{	12,	2,	6,	0	},
				{	13,	3,	7,	0	},
			};

        String[] labels = 
        {   "Var1", "Var2", "Var3", "Var4"  };

		Environment env = new Environment();
		
		env.getNewSample(data,1);
        env.setLabels(labels);
		
		System.out.println(Arrays.deepToString(env.getDataSet()));
		System.out.println(Arrays.toString(env.getColByName("Var1")));
		System.out.println(Arrays.toString(env.useFailureThreshold(.2,.05)));

*/		
		String file = 
	            "sampledata.csv";
		boolean hasLabels = true;
		
		Environment env = new Environment();
		env.getFromCSV(file,  hasLabels);
		
		System.out.println(Arrays.deepToString(env.getDataSet()));
//		System.out.println(Arrays.toString(env.getLabels()));
		
		env.setObjVar("Obj");
		env.separateObjVar();
		System.out.println(Arrays.deepToString(env.getDataSet()));
		System.out.println(Arrays.toString(env.getObjData()));
//		System.out.println(env.removeCol(1));
//		System.out.println(Arrays.deepToString(env.getDataSet()));
		System.out.println(Arrays.toString(env.useFailureThreshold(.2,.05)));

	}

}
