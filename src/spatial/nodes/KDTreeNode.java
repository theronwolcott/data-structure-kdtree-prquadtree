package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;

import java.util.Collection;

/**
 * <p>
 * {@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used
 * extensively by
 * {@link spatial.trees.KDTree} to implement its functionality.
 * </p>
 *
 * <p>
 * <b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b>
 * </p>
 *
 * @author ---- YOUR NAME HERE! -----
 *
 * @see spatial.trees.KDTree
 */
public class KDTreeNode {

    /*
     * ***************************************************************************
     */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED. **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private int height;
    private KDTreeNode left, right;

    /*
     * *****************************************************************************
     * **********
     */
    /*
     * ************* PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE:
     * ************
     */
    /*
     * *****************************************************************************
     * ********
     */

    /* *********************************************************************** */
    /* *************** IMPLEMENT THE FOLLOWING PUBLIC METHODS: ************ */
    /* *********************************************************************** */

    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly
     * created node.
     * 
     * @param p The {@link KDPoint} to store inside this. Just a reminder:
     *          {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p) {
        this.p = p;
        height = 0;
        left = null;
        right = null;
    }

    /**
     * <p>
     * Inserts the provided {@link KDPoint} in the tree rooted at this. To select
     * which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the
     * value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained
     * {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.
     * </p>
     * 
     * @param currDim The current dimension to consider
     * @param dims    The total number of dimensions that the space considers.
     * @param pIn     The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public void insert(KDPoint pIn, int currDim, int dims) {
        insert(this, pIn, currDim, dims);
    }

    private static KDTreeNode insert(KDTreeNode node, KDPoint pIn, int currDim, int dims) {
        if (node == null) {
            return new KDTreeNode(pIn);
        }
        var nextDim = (currDim + 1) % dims;
        if (pIn.coords[currDim] < node.p.coords[currDim]) {
            node.left = insert(node.left, pIn, nextDim, dims);
        } else {
            node.right = insert(node.right, pIn, nextDim, dims);
        }
        int lHeight = node.left == null ? -1 : node.left.height;
        int rHeight = node.right == null ? -1 : node.right.height;
        node.height = (Math.max(lHeight, rHeight)) + 1;

        return node;
    }

    /**
     * <p>
     * Deletes the provided {@link KDPoint} from the tree rooted at this. To select
     * which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the
     * value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained
     * {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There
     * exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who
     * either:
     * </p>
     *
     * <ul>
     * <li>Has a NON-null subtree as a right child.</li>
     * <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>
     * You should consult the class slides, your notes, and the textbook about what
     * you need to do in those two
     * special cases.
     * </p>
     * 
     * @param currDim The current dimension to consider.
     * @param dims    The total number of dimensions that the space considers.
     * @param pIn     The {@link KDPoint} to delete from the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims) {
        return delete(this, pIn, currDim, dims);
    }

    private static KDTreeNode delete(KDTreeNode node, KDPoint pIn, int currDim, int dims) {
        if (node == null) {
            return null;
        }
        var nextDim = (currDim + 1) % dims;

        if (node.p.equals(pIn)) {
            if (node.left == null && node.right == null) {
                return null;
            } else if (node.right != null) {
                // find in order and swap
                var inOrder = inOrder(node.right, currDim, nextDim, dims);
                node.p = inOrder;
                // recursively delete on the one you swapped with
                node.right = delete(node.right, inOrder, nextDim, dims);
            } else {
                // find in order and swap
                var inOrder = inOrder(node.left, currDim, nextDim, dims);
                node.p = inOrder;
                // make the left subtree the right, make left null
                node.right = node.left;
                node.left = null;
                // recursively delete on the one you swapped with
                node.right = delete(node.right, inOrder, nextDim, dims);
            }
        } else if (pIn.coords[currDim] < node.p.coords[currDim]) {
            node.left = delete(node.left, pIn, nextDim, dims);
        } else {
            node.right = delete(node.right, pIn, nextDim, dims);
        }
        int lHeight = node.left == null ? -1 : node.left.height;
        int rHeight = node.right == null ? -1 : node.right.height;
        node.height = (Math.max(lHeight, rHeight)) + 1;
        return node;
    }

    private static KDPoint inOrder(KDTreeNode node, int targetDim, int currDim, int dims) {
        if (node == null) {
            return null;
        }
        var nextDim = (currDim + 1) % dims;
        var lowest = node.p;
        if (node.left != null) {
            var l = inOrder(node.left, targetDim, nextDim, dims);
            if (l.coords[targetDim] < lowest.coords[targetDim]) {
                lowest = l;
            }
        }
        if (node.right != null) {
            var r = inOrder(node.right, targetDim, nextDim, dims);
            if (r.coords[targetDim] < lowest.coords[targetDim]) {
                lowest = r;
            }
        }
        return lowest;
    }

    /**
     * Searches the subtree rooted at the current node for the provided
     * {@link KDPoint}.
     * 
     * @param pIn     The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims    The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false
     *         otherwise.
     */
    public boolean search(KDPoint pIn, int currDim, int dims) {
        return search(this, pIn, currDim, dims);
    }

