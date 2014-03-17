/**
 * Implements a phylogenetic binary tree of different organisms
 * 
 * TODO create parent pointer?
 * TODO output to tree file formats
 * TODO constructor using tree file format
 * 
 * @author raphaelkargon
 * 
 */
public class PhyloTree {
	public double branchlength = 0; //length of branch leading up to the node
	public Organism<?> organism = null; //this is null unless the object is a leaf node
	public PhyloTree left = null, right = null; //children of tree, null if the object is a leaf node

	public PhyloTree(Organism<?> organism) {
		this.organism = organism;
	}

	public PhyloTree(PhyloTree left, PhyloTree right) {
		this.left = left;
		this.right = right;

	}

	/**
	 * Recursively traverse the tree to find the number of leaves
	 * 
	 * @return the number of leaves in the tree
	 */
	public int getLeaves() {
		if (organism != null) return 1;
		else return ((left == null) ? 0 : left.getLeaves())
				+ ((right == null) ? 0 : right.getLeaves());
	}

	/**
	 * Returns the average length of all branches
	 * @return The average length of all branches
	 */
	public double getAverageBranchLength() {
		if (left == null && right == null) return branchlength;
		else if (right == null) return left.getTotalLength() / left.getLeaves();
		else if (left == null) return right.getTotalLength()
				/ right.getLeaves();
		else return (left.getTotalLength() + right.getTotalLength())
				/ (left.getLeaves() + right.getLeaves());

	}

	/**
	 * The total length of all branches
	 * @return The total length of all branches
	 */
	public double getTotalLength() {
		return branchlength + ((left == null) ? 0 : left.getTotalLength())
				+ ((right == null) ? 0 : right.getTotalLength());
	}

	/**
	 * Returns the branch length obtained by only traversing the leftmost
	 * branches of the tree
	 * Faster than recursively traversing the whole tree, and can be used with
	 * ultrametric trees
	 * If the left node is null, the right node is used.
	 * 
	 * @return The distance from the root to the leftmost leaf node
	 */
	public double getLeftBranchLength() {
		return branchlength
				+ ((left == null) ? ((right == null) ? 0 : right.branchlength)
						: left.branchlength);
	}

	/**
	 * Displays the tree, similar to Linux "tree" command
	 */
	public String toString() {
		return toString("", true);
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
	 * @return A string representing the tree
	 */
	private String toString(String prefix, boolean isTail) {
		String s = "";

		//TODO add branch length
		//print current node info
		s += (prefix + (isTail ? "\\-- " : "|-- ")
				+ (organism == null ? "-" : organism.name) + "\n");

		if (left != null)
			s += left.toString(prefix + (isTail ? "    " : "|   "), false);
		if (right != null)
			s += right
					.toString(prefix + (isTail ? "    " : "|   "), (left != null));

		return s;
	}
}
