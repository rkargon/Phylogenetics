import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DebugGraphics;

/**
 * Implements a phylogenetic tree.
 * 
 * TODO create parent pointer?
 * TODO output to tree file formats
 * TODO constructor using tree file format
 * 
 * @author raphaelkargon
 * @version 0.2
 */
public class PhyloTree {
	public double branchlength = 0; //length of branch leading up to the node
	public Organism<?> organism = null; //this is null unless the object is a leaf node
	private List<PhyloTree> children = new ArrayList<PhyloTree>(); //children of tree, null if the object is a leaf node
	private boolean isLeaf = true;

	//regexes for Newick matching
	public static String length_rgx = "[-+]?\\d*\\.?\\d+"; //number
	public static String name_rgx = "([^\\(\\),;:]*)"; //name, not containing ( ) , ; :
	public static String subtree_rgx = "(?:\\((.*)\\))?" + name_rgx + "?"
			+ "(\\:" + length_rgx + ")?;?";//subtree, (...)[name][:length]
	Pattern newick_p = Pattern.compile(subtree_rgx);

	/* CONSTRUCTORS */
	public PhyloTree(double branchlength, Organism<?> organism, List<PhyloTree> children) {
		super();
		this.branchlength = branchlength;
		this.organism = organism;
		if (children != null) this.children = (List<PhyloTree>) children;
		setLeafState();
	}

	/**
	 * Copy constructor
	 * NOTE: Shallow copy
	 * Children point to the same objects as t.children.
	 * Also, regardless of type of t.children, this.children becomes arraylist
	 * Organism, however, is copied.
	 * 
	 * @param t
	 *            The tree to copy
	 */
	public PhyloTree(PhyloTree t) {
		this.branchlength = t.branchlength;
		this.organism = t.organism.clone();
		for (PhyloTree child : t.children) {
			this.children.add(child);
		}
		setLeafState();
	}

	/**
	 * Constructs tree based on Newick tree string
	 */
	public PhyloTree(String treestr) {
		Matcher m = newick_p.matcher(treestr);
		if (m.matches()) {
			String children_str = m.group(1);
			String name = m.group(2);
			double length;
			try {
				length = Double.parseDouble(m.group(3).substring(1));
			}
			catch (Exception e) {
				length = 0;
			}

			this.organism = new Organism<Void>(new Void[0], (name==null ? "" : name));
			this.branchlength = length;
			if(children_str!=null && children_str.length()>0){
				int start=0, end=0;//bounds for substring to start new child
				int depth = 0;//only top-level commas should be counted
				char c;
				for(int i=0; i<children_str.length(); i++){
					c = children_str.charAt(i);
					if(c=='(') depth++;
					else if(c==')') depth--;
					else if(c==',') {
						if(depth==0){
							end = i;
							children.add(new PhyloTree(children_str.substring(start, end)));
							start = i+1;
						}
					}
				}
				children.add(new PhyloTree(children_str.substring(start, children_str.length())));
			}
			setLeafState();
		}
		else throw new IllegalArgumentException("Invalid Tree String: "+treestr);
	}

	/* ACCESSORS */
	/**
	 * @return the children
	 */
	public List<PhyloTree> children() {
		return children;
	}

	/**
	 * Returns child with corresponding index.
	 * Throws ArrayIndexOutOfBoundsException if invalid index
	 * 
	 * @param i
	 *            The index of the child to get
	 * @return
	 */
	public PhyloTree getChild(int i) {
		//if(i<0 || i>=children.size()) return null;
		return children.get(i);
	}

