//Class to test Phenotype
public class TestPhenotype {

	public static void main(String[] args) {
		//Create new phenotype
		boolean[] vars = { true, true, false };
		double mutrate = 0.75;
		int mutdepth = 1;
		boolean muton = false;
		
		Phenotype gene = new Phenotype(vars, mutrate, mutdepth, muton);
		
		//Print properties of 'gene'
		System.out.println(gene.getId());
		System.out.println(gene.getMutationRate());
		System.out.println(gene.getMutationDepth());
		System.out.println(gene.getIsMutant());

		//Have gene reproduce and print properties of its child, gene2
		Phenotype gene2 = gene.reproduce();
		
		System.out.println(gene2.getId());
		System.out.println(gene2.getMutationRate());
		System.out.println(gene2.getMutationDepth());
		System.out.println(gene2.getIsMutant());
		
	}

}
