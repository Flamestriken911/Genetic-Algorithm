import java.util.Arrays;


public class TestReadCSV {

	public static void main(String[] args) {
		String file = 
			"C:\\Users\\Chris\\Common Files\\Research and Work\\School\\Spring-2015\\MATH 4779\\Evolutionary Regression Algorithm\\Genetic-Algorithm\\DPS Genetic Algorithm\\sampledata.csv";
		boolean[] cols = {true, true, true};
		boolean hasLabels = true;
		
	
		ReadCSV read = new ReadCSV();
		read.getData(file, 5, cols, 1.0, hasLabels);

		double[][] data = read.getData();
		String[] labels = read.getLabels();
		
		System.out.println(Arrays.deepToString(labels));
		System.out.println(Arrays.deepToString(data));
	}

}