    private boolean search(KDTreeNode node, KDPoint pIn, int currDim, int dims) {
        if (node == null) {
            return false;
        }

        var nextDim = (currDim + 1) % dims;

        if (node.p.equals(pIn)) {
            return true;
        }

        if (pIn.coords[currDim] < node.p.coords[currDim]) {
            return search(node.left, pIn, nextDim, dims);
        } else {
            return search(node.right, pIn, nextDim, dims);
        }

    }

    /**
     * <p>
     * Executes a range query in the given {@link KDTreeNode}. Given an
     * &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint)
     * euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself
     * should be inserted into the {@link Collection}
     * that is passed.
     * </p>
     *
     * <p>
     * Remember: range queries behave <em>greedily</em> as we go down (approaching
     * the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have
     * to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.
     * </p>
     *
     * @param anchor  The centroid of the hypersphere that the range query
     *                implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims    The total number of dimensions of our {@link KDPoint}s.
     * @param range   The <b>INCLUSIVE</b> range from the &quot;anchor&quot;
     *                {@link KDPoint}, within which all the
     *                {@link KDPoint}s that satisfy our query will fall. The
     *                euclideanDistance metric used} is defined by
     *                {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
            double range, int currDim, int dims) {
        range(this, anchor, results, range, currDim, dims);
    }

    private static void range(KDTreeNode node, KDPoint anchor, Collection<KDPoint> results,
            double range, int currDim, int dims) {
        if (node == null) {
            return;
        }
        var nextDim = (currDim + 1) % dims;
        double diff = Math.abs(anchor.coords[currDim] - range);

        if ((anchor.coords[currDim] - range) <= node.p.coords[currDim]) {
            // recurse left
            range(node.left, anchor, results, range, nextDim, dims);
        }
        if ((anchor.coords[currDim] + range) >= node.p.coords[currDim]) {
            // recurse right
            range(node.right, anchor, results, range, nextDim, dims);
        }
        if (node.p.euclideanDistance(anchor) <= range && !node.p.equals(anchor)) {
            results.add(node.p);
        }

    }

    /**
     * <p>
     * Executes a nearest neighbor query, which returns the nearest neighbor, in
     * terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot;
     * point.
     * </p>
     *
     * <p>
     * Recall that, in the descending phase, a NN query behaves <em>greedily</em>,
     * approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it
     * implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best
     * solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as
     * &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently.
     * Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to
     * compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those
     * are comparable with each other because they
     * are the same data type ({@link Double}).
     * </p>
     *
     * @return An object of type {@link NNData}, which exposes the pair
     *         (distance_of_NN_from_anchor, NN),
     *         where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint}
     *         that we found.
     *
     * @param anchor  The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor
     *                query.
     * @param currDim The current dimension considered.
     * @param dims    The total number of dimensions considered.
     * @param n       An object of type {@link NNData}, which will define a nearest
     *                neighbor as a pair (distance_of_NN_from_anchor, NN),
     *                * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
            NNData<KDPoint> n, int dims) {
        nearestNeighbor(this, anchor, currDim, n, dims);
        return n;
    }

    private static void nearestNeighbor(KDTreeNode node, KDPoint anchor, int currDim,
            NNData<KDPoint> n, int dims) {
        if (node == null) {
            return;
        }
        var nextDim = (currDim + 1) % dims;
        double diff = Math.abs(anchor.coords[currDim] - node.p.coords[currDim]);
        double dist = node.p.euclideanDistance(anchor);

        if ((dist < n.getBestDist() || n.getBestDist() < 0) && !node.p.equals(anchor)) {
            n.update(node.p, dist);
        }

        if (anchor.coords[currDim] < node.p.coords[currDim]) {
            nearestNeighbor(node.left, anchor, nextDim, n, dims);
            if (diff < n.getBestDist()) {
                nearestNeighbor(node.right, anchor, nextDim, n, dims);
            }
        } else {
            nearestNeighbor(node.right, anchor, nextDim, n, dims);
            if (diff < n.getBestDist()) {
                nearestNeighbor(node.left, anchor, nextDim, n, dims);
            }
        }
    }

    /**
     * <p>
     * Executes a nearest neighbor query, which returns the nearest neighbor, in
     * terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot;
     * point.
     * </p>
     *
     * <p>
     * Recall that, in the descending phase, a NN query behaves <em>greedily</em>,
     * approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it
     * implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst
     * solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another
     * instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different
     * subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by*
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type
     * ({@link Double}).
     * </p>
     *
     * <p>
     * The main difference of the implementation of this method and the
     * implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using
     * the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.
     * </p>
     *
     * @param k       The total number of neighbors to retrieve. It is better if
     *                this quantity is an odd number, to
     *                avoid ties in Binary Classification tasks.
     * @param anchor  The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor
     *                query.
     * @param currDim The current dimension considered.
     * @param dims    The total number of dimensions considered.
     * @param queue   A {@link BoundedPriorityQueue} that will maintain at most k
     *                nearest neighbors of
     *                the anchor point at all times, sorted by euclideanDistance to
     *                the point.
     *
     * @see BoundedPriorityQueue
     */
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims) {
        kNearestNeighbors(this, k, anchor, queue, currDim, dims);
    }

    private static void kNearestNeighbors(KDTreeNode node, int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue,
            int currDim, int dims) {
        if (node == null) {
            return;
        }
        var nextDim = (currDim + 1) % dims;
        double diff = Math.abs(anchor.coords[currDim] - node.p.coords[currDim]);
        double dist = node.p.euclideanDistance(anchor);

        if (dist < calcDistance(anchor, queue, k) && !node.p.equals(anchor)) {
            queue.enqueue(node.p, dist);
        }

        if (anchor.coords[currDim] < node.p.coords[currDim]) {
            kNearestNeighbors(node.left, k, anchor, queue, nextDim, dims);
            if (diff < calcDistance(anchor, queue, k)) {
                kNearestNeighbors(node.right, k, anchor, queue, nextDim, dims);
            }
        } else {
            kNearestNeighbors(node.right, k, anchor, queue, nextDim, dims);
            if (diff < calcDistance(anchor, queue, k)) {
                kNearestNeighbors(node.left, k, anchor, queue, nextDim, dims);
            }
        }
    }

    private static double calcDistance(KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int k) {
        if (queue.size() < k) {
            return Double.POSITIVE_INFINITY;
        }
        return queue.last().euclideanDistance(anchor);
    }

    /**
     * Returns the height of the subtree rooted at the current node. Recall our
     * definition of height for binary trees:
     * <ol>
     * <li>A null tree has a height of -1.</li>
     * <li>A non-null tree has a height equal to max(height(left_subtree),
     * height(right_subtree))+1</li>
     * </ol>
     * 
     * @return the height of the subtree rooted at the current node.
     */
    public int height() {
        return height;
    }

    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember:
     * {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * 
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint() {
        return p;
    }

    public KDTreeNode getLeft() {
        return left;
    }

    public KDTreeNode getRight() {
        return right;
    }
}
