package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.PRQuadTree;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants:
 * </p>
 * <ul>
 * <li>Its children pointer buffer is non-null and has a length of 4.</li>
 * <li>If there is at least one black node child, the total number of
 * {@link KDPoint}s stored
 * by <b>all</b> of the children is greater than the bucketing parameter
 * (because if it is equal to it
 * or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p>
 * <b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b>
 * </p>
 *
 * @author --- YOUR NAME HERE! ---
 */
public class PRQuadGrayNode extends PRQuadNode {

    /* ******************************************************************** */
    /* ************* PLACE ANY PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
    private PRQuadNode[][] list;

    /* *********************************************************************** */
    /* *************** IMPLEMENT THE FOLLOWING PUBLIC METHODS: ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode} with the provided {@link KDPoint} as a
     * centroid;
     * 
     * @param centroid       A {@link KDPoint} that will act as the centroid of the
     *                       space spanned by the current
     *                       node.
     * @param k              The See {@link PRQuadTree#PRQuadTree(int, int)} for
     *                       more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by
     *                       {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam) {
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the
                                            // object!
        list = new PRQuadNode[2][2];
    }

    /**
     * <p>
     * Insertion into a {@link PRQuadGrayNode} consists of navigating to the
     * appropriate child
     * and recursively inserting elements into it. If the child is a white node,
     * memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If
     * it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the
     * insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for
     * the appropriate insert to be called
     * based on the child object's runtime object.
     * </p>
     * 
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current
     *          {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b>
     *          {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint} to the
     *          appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after
     *         insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
        if (p.coords[0] < this.centroid.coords[0]) { // point is left of centroid
            if (p.coords[1] >= this.centroid.coords[1]) { // point is above centroid [0][0] NW
                if (this.list[0][0] == null) { // white
                    this.list[0][0] = new PRQuadBlackNode(newCentroid(this.centroid, k, "NW"), k - 1,
                            this.bucketingParam, p);
                    return this;
                } else { // gray or black
                    this.list[0][0] = this.list[0][0].insert(p, k - 1);
                }
            } else { // point is below centroid [1][0] SW
                if (this.list[1][0] == null) { // white
                    this.list[1][0] = new PRQuadBlackNode(newCentroid(this.centroid, k, "SW"), k - 1,
                            this.bucketingParam, p);
                    return this;
                } else { // gray or black
                    this.list[1][0] = this.list[1][0].insert(p, k - 1);
                }
            }
        } else { // point is right of centroid
            if (p.coords[1] >= this.centroid.coords[1]) { // point is above [0][1] NE
                if (this.list[0][1] == null) { // white
                    this.list[0][1] = new PRQuadBlackNode(newCentroid(this.centroid, k, "NE"), k - 1,
                            this.bucketingParam, p);
                    return this;
                } else { // gray or black
                    this.list[0][1] = this.list[0][1].insert(p, k - 1);
                }
            } else { // point is below [1][1] SE
                if (this.list[1][1] == null) { // white
                    this.list[1][1] = new PRQuadBlackNode(newCentroid(this.centroid, k, "SE"), k - 1,
                            this.bucketingParam, p);
                    return this;
                } else { // gray or black
                    this.list[1][1] = this.list[1][1].insert(p, k - 1);
                }
            }
        }
        return this;
    }

    private KDPoint newCentroid(KDPoint currCentroid, int k, String quadrant) {
        KDPoint newCentroid = new KDPoint();
        if (quadrant.equals("NW")) {
            newCentroid.coords[0] = currCentroid.coords[0] - (int) (Math.pow(2, k - 2));
            newCentroid.coords[1] = currCentroid.coords[1] + (int) (Math.pow(2, k - 2));
        } else if (quadrant.equals("NE")) {
            newCentroid.coords[0] = currCentroid.coords[0] + (int) (Math.pow(2, k - 2));
            newCentroid.coords[1] = currCentroid.coords[1] + (int) (Math.pow(2, k - 2));
        } else if (quadrant.equals("SW")) {
            newCentroid.coords[0] = currCentroid.coords[0] - (int) (Math.pow(2, k - 2));
            newCentroid.coords[1] = currCentroid.coords[1] - (int) (Math.pow(2, k - 2));
        } else if (quadrant.equals("SE")) {
            newCentroid.coords[0] = currCentroid.coords[0] + (int) (Math.pow(2, k - 2));
            newCentroid.coords[1] = currCentroid.coords[1] - (int) (Math.pow(2, k - 2));
        } else { // shouldn't get ever here
            return null;
        }
        return newCentroid;
    }

    /**
     * <p>
     * Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of
     * recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no
     * such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree
     * rooted at the current node!</b>
     * </p>
     *
     * <p>
     * Polymorphism will allow for the recursive call to be made into the
     * appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if
     * the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if
     * it has no gray children, and one of the
     * following two conditions are satisfied:
     * </p>
     *
     * <ol>
     * <li>The deletion left it with a single black child. Then, there is no reason
     * to further subdivide the quadrant,
     * and we can replace this with a {@link PRQuadBlackNode} that contains the
     * {@link KDPoint}s that the single
     * black child contains.</li>
     * <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained
     * by <b>all</b> the black children
     * is <b>equal to or smaller than</b> the bucketing parameter. We can then
     * similarly replace this with a
     * {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black
     * children.</li>
     * </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current
     *          node.
     * @return The subtree rooted at the current node, potentially adjusted after
     *         deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
        if (this.search(p) == false) {
            return this;
        }
        if (p.coords[0] < this.centroid.coords[0]) { // point is left of centroid
            if (p.coords[1] >= this.centroid.coords[1]) { // point is above centroid [0][0] NW
                if (this.list[0][0] == null) { // white
                    return null;
                } else { // gray or black
                    this.list[0][0] = this.list[0][0].delete(p);
                    // check if all 4 children are null now
                    if (this.list[0][0] == null && this.list[0][1] == null && this.list[1][0] == null
                            && this.list[1][1] == null) {
                        return null;
                    }
                }
            } else { // point is below centroid [1][0] SW
                if (this.list[1][0] == null) { // white
                    return null;
                } else { // gray or black
                    this.list[1][0] = this.list[1][0].delete(p);
                    // check if all 4 children are null now
                    if (this.list[0][0] == null && this.list[0][1] == null && this.list[1][0] == null
                            && this.list[1][1] == null) {
                        return null;
                    }
                }
            }
        } else { // point is right of centroid
            if (p.coords[1] >= this.centroid.coords[1]) { // point is above [0][1] NE
                if (this.list[0][1] == null) { // white
                    return null;
                } else { // gray or black
                    this.list[0][1] = this.list[0][1].delete(p);
                    // check if all 4 children are null now
                    if (this.list[0][0] == null && this.list[0][1] == null && this.list[1][0] == null
                            && this.list[1][1] == null) {
                        return null;
                    }
                }
            } else { // point is below [1][1] SE
                if (this.list[1][1] == null) { // white
                    return null;
                } else { // gray or black
                    this.list[1][1] = this.list[1][1].delete(p);
                    // check if all 4 children are null now
                    if (this.list[0][0] == null && this.list[0][1] == null && this.list[1][0] == null
                            && this.list[1][1] == null) {
                        return null;
                    }
                }
            }
        }
        return this;
    }

    @Override
    public boolean search(KDPoint p) {
        if (p.coords[0] < this.centroid.coords[0]) { // point is left of centroid
            if (p.coords[1] >= this.centroid.coords[1]) { // point is above centroid [0][0] NW
                if (this.list[0][0] == null) { // white
                    return false;
                } else { // gray or black
                    return this.list[0][0].search(p);
                }
            } else { // point is below centroid [1][0] SW
                if (this.list[1][0] == null) { // white
                    return false;
                } else { // gray or black
                    return this.list[1][0].search(p);
                }
            }
        } else { // point is right of centroid
            if (p.coords[1] >= this.centroid.coords[1]) { // point is above [0][1] NE
                if (this.list[0][1] == null) { // white
                    return false;
                } else { // gray or black
                    return this.list[0][1].search(p);
                }
            } else { // point is below [1][1] SE
                if (this.list[1][1] == null) { // white
                    return false;
                } else { // gray or black
                    return this.list[1][1].search(p);
                }
            }
        }
    }

    @Override
    public int height() {
        int nwCount = (this.list[0][0] == null) ? -1 : this.list[0][0].height();
        int neCount = (this.list[0][1] == null) ? -1 : this.list[0][1].height();
        int swCount = (this.list[1][0] == null) ? -1 : this.list[1][0].height();
        int seCount = (this.list[1][1] == null) ? -1 : this.list[1][1].height();

        int max1 = Math.max(nwCount, neCount);
        int max2 = Math.max(swCount, seCount);
        int max = Math.max(max1, max2);
        return max + 1;

    }

    @Override
    public int count() {
        int nwCount = (this.list[0][0] == null) ? 0 : this.list[0][0].count();
        int neCount = (this.list[0][1] == null) ? 0 : this.list[0][1].count();
        int swCount = (this.list[1][0] == null) ? 0 : this.list[1][0].count();
        int seCount = (this.list[1][1] == null) ? 0 : this.list[1][1].count();

        return nwCount + neCount + swCount + seCount;
    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D
     * array.
     * 
     * @return An array of references to the children of {@code this}. The order is
     *         Z (Morton), like so:
     *         <ol>
     *         <li>0 is NW</li>
     *         <li>1 is NE</li>
     *         <li>2 is SW</li>
     *         <li>3 is SE</li>
     *         </ol>
     */
    public PRQuadNode[] getChildren() {
        PRQuadNode[] children = new PRQuadNode[4];
        children[0] = this.list[0][0]; // NW
        children[1] = this.list[0][1]; // NE
        children[2] = this.list[1][0]; // SW
        children[3] = this.list[1][1]; // SE
        return children;
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
            double range) {
        double nwDist = (this.list[0][0] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[0][0].centroid, anchor).euclideanDistance(anchor);
        double neDist = (this.list[0][1] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[0][1].centroid, anchor).euclideanDistance(anchor);
        double swDist = (this.list[1][0] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[1][0].centroid, anchor).euclideanDistance(anchor);
        double seDist = (this.list[1][1] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[1][1].centroid, anchor).euclideanDistance(anchor);

        if (anchor.coords[0] < this.centroid.coords[0]) { // point is left of centroid
            if (anchor.coords[1] >= this.centroid.coords[1]) { // point is above centroid [0][0] NW
                if (this.list[0][0] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[0][0].range(anchor, results, range);
                    // check if other three are in range
                    if (neDist <= range) {
                        // visit
                        this.list[0][1].range(anchor, results, range);
                    }
                    if (swDist <= range) {
                        // visit
                        this.list[1][0].range(anchor, results, range);
                    }
                    if (seDist <= range) {
                        // visit
                        this.list[1][1].range(anchor, results, range);
                    }
                }
            } else { // point is below centroid [1][0] SW
                if (this.list[1][0] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[1][0].range(anchor, results, range);
                    // check if other three are in range
                    if (nwDist <= range) {
                        // visit
                        this.list[0][0].range(anchor, results, range);
                    }
                    if (neDist <= range) {
                        // visit
                        this.list[0][1].range(anchor, results, range);
                    }
                    if (seDist <= range) {
                        // visit
                        this.list[1][1].range(anchor, results, range);
                    }
                }
            }
        } else { // point is right of centroid
            if (anchor.coords[1] >= this.centroid.coords[1]) { // point is above [0][1] NE
                if (this.list[0][1] == null) { // white
                    return;
                } else { // gray or black
                    this.list[0][1].range(anchor, results, range);
                    // check if other three are in range
                    if (nwDist <= range) {
                        // visit
                        this.list[0][0].range(anchor, results, range);
                    }
                    if (swDist <= range) {
                        // visit
                        this.list[1][0].range(anchor, results, range);
                    }
                    if (seDist <= range) {
                        // visit
                        this.list[1][1].range(anchor, results, range);
                    }
                }
            } else { // point is below [1][1] SE
                if (this.list[1][1] == null) { // white
                    return;
                } else { // gray or black
                    this.list[1][1].range(anchor, results, range);
                    // check if other three are in range
                    if (nwDist <= range) {
                        // visit
                        this.list[0][0].range(anchor, results, range);
                    }
                    if (neDist <= range) {
                        // visit
                        this.list[0][1].range(anchor, results, range);
                    }
                    if (swDist <= range) {
                        // visit
                        this.list[1][0].range(anchor, results, range);
                    }
                }
            }
        }
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n) {
        double nwDist = (this.list[0][0] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[0][0].centroid, anchor).euclideanDistance(anchor);
        double neDist = (this.list[0][1] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[0][1].centroid, anchor).euclideanDistance(anchor);
        double swDist = (this.list[1][0] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[1][0].centroid, anchor).euclideanDistance(anchor);
        double seDist = (this.list[1][1] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[1][1].centroid, anchor).euclideanDistance(anchor);

        if (anchor.coords[0] < this.centroid.coords[0]) { // point is left of centroid
            if (anchor.coords[1] >= this.centroid.coords[1]) { // point is above centroid [0][0] NW
                if (this.list[0][0] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[0][0].nearestNeighbor(anchor, n);
                }
                // check if other three are in range
                if (this.list[0][1] != null && (neDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    // visit
                    this.list[0][1].nearestNeighbor(anchor, n);
                }
                if (this.list[1][0] != null && (swDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    // visit
                    this.list[1][0].nearestNeighbor(anchor, n);
                }
                if (this.list[1][1] != null && (seDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    // visit
                    this.list[1][1].nearestNeighbor(anchor, n);
                }
            } else { // point is below centroid [1][0] SW
                if (this.list[1][0] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[1][0].nearestNeighbor(anchor, n);
                }
                // check if other three are in range
                if (this.list[0][0] != null && (neDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    // visit
                    this.list[0][0].nearestNeighbor(anchor, n);
                }
                if (this.list[0][1] != null && (nwDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    // visit
                    this.list[0][1].nearestNeighbor(anchor, n);
                }
                if (this.list[1][1] != null && (seDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    // visit
                    this.list[1][1].nearestNeighbor(anchor, n);
                }
            }
        } else { // point is right of centroid
            if (anchor.coords[1] >= this.centroid.coords[1]) { // point is above [0][1] NE
                if (this.list[0][1] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[0][1].nearestNeighbor(anchor, n);
                }
                // check if other three are in range
                if (this.list[0][0] != null && (neDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    this.list[0][0].nearestNeighbor(anchor, n);
                }
                if (this.list[1][0] != null && (swDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    this.list[1][0].nearestNeighbor(anchor, n);
                }
                if (this.list[1][1] != null && (seDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    this.list[1][1].nearestNeighbor(anchor, n);
                }
            } else { // point is below [1][1] SE
                if (this.list[1][1] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[1][1].nearestNeighbor(anchor, n);
                }
                // check if other three are in range
                if (this.list[0][0] != null && (neDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    this.list[0][0].nearestNeighbor(anchor, n);
                }
                if (this.list[0][1] != null && (neDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    this.list[0][1].nearestNeighbor(anchor, n);
                }
                if (this.list[1][0] != null && (swDist <= n.getBestDist() || n.getBestDist() == PRQuadTree.INFTY)) {
                    this.list[1][0].nearestNeighbor(anchor, n);
                }
            }
        }
        return n;
    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
        double nwDist = (this.list[0][0] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[0][0].centroid, anchor).euclideanDistance(anchor);
        double neDist = (this.list[0][1] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[0][1].centroid, anchor).euclideanDistance(anchor);
        double swDist = (this.list[1][0] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[1][0].centroid, anchor).euclideanDistance(anchor);
        double seDist = (this.list[1][1] == null) ? Double.MAX_VALUE
                : nearestPointOnSquare(k, this.list[1][1].centroid, anchor).euclideanDistance(anchor);
        double qDist = calcDistance(anchor, queue, k);

        if (anchor.coords[0] < this.centroid.coords[0]) { // point is left of centroid
            if (anchor.coords[1] >= this.centroid.coords[1]) { // point is above centroid [0][0] NW
                if (this.list[0][0] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[0][0].kNearestNeighbors(k, anchor, queue);
                    qDist = calcDistance(anchor, queue, k);
                }
                // check if other three are in range
                if (this.list[0][1] != null && (neDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[0][1].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[1][0] != null && (swDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[1][0].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[1][1] != null && (seDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[1][1].kNearestNeighbors(k, anchor, queue);
                }
            } else { // point is below centroid [1][0] SW
                if (this.list[1][0] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[1][0].kNearestNeighbors(k, anchor, queue);
                    qDist = calcDistance(anchor, queue, k);
                }
                // check if other three are in range
                if (this.list[0][0] != null && (nwDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[0][0].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[0][1] != null && (neDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[0][1].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[1][1] != null && (seDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[1][1].kNearestNeighbors(k, anchor, queue);
                }
            }
        } else { // point is right of centroid
            if (anchor.coords[1] >= this.centroid.coords[1]) { // point is above [0][1] NE
                if (this.list[0][1] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[0][1].kNearestNeighbors(k, anchor, queue);
                    qDist = calcDistance(anchor, queue, k);
                }
                // check if other three are in range
                if (this.list[0][0] != null && (nwDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[0][0].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[1][0] != null && (swDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[1][0].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[1][1] != null && (seDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[1][1].kNearestNeighbors(k, anchor, queue);
                }
            } else { // point is below [1][1] SE
                if (this.list[1][1] == null) { // white
                    // do nothing
                } else { // gray or black
                    this.list[1][1].kNearestNeighbors(k, anchor, queue);
                    qDist = calcDistance(anchor, queue, k);
                }
                // check if other three are in range
                if (this.list[0][0] != null && (nwDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[0][0].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[0][1] != null && (neDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[0][1].kNearestNeighbors(k, anchor, queue);
                }
                if (this.list[1][0] != null && (swDist <= qDist || qDist == PRQuadTree.INFTY)) {
                    this.list[1][0].kNearestNeighbors(k, anchor, queue);
                }
            }
        }
    }

    public KDPoint nearestPointOnSquare(int k, KDPoint centroid, KDPoint anchor) {
        if (centroid == null) {
            return null;
        }
        double length = Math.pow(2, k - 1);

        double left = centroid.coords[0] - length;
        double right = centroid.coords[0] + length;
        double top = centroid.coords[0] + length;
        double bottom = centroid.coords[0] - length;

        double closestX = Math.max(left, Math.min(right, anchor.coords[0]));
        double closestY = Math.max(bottom, Math.min(top, anchor.coords[1]));

        // Determine if the anchor is closer to vertical or horizontal sides
        if (closestX == anchor.coords[0]) {
            // Anchor vertically aligns with the square, adjust y
            closestY = (anchor.coords[1] < centroid.coords[1]) ? bottom : top;
        } else if (closestY == anchor.coords[1]) {
            // Anchor horizontally aligns with the square, adjust x
            closestX = (anchor.coords[0] < centroid.coords[0]) ? left : right;
        }

        // Create the closest point on the perimeter
        return new KDPoint((int) closestX, (int) closestY);
    }

    private static double calcDistance(KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int k) {
        if (queue.size() < k) {
            return Double.POSITIVE_INFINITY;
        }
        return queue.last().euclideanDistance(anchor);
    }
}
