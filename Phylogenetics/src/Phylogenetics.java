import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

/**
 * Phylogenetics functions
 * 
 * TODO load common phylo file formats
 * 
 * @author raphaelkargon
 * @version 0.1
 */
public class Phylogenetics {

	/* FILE I/O */
	public static final Pattern CLUSTALW_LINE = Pattern
			.compile("^([^\\s]+)\\s+([A-Z_\\-]+)");

	/**
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Organism<Nucleotide>> readNucleotideClustalW(File f) throws IOException{

		BufferedReader br = new BufferedReader(new FileReader(f));
		ArrayList<Organism<Nucleotide>> organisms = new ArrayList<Organism<Nucleotide>>();
		HashMap<String, String> seqs_hash = new HashMap<String, String>();
		
		String line = "", seq = "", name = "";
		Matcher m;
		
		while((line = br.readLine()) != null){
			m = CLUSTALW_LINE.matcher(line);
			if(m.find()){
				name = m.group(1);
				seq = m.group(2);
				if(seqs_hash.get(name)==null) seqs_hash.put(name, seq);
				else seqs_hash.put(name, seqs_hash.get(name).concat(seq));
			}
		}

		//convert hash to array of organisms
		for (String name_tmp : seqs_hash.keySet()) {
			organisms.add(new Organism<Nucleotide>(Nucleotide
					.parseString(seqs_hash.get(name_tmp)), name_tmp));
		}
		
		br.close();
		return organisms;
	}
	
	public static ArrayList<Organism<Nucleotide>> readMultipleNucleotideClustalWs(
			File[] files) throws IOException {
		ArrayList<Organism<Nucleotide>> organisms = new ArrayList<Organism<Nucleotide>>();

		for (File f : files) {
			organisms.addAll(readNucleotideClustalW(f));
		}

		return organisms;
	}

	/**
	 * Reads a FASTA file of a set of sequences.
	 * A regex can be used to extract organism names from headers. (The first
	 * capture group is used).
	 * Only the first sequence for a given organism name is stored.
	 * TODO recover and align multiple sequences from an organism
	 * -- Maybe require that data is already aligned?
	 * 
	 * @param f
	 *            The FASTA file to be read
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Organism<Nucleotide>> readFASTA(File f, String regex)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		ArrayList<Organism<Nucleotide>> organisms = new ArrayList<Organism<Nucleotide>>();
		HashMap<String, String> seqs_hash = new HashMap<String, String>();
		String line = "", seq = "", name = "";
		boolean use_regex = (regex.length() > 0);
		Pattern p = null;
		Matcher m;
		if (use_regex) {
			p = Pattern.compile(regex);
		}

		while ((line = br.readLine()) != null) {
			line.trim();
			if (line.length() == 0) continue;
			if (line.charAt(0) == '>') {
				//if this isn't the first line in the file, ie a sequence has already been read
				if (seq.length() > 0) {
					//add to hash if organism hasn't been added yet
					if (seqs_hash.get(name)==null) seqs_hash.put(name, seq);
				}

				if (use_regex) {
					m = p.matcher(line);
					name = m.find() ? m.group(1) : line.substring(1);
				}
				else name = line.substring(1); //sequence header
				seq = ""; //new sequence 
			}
			else {
				seq += line;
			}
		}
		//add final sequence
		if (!seqs_hash.containsKey(name)) seqs_hash.put(name, seq);

		//convert hash to array of organisms
		for (String name_tmp : seqs_hash.keySet()) {
			organisms.add(new Organism<Nucleotide>(Nucleotide
					.parseString(seqs_hash.get(name_tmp)), name_tmp));
		}

		br.close();
		return organisms;
	}

	public static ArrayList<Organism<Nucleotide>> readFASTA(File f)
			throws IOException {
		return readFASTA(f, "");
	}

	/**
	 * Reads a set of FASTA files, assigning each sequence to a different
	 * organism.
	 * 
	 * @param f
	 *            the set of files to be read
	 * @return The list of Organisms and their data
	 * @throws IOException
	 *             If an unreadable file is encountered
	 */
	public static ArrayList<Organism<Nucleotide>> readMultipleFASTAs(
			File[] files, String regex) throws IOException {
		ArrayList<Organism<Nucleotide>> organisms = new ArrayList<Organism<Nucleotide>>();

		for (File f : files) {
			organisms.addAll(readFASTA(f, regex));
		}

		return organisms;
	}

	//calls readMultipleFASTAs with empty regex
	public static ArrayList<Organism<Nucleotide>> readMultipleFASTAs(
			File[] files) throws IOException {
		return readMultipleFASTAs(files, "");
	}

	/* TREE I/O */

	/* TREE ALGORITHMS */

	/**
	 * Creates a pairwise distance matrix between organisms using a given
	 * substitution model
	 * 
	 * @param organisms
	 *            The set of organisms to be used
	 * @param model
	 *            The substitution model used to determine pariwise distances
	 * @return A 2-dimensional ArrayList of Double values representing pairwise
	 *         distances
	 */
	public static <T> ArrayList<ArrayList<Double>> distanceMatrix(
			ArrayList<Organism<T>> organisms, SubstitutionModel<T> model) {
		ArrayList<ArrayList<Double>> dist_matrix = new ArrayList<ArrayList<Double>>();

		for (Organism<T> o : organisms) {
			ArrayList<Double> row = new ArrayList<Double>();
			for (Organism<T> o2 : organisms) {
				row.add(model.organismDistance(o, o2));
			}
			dist_matrix.add(row);
		}

		return dist_matrix;
	}

