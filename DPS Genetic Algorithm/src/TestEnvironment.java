//Class to test the Environment class
import java.util.Arrays;

public class TestEnvironment {

	public static void main(String[] args) {
		double[][] data = 
			{
				{	1,	1,	2,	3	},
				{	2,	2,	3,	4	},
				{	3,	3,	4,	5	},
				{	4,	4,	5,	6	},
				{	5,	5,	6,	7	},
				{	6,	1,	7,	8	},
				{	7,	2,	1,	9	},
				{	8,	3,	2,	1	},
				{	9,	4,	3,	2	},
				{	10,	5,	4,	3	},
				{	11,	1,	5,	4	},
				{	12,	2,	6,	5	},
				{	13,	3,	7,	6	},
			};

		Environment env = new Environment();
		
		env.getNewSample(data,0.5);
		
		System.out.println(Arrays.deepToString(env.getDataSet()));
	}

}
