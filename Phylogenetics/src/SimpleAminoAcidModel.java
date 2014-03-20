
public class SimpleAminoAcidModel extends SubstitutionModel<AminoAcid> {

	@Override
	public double distance(AminoAcid x, AminoAcid y) {
		// TODO Auto-generated method stub
		if(x.equals(y)) return 0;
		return 1;
	}

}
