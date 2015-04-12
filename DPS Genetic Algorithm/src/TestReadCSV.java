import java.util.Arrays;


public class TestReadCSV {

	public static void main(String[] args) {
		String file = "sampledata.csv";
		boolean hasLabels = true;
		
		  System.out.println(file);

		ReadCSV read = new ReadCSV(file, hasLabels);

		double[][] data = read.getData();
		String[] labels = read.getLabels();
		
		System.out.println(Arrays.deepToString(labels));
		System.out.println(Arrays.deepToString(data));

		int[] rowcol = read.getNumRowsCols(file);
		System.out.println(Arrays.toString(rowcol));
	
	}

}
