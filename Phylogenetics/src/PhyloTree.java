import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	/* CONSTRUCTORS */
	public PhyloTree(double branchlength, Organism<?> organism, List<PhyloTree> children) {
		super();
		this.branchlength = branchlength;
		this.organism = organism;
		if (children != null) this.children = (List<PhyloTree>) children;
		setLeafState();
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
		return toString(50);
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
		//TODO add branch length
		String branchspace = "", branchdash = "";
		double branchchars = (ratio == ratio) ? ratio * branchlength : 0;
		for (int i = 1; i <= branchchars; i++) {
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
}