	/**
	 * @return the isLeaf
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/* MUTATORS */

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<PhyloTree> children) {
		if (children != null) this.children = children;
		else children = new ArrayList<PhyloTree>();
		setLeafState();
	}

	/**
	 * Properly sets <code>isLeaf</code> based on number of children
	 */
	private void setLeafState() {
		this.isLeaf = (children.size() == 0);
	}

	/**/
	public boolean isBinary() {
		boolean isBinary = true;

		if (isLeaf) return true;
		else {
			if (children.size() == 2) {
				for (PhyloTree t : children)
					isBinary = isBinary && t.isBinary();
			}
			else return false;
		}

		return isBinary;
	}

	/**
	 * Recursively traverse the tree to find the number of leaves
	 * 
	 * @return the number of leaves in the tree
	 */
	public int getLeaves() {
		if (isLeaf) return 1;
		else {
			int leaves = 0;
			for (PhyloTree t : children)
				leaves += t.getLeaves();
			return leaves;
		}
	}

	/**
	 * Returns the average length of all branches
	 * If this is a leaf node, return 0
	 * 
	 * @return The average length of all branches
	 */
	public double getAverageBranchLength() {
		int sum = 0, leaves = getLeaves();
		for (PhyloTree t : children)
			sum += t.getTotalLength();
		return (leaves == 0) ? 0 : sum / leaves;
	}

	/**
	 * The total length of all branches
	 * 
	 * @return The total length of all branches
	 */
	public double getTotalLength() {
		if (isLeaf) return branchlength;
		double totallength = this.branchlength;
		for (PhyloTree t : children)
			totallength += t.getTotalLength();
		return totallength;
	}

	public double getMaxLength() {
		if (isLeaf) return branchlength;
		double max = Double.NaN;
		for (PhyloTree t : children) {
			max = (max == max) ? Math.max(max, t.getMaxLength()) : t
					.getMaxLength();
		}
		return branchlength + max;
	}

	/**
	 * Returns the branch length obtained by only traversing the first child of
	 * each node.
	 * Faster than recursively traversing the whole tree, and can be used with
	 * ultrametric trees..
	 * 
	 * @return The distance from the root to the leftmost leaf node
	 */
	public double getFirstBranchLength() {
		double length = branchlength;
		for (PhyloTree t : children) {
			length += t.getFirstBranchLength();
			break;
		}
		return length;
	}

	/**
	 * Displays the tree, similar to Linux "tree" command
	 */
	public String toString(int width) {
		double max_len = getMaxLength();
		double ratio = (max_len != 0) ? width / max_len : Double.NaN;
		return toString("", true, ratio);
	}

	public String toString() {
		return toString(100);
	}

	/**
	 * Displays the tree with given settings and prefix, used in
	 * {@link #toString()}
	 * 
	 * @param prefix
	 *            Added before output, used to indent diagram if this is a
	 *            subtree
	 * @param isTail
	 *            Used to format last item in list of children differently
	 * @param ratio
	 *            How many characters / unit of branch length. Usually set up so
	 *            that max length of tree is 100 characters.
	 * @return A string representing the tree
	 */
	private String toString(String prefix, boolean isTail, double ratio) {
		String s = "";
		String branchspace = "", branchdash = "";
		double branchchars = (ratio == ratio) ? ratio * branchlength : 0;
		for (int i = 0; i <= branchchars; i++) {
			branchspace += " ";
			branchdash += "-";
		}

		//print current node info
		s += (prefix + (isTail ? "\\" : "|") + branchdash
				+ (organism == null ? "" : " " + organism.name) + " ("
				+ String.format("%.1f", branchlength) + ")\n");

		if (children.size() > 0) {
			for (int i = 0; i < children.size() - 1; i++) {
				s += children.get(i).toString(prefix + (isTail ? "" : "|")
						+ branchspace, false, ratio);
			}
			s += children.get(children.size() - 1).toString(prefix
					+ (isTail ? "" : "|") + branchspace, true, ratio);
		}

		return s;
	}

	public String toNewickString(){return toNewickString(true);}
	public String toNewickString(boolean isRoot){
		String s = "";
		if(!isLeaf){
			s+="(";
			for(int i=0; i<children.size()-1; i++){
				s+=children.get(i).toNewickString(false)+",";
			}
			s+=children.get(children.size()-1).toNewickString(false);
			s+=")";
		}
		if(organism != null) s+=organism.name;
		if(branchlength!=0) s+=":"+String.valueOf(branchlength).replaceAll("\\.?0+$", "");
		if(isRoot) s+=";";
		return s;
	}
	
	public static void main(String[] args) {
		PhyloTree t = new PhyloTree("((C,(D))B,(G,H)F)A;");
		System.out.println(t);
		System.out.println(t.toNewickString());
	}
}
