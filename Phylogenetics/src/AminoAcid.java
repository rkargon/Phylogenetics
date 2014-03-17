public enum AminoAcid {
	A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, _;

	public static AminoAcid[] parseString(String s) {
		AminoAcid[] seq = new AminoAcid[s.length()];
		for (int i = 0; i < seq.length; i++) {
			try {
				seq[i] = AminoAcid.valueOf(String.valueOf(s.charAt(i))
						.toUpperCase());
			}
			catch (IllegalArgumentException e) {
				seq[i] = _;
			}

		}
		return seq;
	}
}
