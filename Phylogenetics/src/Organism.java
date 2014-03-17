import java.util.Arrays;

/**
 * Stores data of one organism, or sample.
 * 
 * @author raphaelkargon
 *
 */
public class Organism<T> {
	public T[] traits;
	public String name;
	
	public Organism(){
		this(null, null);
	}
	
	public Organism(T[] traits, String name) {
		super();
		this.traits = traits;
		this.name = name;
	}
	
	@Override
	public String toString(){
		return String.format("Organism: Name = \"%10s\", Traits = %s", name, Arrays.toString(traits));
	}
	
}
