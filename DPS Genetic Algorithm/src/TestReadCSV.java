import java.util.Arrays;


public class TestReadCSV {

	public static void main(String[] args) {
		String file = 
	            "C:\\Users\\Chris\\Desktop\\Chris\\Learning & Research\\School\\Spring-2015\\MATH 4779\\DPS Genetic Workspace\\Genetic-Algorithm\\DPS Genetic Algorithm\\src\\sampledata.csv";
		boolean hasLabels = true;
		
	
		ReadCSV read = new ReadCSV(file, hasLabels);

		double[][] data = read.getData();
		String[] labels = read.getLabels();
		
		System.out.println(Arrays.deepToString(labels));
		System.out.println(Arrays.deepToString(data));

		int[] rowcol = read.getNumRowsCols(file);
		System.out.println(Arrays.toString(rowcol));
	}

}