	/**
	 * Creates phylogenetic tree based on the
	 * "Unweighted Pair Group Method with Arithmetic Mean" algorithm, given a
	 * set of organisms with traits and a substitution model to calculate
	 * pairwise distances.
	 * 
	 * @param organisms
	 *            The array of organisms to be sorted into a tree
	 * @param model
	 *            The substitution model used
	 * @return A phylogenetic tree with the given organisms
	 */
	public static <T> PhyloTree UPGMA_Tree(ArrayList<Organism<T>> organisms,
			SubstitutionModel<T> model) {

		//create distance matrix
		ArrayList<ArrayList<Double>> dist_matrix = distanceMatrix(organisms, model);

		//set up array of group counts, faster than recursively searching each subtree to find number of leaf nodes
		ArrayList<Integer> groupcounts = new ArrayList<Integer>();
		for (int i = 0; i < organisms.size(); i++) {
			groupcounts.add(1);
		}

		//Set up array of partial trees
		ArrayList<PhyloTree> trees = new ArrayList<PhyloTree>();
		for (int i = 0; i < organisms.size(); i++) {
			trees.add(new PhyloTree(0, organisms.get(i), null));
		}

		while (trees.size() > 1) {

			//find smallest i,j
			int i, j, i_min = -1, j_min = -1;
			double d_ij, d_min = Double.NaN; //starting, invalid value
			for (i = 0; i < dist_matrix.size(); i++) {
				//distance matrix should be symmetrical, can start with j=i+1 to save time
				for (j = i + 1; j < dist_matrix.size(); j++) {
					//System.out.println(i+", "+j);
					d_ij = dist_matrix.get(i).get(j);
					//new minimum is found
					if (d_ij < d_min || d_min != d_min) { //wacky NaN!=NaN test!
						d_min = d_ij;
						i_min = i;
						j_min = j;
					}
				}
			}

			//group most similar nodes under new node, assign branch lengths D/2 to each
			PhyloTree newtree = new PhyloTree(0, null, Arrays.asList(trees
					.get(i_min), trees.get(j_min)));
			newtree.children().get(0).branchlength = d_min / 2
					- newtree.children().get(0).getFirstBranchLength();
			newtree.children().get(1).branchlength = d_min / 2
					- newtree.children().get(1).getFirstBranchLength();
			trees.add(newtree);

			//update group counts
			int n_i = groupcounts.get(i_min), n_j = groupcounts.get(j_min), n_ij = n_i
					+ n_j;
			groupcounts.add(n_ij);

			//update distance matrix
			double newdist; //temp variable for distance between new group and other nodes
			ArrayList<Double> newrow = new ArrayList<Double>();
			for (int k = 0; k < dist_matrix.size(); k++) {
				newdist = ((double) n_i / n_ij) * dist_matrix.get(k).get(i_min)
						+ ((double) n_j / n_ij) * dist_matrix.get(k).get(j_min);
				dist_matrix.get(k).add(newdist);
				newrow.add(newdist);
			}
			newrow.add(0.0);
			dist_matrix.add(newrow);

			//remove data corresponding to i_min, j_min
			//i_min < j_min, so j is removed first to prevent shifting of indices
			trees.remove(j_min);
			trees.remove(i_min);
			groupcounts.remove(j_min);
			groupcounts.remove(i_min);
			dist_matrix.remove(j_min);
			dist_matrix.remove(i_min);
			//remove columns from individual rows
			for (ArrayList<Double> row : dist_matrix) {
				row.remove(j_min);
				row.remove(i_min);
			}
		}	

		return trees.get(0);
	}

	public static void main(String[] args) {
//				/* File dialog */
//						//TODO Set up actual interface
//						JFrame frame = new JFrame();
//				
//						//Set up file dialog
//						FileDialog fd = new FileDialog(frame, "Choose Input Data:", FileDialog.LOAD);
//						fd.setMultipleMode(true);
//						fd.setDirectory("~");
//						fd.setVisible(true);
//				
//						File[] files = fd.getFiles();

		File[] files = { new File("/Users/raphaelkargon/Dropbox/Programming/Phylogenetics/sample_vertebrates.fasta") };

		try {
			ArrayList<Organism<Nucleotide>> organisms = new ArrayList<Organism<Nucleotide>>();
			organisms = readMultipleFASTAs(files, "");
			//organisms = Phylogenetics.readMultipleNucleotideClustalWs(files);
 
			System.out.println(organisms.size() + " samples read.");
			//display organisms that have been read
			for (Organism<Nucleotide> o : organisms)
				System.out.println(o);
			System.out.println();
			
			PhyloTree tree = UPGMA_Tree(organisms, new SimpleNucleotideModel());
			System.out.println(tree);
			System.out.println(tree.toNewickString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

}
