/**
 * Describes a substitution model, along with methods to find the "distance"
 * between two traits.
 * 
 * @author raphaelkargon
 * 
 * @param <T>
 *            The type of characteristic used in this model.
 */
public abstract class SubstitutionModel<T> {
	
	/**
	 * Determines the distance between two traits. 
	 * Should be symmetrical, ie distance(x, y) == distance(y, x)
	 * 
	 * @param x The first trait
	 * @param y The second trait
	 * @return The distance between the two traits
	 */
	public abstract double distance(T x, T y);

	public double organismDistance(Organism<T> o1, Organism<T> o2){
		int minlength = Math.min(o1.traits.length, o2.traits.length);
		double total_dist=0;
		
		for(int i=0; i<minlength; i++){
			total_dist += distance(o1.traits[i], o2.traits[i]);
		}
		
		return total_dist;
	}
}