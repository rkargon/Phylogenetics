/**
 * Represents a nucleotide. Constants use IUPAC codes.
 * 
 * A Adenine
 * C Cytosine
 * G Guanine
 * T (or U) Thymine (or Uracil)
 * N any base
 * _ gap
 * 
 * TODO: support more degenerate nucleotides
 */
public enum Nucleotide {
	A, C, G, T, U, N, _;

	public static Nucleotide[] parseString(String s) {
		Nucleotide[] seq = new Nucleotide[s.length()];
		for (int i = 0; i < seq.length; i++) {
			try {
				seq[i] = Nucleotide.valueOf(String.valueOf(s.charAt(i))
						.toUpperCase());
			}
			catch (IllegalArgumentException e) {
				seq[i] = _;
			}

		}
		return seq;
	}
}
