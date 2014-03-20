public class SimpleNucleotideModel extends SubstitutionModel<Nucleotide> {

	/**
	 * The "distance" between two nucleotides in this model is 1 if the
	 * nucleotides are different, and 0 if they are the same.
	 * 
	 * Two N values are treated as the same.
	 * Gaps are treated as separate characters, with a gap and a nucleotide considered different, and two gaps considered the same
	 * TODO Figure out proper way of treating sequence gaps
	 * 		-- seems like ignoring sequencing gaps makes things more accurate. (Or at least, more accurate than counting them as different characters)
	 */
	@Override
	public double distance(Nucleotide x, Nucleotide y) {
		//identical codes
		if (x.equals(y)) return 0;
		else if (x==Nucleotide.N || y==Nucleotide.N) return 0;
		else if (x==Nucleotide._ || y==Nucleotide._) return 0; //comment this out to stop ignoring gaps
		else if((x==Nucleotide.T || x==Nucleotide.U) && (y==Nucleotide.T || y==Nucleotide.U)) return 0;
		else return 1;
	}

}
