package spatial;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spatial.kdpoint.InvalidDimensionalityException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.nodes.PRQuadBlackNode;
import spatial.nodes.PRQuadGrayNode;
import spatial.nodes.PRQuadNode;
import spatial.trees.KDTree;
import spatial.trees.PRQuadTree;
import visualization.CompactVizTree;

import java.util.*;

import static org.junit.Assert.*;
import static spatial.kdpoint.KDPoint.*;

/**
 * <p>
 * A testing framework for {@link spatial.trees.KDTree} and
 * {@link spatial.trees.PRQuadTree}
 * </p>
 * .
 * You should extend it with your own tests.
 *
 * @author --- YOUR NAME HERE! ---
 *
 * @see KDTree
 * @see PRQuadTree
 * @see spatial.trees.SpatialDictionary
 * @see spatial.trees.SpatialQuerySolver
 */

public class StudentTests {

        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *********************************** PRIVATE FIELDS AND METHODS
         * ****************************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */

        private PRQuadTree prQuadTree;
        private KDTree kdTree;
        private static final long SEED = 47;
        private Random r;
        private static final int MAX_ITER = 200;
        private static final int BOUND = 100; // An upper bound for your sampled int coordinates.

        private int getRandomSign() {
                return 2 * r.nextInt(2) - 1;
        }

        private int[] getRandomIntCoords(int dim) {
                int[] coords = new int[dim];
                for (int i = 0; i < dim; i++)
                        coords[i] = getRandomSign() * r.nextInt(BOUND);
                return coords;
        }

        private KDPoint getRandomPoint(int dim) {
                return new KDPoint(getRandomIntCoords(dim)); // This will trigger KDPoint(double[]...) constructor
        }

        private boolean checkRangeQuery(KDTree tree, KDPoint origin, double range, KDPoint... candidates) {
                Collection<KDPoint> rangeQueryResults = tree.range(origin, range);
                List<KDPoint> candidateList = Arrays.asList(candidates);
                return rangeQueryResults.containsAll(candidateList); // Order not important in range queries: only
                                                                     // containment.
        }

        /*
         * Setup and teardown methods; those are run before and after every jUnit test.
         */

        @Before
        public void setUp() {
                r = new Random(SEED);
                prQuadTree = new PRQuadTree(r.nextInt(BOUND), r.nextInt(BOUND));
        }

        @After
        public void tearDown() {
                r = null;
                kdTree = null;
                prQuadTree = null;
                System.gc();
        }

        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * ***************************************** BPQ Tests
         * *************************************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */

        @Test(expected = IllegalArgumentException.class)
        public void testBPQZeroCapacityProvided() {
                new BoundedPriorityQueue<>(0);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testBPQNegativeCapacityProvided() {
                new BoundedPriorityQueue<>(-1);
        }

        @Test
        public void testBPQBasicEnqueueDequeueFirstAndLast() {
                BoundedPriorityQueue<KDPoint> myQueue = new BoundedPriorityQueue<>(1);
                myQueue.enqueue(ZERO, 2.3);
                assertEquals(
                                "After enqueueing a single KDPoint in a BPQ instance with a capacity of 1, a call to first() did not return "
                                                +
                                                "the point itself.",
                                ZERO, myQueue.first());
                assertEquals(
                                "After enqueueing a single KDPoint in a BPQ instance with a capacity of 1, a call to last() did not return "
                                                +
                                                "the point itself.",
                                ZERO, myQueue.last());
                assertEquals(
                                "After enqueueing a single KDPoint in a BPQ instance with a capacity of 1, a call to dequeue() did not return "
                                                +
                                                "the point itself.",
                                ZERO, myQueue.dequeue());
        }

        @Test
        public void testBPQComplexEnqueueDequeueFirstAndLast() {
                BoundedPriorityQueue<KDPoint> myQueue = new BoundedPriorityQueue<>(3);
                myQueue.enqueue(ZERO, 2.3);
                myQueue.enqueue(ONEONE, 1.1);
                assertEquals(
                                "After enqueueing two KDPoints in a BPQ instance with a capacity of 3, a call to first() did not return "
                                                +
                                                "the expected point.",
                                ONEONE, myQueue.first());
                assertEquals(
                                "After enqueueing two KDPoints in a BPQ instance with a capacity of 3, a call to last() did not return "
                                                +
                                                "the expected point.",
                                ZERO, myQueue.last());
                assertEquals(
                                "After enqueueing two KDPoints in a BPQ instance with a capacity of 3, a call to dequeu() did not return "
                                                +
                                                "the expected point.",
                                ONEONE, myQueue.dequeue());
                myQueue.enqueue(MINUSONEMINUSONE, 4);
                assertEquals(
                                "After enqueueing two KDPoints in a BPQ instance with a capacity of 3, dequeuing one and enqueuing another, "
                                                +
                                                "a call to last() did not return the expected point.",
                                MINUSONEMINUSONE, myQueue.last());

        }

        @Test
        public void testBPQEnqueuePastCapacity() {
                BoundedPriorityQueue<KDPoint> myQueue = new BoundedPriorityQueue<>(5);
                myQueue.enqueue(ZERO, 1);
                myQueue.enqueue(ONEONE, 2);
                myQueue.enqueue(ONEMINUSONE, 3);
                myQueue.enqueue(MINUSONEONE, 4);
                myQueue.enqueue(ZEROMINUSONE, 5);
                myQueue.enqueue(ONEZERO, 5); // FIFO should keep this one away
                assertEquals("After enqueuing six elements in a BPQ with initial capacity 5, a call to last() did not return "
                                +
                                "the expected element.", ZEROMINUSONE, myQueue.last());
                myQueue.enqueue(ONEZERO, 0.5); // The BPQ's sorting should put this first.
                myQueue.enqueue(ZEROONE, 1.5); // And this third.
                assertEquals(
                                "After enqueuing eight elements in a BPQ with initial capacity 5, we would expect its size to still be"
                                                +
                                                "5.",
                                5, myQueue.size());
                assertEquals(
                                "After enqueuing eight elements in a BPQ with initial capacity 5, a call to dequeue() did not return "
                                                +
                                                "the expected element.",
                                ONEZERO, myQueue.dequeue()); // Two previous last ones must have been thrown out.
                assertEquals(
                                "After enqueuing eight elements in a BPQ with initial capacity 5 and one dequeueing, our second call to dequeue() did not return "
                                                +
                                                "the expected element.",
                                ZERO, myQueue.dequeue());
                assertEquals(
                                "After enqueuing eight elements in a BPQ with initial capacity 5 and two dequeueings, our third call to dequeue() did not return "
                                                +
                                                "the expected element.",
                                ZEROONE, myQueue.dequeue());
        }

        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * ***************************************** KD-TREE TESTS
         * *************************************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        @Test
        public void testKDTreeIsEmpty() {
                kdTree = new KDTree(10);
                assertTrue("A freshly created KD-Tree should be empty!", kdTree.isEmpty());
        }

        @Test
        public void testKDTreeFewInsertions() {
                kdTree = new KDTree(2);
                kdTree.insert(new KDPoint(10, 30));
                kdTree.insert(new KDPoint(12, 18));
                kdTree.insert(new KDPoint(-20, 300));

                assertEquals("The first point inserted should be our root.", new KDPoint(10, 30), kdTree.getRoot());
                assertEquals("The height of this KD-Tree should be 1.", 1, kdTree.height());
                assertEquals("The number of nodes in this tree should be 3.", 3, kdTree.count());
        }

        @Test
        public void testKDTreeSimpleRange() throws InvalidDimensionalityException {
                int MAX_DIM = 10;
                for (int dim = 1; dim <= MAX_DIM; dim++) { // For MAX_DIM-many trees...
                        KDTree tree = new KDTree(dim);
                        for (int i = 0; i < MAX_ITER; i++) { // For MAX_ITER-many points...
                                KDPoint originInDim = KDPoint.getOriginInDim(dim);
                                KDPoint p = getRandomPoint(dim);
                                tree.insert(p);
                                assertTrue("Failed a range query for a " + dim + "-D tree which only contained " +
                                                p + ", KDPoint #" + i + ".",
                                                checkRangeQuery(tree, originInDim, p.euclideanDistance(originInDim)));
                        }
                }
        }

        @Test
        public void testKDTreeNN() {
                KDTree kdTree = new KDTree(1);
                kdTree.insert(new KDPoint(9));
                kdTree.insert(new KDPoint(3));
                kdTree.insert(new KDPoint(5));
                kdTree.insert(new KDPoint(16));
                kdTree.insert(new KDPoint(15));
                kdTree.insert(new KDPoint(25));

                assertEquals(new KDPoint(5), kdTree.nearestNeighbor(new KDPoint(9)));
        }

        @Test
        public void testKDTreeKNN() {
                KDTree kdTree = new KDTree(1);
                kdTree.insert(new KDPoint(9));
                kdTree.insert(new KDPoint(3));
                kdTree.insert(new KDPoint(5));
                kdTree.insert(new KDPoint(16));
                kdTree.insert(new KDPoint(15));
                kdTree.insert(new KDPoint(25));

                assertEquals(new KDPoint(15), kdTree.kNearestNeighbors(1, new KDPoint(16)).first());
        }

        @Test
        public void testKDTree2() {
                // https://www.geeksforgeeks.org/search-and-insertion-in-k-dimensional-tree/
                KDTree kdTree = new KDTree(2);
                kdTree.insert(new KDPoint(3, 6));
                kdTree.insert(new KDPoint(17, 15));
                kdTree.insert(new KDPoint(13, 15));
                kdTree.insert(new KDPoint(6, 12));
                kdTree.insert(new KDPoint(9, 1));
                kdTree.insert(new KDPoint(2, 7));
                kdTree.insert(new KDPoint(10, 19));

                assertEquals(new KDPoint(13, 15), kdTree.nearestNeighbor(new KDPoint(14, 12)));
                var knn = kdTree.kNearestNeighbors(3, new KDPoint(14, 17));
                assertEquals(new KDPoint(13, 15), knn.first());

                assertEquals(true, kdTree.search(new KDPoint(13, 15)));
                assertEquals(false, kdTree.search(new KDPoint(13, 14)));
        }

        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * ***************************************** PR-QUADTREE TESTS
         * ********************************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        /*
         * *****************************************************************************
         * ***************************
         */
        @Test
        public void testPRQEmptyPRQuadTree() {
                assertNotNull("Tree reference should be non-null by setUp() method.", prQuadTree);
                assertTrue("A freshly created PR-QuadTree should be empty!", prQuadTree.isEmpty());
        }

        @Test
        public void testPRQSimpleQuadTree() {
                prQuadTree = new PRQuadTree(4, 2); // Space from (-8, -8) to (8, 8), bucketing parameter = 2.
                prQuadTree.insert(new KDPoint(1, 1));
                prQuadTree.insert(new KDPoint(4, 2)); // Should fit
                assertEquals(
                                "After two insertions into a PR-QuadTree with b = 2, the result should be a quadtree consisting of a single black node.",
                                0, prQuadTree.height());
                assertEquals("After two insertions into a PR-QuadTree, the count should be 2.", 2, prQuadTree.count());

                // The following deletion should work just fine...
                try {
                        prQuadTree.delete(new KDPoint(1, 1));
                } catch (Throwable t) {
                        fail("Caught a " + t.getClass().getSimpleName() + " with message: " + t.getMessage()
                                        + " when attempting to delete a KDPoint that *should*" +
                                        " be in the PR-QuadTree.");
                }
                assertFalse("After deleting a point from a PR-QuadTree, we should no longer be finding it in the tree.",
                                prQuadTree.search(new KDPoint(1, 1)));

                // The following two insertions should split the root node into a gray node with
                // 2 black node children and 2 white node children.
                prQuadTree.insert(new KDPoint(-5, -6));
                prQuadTree.insert(new KDPoint(0, 0)); // (0, 0) should go to the NE quadrant after splitting.
                assertEquals(
                                "After inserting three points into a PR-QuadTree with b = 2, the tree should split into a gray node with 4 children.",
                                prQuadTree.height(), 1);
                for (KDPoint p : new KDPoint[] { new KDPoint(0, 0), new KDPoint(4, 2), new KDPoint(-5, -6) })
                        assertTrue(
                                        "After inserting a point into a PR-QuadTree without subsequently deleting it, we should be able to find it.",
                                        prQuadTree.search(p));

        }

        @Test
        public void testKNNPRQuadTree() {

                int k = 4;
                int kNN = 3;
                int bucketingParam = 2;
                prQuadTree = new PRQuadTree(k, bucketingParam); // Space from (-8, -8) to (8, 8), bucketing parameter =
                                                                // 2.
                KDPoint[] points = { new KDPoint(1, 1), new KDPoint(2, 2), new KDPoint(3, 3),
                                new KDPoint(-2, 2), new KDPoint(-1, 2), new KDPoint(-2, 6),
                };
                for (int i = 1; i < points.length; i++)
                        prQuadTree.insert(points[i]);

                KDPoint queryPt = new KDPoint(-1, 4);

                BoundedPriorityQueue<KDPoint> expectedKnnPoints = new BoundedPriorityQueue<>(kNN + 1);
                expectedKnnPoints.enqueue(new KDPoint(-1, 2), 2);
                expectedKnnPoints.enqueue(new KDPoint(-2, 2), 2.236068);
                expectedKnnPoints.enqueue(new KDPoint(-2, 6), 2.236068);
                // expectedKnnPoints.enqueue(new KDPoint(-2.0, 7.0),2.236068);

                BoundedPriorityQueue<KDPoint> knnPoints = prQuadTree.kNearestNeighbors(kNN, queryPt);
                assertEquals("Expected KNN result to have " + expectedKnnPoints.size()
                                + " elements but it actually have "
                                + knnPoints.size() + " elements", expectedKnnPoints.size(), knnPoints.size());
                KDPoint actualPoint;
                for (int i = 0; i < kNN; i++) {
                        actualPoint = knnPoints.dequeue();
                        assertTrue("KNN result contains " + actualPoint + ", which was not expected",
                                        expectedKnnPoints.contains(actualPoint));
                }

        }

        @Test
        public void testNNPRQuadTree() {
                prQuadTree = new PRQuadTree(4, 2); // Space from (-8, -8) to (8, 8), bucketing parameter = 2.
                KDPoint[] points = { new KDPoint(1, 1) };

                prQuadTree.insert(points[0]);
                KDPoint nn;

                nn = prQuadTree.nearestNeighbor(points[0]);
                assertNull(
                                "nearestNeighbor check; Expected null but actual value is not null. Make sure the code does not include query point in the result",
                                nn);

                nn = prQuadTree.nearestNeighbor(new KDPoint(points[0].coords[0], points[0].coords[1] + 1));
                assertEquals("nearestNeighbor check failed. ", nn, points[0]);
        }

        @Test
        public void testPRQRange() {
                prQuadTree = new PRQuadTree(4, 2); // Space from (-8, -8) to (8, 8), bucketing parameter = 2.
                KDPoint point = ONEONE;
                prQuadTree.insert(point);
                ArrayList<KDPoint> ptsWithinRange = new ArrayList<>(prQuadTree.range(ZERO, 0.5));
                assertEquals("PR-QuadTree contains (1, 1) and a range query from (0, 0) with a range of 0.5 " +
                                "should not be sufficient to include (1, 1)",
                                0, ptsWithinRange.size());
                ptsWithinRange = new ArrayList<>(prQuadTree.range(ZERO, euclideanDistance(ZERO, point)));
                assertTrue("PR-QuadTree contains (1, 1) and a range query from (0, 0) with a range of sqrt(2) + EPS " +
                                "should be sufficient to include (1, 1)",
                                ptsWithinRange.size() == 1 && ptsWithinRange.get(0).equals(point));

                // Inserting (0, 0) should *not* change anything, because we never report the
                // anchor point.
                prQuadTree.insert(point);
                ptsWithinRange = new ArrayList<>(prQuadTree.range(ZERO, euclideanDistance(ZERO, point)));
                assertTrue("PR-QuadTree contains (1, 1) and (0, 0). A range query from (0, 0) with a range " +
                                "of sqrt(2) + EPS should be sufficient to include (1, 1) but *not* report (0, 0).",
                                ptsWithinRange.size() == 1 && ptsWithinRange.get(0).equals(point));
        }

        /**
         * This &quot;test&quot; just gives an example for how to generate a KD-tree
         * visualization using {@link CompactVizTree}.
         * If successful, an image named <tt>compact_kdtree.png</tt> should be saved
         * inside your project directory
         * Please make sure to delete these image files before submission. We give it to
         * you as a {@code jUnit} test
         * in order for it to run automatically whenever you run the full suite.
         */
        @Test
        public void testKDTreeViz() {
                kdTree = new KDTree(2);
                KDPoint[] points = { new KDPoint(10, 30),
                                new KDPoint(12, 18),
                                new KDPoint(-20, 300),
                                new KDPoint(16, 100),
                                new KDPoint(10, 500),
                                new KDPoint(18, 500),
                };

                for (int i = 1; i < points.length; i++)
                        kdTree.insert(points[i]);

                ArrayList<String> kdDescription = kdTree.treeDescription(false);
                CompactVizTree visualizer = new CompactVizTree(120, 40, 10);
                visualizer.drawBinaryTreeToFile(kdDescription, "compact_kdtree");

        }

        /**
         * This "test" just gives an example for how to generate a Quadtree
         * visualization using {@link CompactVizTree}.
         * If successful, an image named {@code compact_quadtree.png} should be created.
         * Please make sure to delete these image files before submission. We give it to
         * you as a {@code jUnit} test
         * in order for it to run automatically whenever you run the full suite.
         */
        @Test
        public void testPRTreeViz() {
                prQuadTree = new PRQuadTree(4, 2); // Space from (-8, -8) to (8, 8), bucketing parameter = 2.

                KDPoint[] points = { new KDPoint(1, 1),
                                new KDPoint(4, 2),
                                new KDPoint(7, 2),
                                new KDPoint(7, 7),
                                new KDPoint(2, 7),
                                new KDPoint(-1, -7),
                                new KDPoint(-1, 7),
                                new KDPoint(7, -7),
                                new KDPoint(5, 2),
                                new KDPoint(1, 2),
                                new KDPoint(-2, 2),
                                new KDPoint(-2, 1)
                };

                for (int i = 1; i < points.length; i++)
                        prQuadTree.insert(points[i]);

                ArrayList<String> kdDescription = prQuadTree.treeDescription(false);
                // VizTree visualizer = new VizTree();
                CompactVizTree visualizer = new CompactVizTree(120, 120, 10);
                visualizer.drawBTreeToFile(kdDescription, 4, "compact_quadtree");

        }

        @Test
        public void comprehensivePRQuadTreeTest1() {
                var tree = new PRQuadTree(8, 1);
                KDPoint[] points = {
                                new KDPoint(100, 125),
                                new KDPoint(25, -30),
                                new KDPoint(-55, 80),
                                new KDPoint(125, -60),
                                new KDPoint(80, 80), // kills it
                                new KDPoint(-80, -8),
                                new KDPoint(-12, -112),
                                new KDPoint(-48, -112),
                                new KDPoint(16, 72),
                                new KDPoint(60, 100),
                                new KDPoint(48, 48),
                                new KDPoint(36, 8),
                                new KDPoint(4, 60),
                                new KDPoint(28, 30),
                                new KDPoint(125, 125),
                                new KDPoint(128, 128)
                };
                for (int i = 0; i < points.length; i++) {
                        tree.insert(points[i]);
                        assertEquals(i + 1, tree.count());
                        assertTrue(tree.search(points[i]));
                }
                assertEquals(points.length, tree.count());
                assertEquals(7, tree.height());
                tree.delete(new KDPoint(128, 128));
                assertEquals(points.length - 1, tree.count());
                assertEquals(7, tree.height());
                tree.delete(new KDPoint(125, 125));
                assertEquals(points.length - 2, tree.count());
                assertEquals(4, tree.height());
        }

        // SHOULD THROW EXCEPTION
        // @Test
        // public void comprehensivePRQuadTreeTest2() {
        // var tree = new PRQuadTree(8, 1);
        // int count = 0;
        // for (int x = -128; x < 129; x++) {
        // for (int y = -128; y < 129; y++) {
        // tree.insert(new KDPoint(x, y));
        // count++;
        // }
        // }
        // assertEquals(count, tree.count());
        // }

        @Test
        public void testKDTreeDeleteLeafNode() {
                kdTree = new KDTree(2);
                KDPoint[] points = { new KDPoint(10, 30), new KDPoint(12, 18), new KDPoint(-20, 300) };
                for (KDPoint point : points) {
                        kdTree.insert(point);
                }

                // Assuming (12, 18) is a leaf node in this setup
                kdTree.delete(new KDPoint(12, 18));
                assertFalse("KDTree should not contain the deleted leaf node.", kdTree.search(new KDPoint(12, 18)));
                // Additional assertions can be made here to check the integrity of the tree
                // structure
        }

        @Test
        public void testKDTreeDeleteNodeWithTwoChildren() {
                kdTree = new KDTree(2);
                KDPoint[] points = { new KDPoint(10, 10), new KDPoint(5, 5), new KDPoint(15, 15), new KDPoint(12, 12),
                                new KDPoint(18, 18) };
                for (KDPoint point : points) {
                        kdTree.insert(point);
                }

                // Assuming (10, 10) has two children
                kdTree.delete(new KDPoint(10, 10));
                assertFalse("KDTree should not contain the deleted node.", kdTree.search(new KDPoint(10, 10)));
                // Additional assertions to check tree structure and invariants
        }

        @Test
        public void testKDTreeDeleteRootNode() {
                kdTree = new KDTree(2);
                KDPoint[] points = { new KDPoint(20, 20), new KDPoint(10, 10), new KDPoint(30, 30), new KDPoint(5, 5) };
                for (KDPoint point : points) {
                        kdTree.insert(point);
                }

                // Assuming (20, 20) is the root node
                kdTree.delete(new KDPoint(20, 20));
                assertFalse("KDTree should not contain the deleted root node.", kdTree.search(new KDPoint(20, 20)));
                // Additional assertions to validate the new structure of the tree
        }

        @Test
        public void testKDTreeDeleteDeepTree() {
                kdTree = new KDTree(2);
                KDPoint[] points = { new KDPoint(10, 10), new KDPoint(5, 5), new KDPoint(15, 15), new KDPoint(12, 12),
                                new KDPoint(18, 18), new KDPoint(17, 17) };
                for (KDPoint point : points) {
                        kdTree.insert(point);
                }

                // Delete a node that causes re-structuring in the deeper part of the tree
                kdTree.delete(new KDPoint(15, 15));
                assertFalse("KDTree should not contain the deleted node.", kdTree.search(new KDPoint(15, 15)));
                // Additional checks for tree structure
        }

        @Test
        public void testBasicNearestNeighbor() {
                prQuadTree = new PRQuadTree(4, 2);
                KDPoint[] points = { new KDPoint(1, 1), new KDPoint(2, 2), new KDPoint(3, 3) };
                for (KDPoint point : points) {
                        prQuadTree.insert(point);
                }
                KDPoint queryPoint = new KDPoint(4, 4);
                KDPoint nn = prQuadTree.nearestNeighbor(queryPoint);
                assertEquals("Nearest neighbor to (4, 4) should be (3, 3).", new KDPoint(3, 3), nn);
        }

        @Test
        public void testNearestNeighborSamePoint() {
                prQuadTree = new PRQuadTree(4, 2);
                KDPoint point = new KDPoint(1, 1);
                prQuadTree.insert(point);
                KDPoint nn = prQuadTree.nearestNeighbor(point);
                assertNull("Nearest neighbor to the only point in the tree should be null.", nn);
        }

        @Test
        public void testNearestNeighborEdgeCase() {
                prQuadTree = new PRQuadTree(4, 2);
                KDPoint[] points = { new KDPoint(-1, -1), new KDPoint(1, 1), new KDPoint(-2, -2) };
                for (KDPoint point : points) {
                        prQuadTree.insert(point);
                }
                KDPoint queryPoint = new KDPoint(0, 0);
                KDPoint nn = prQuadTree.nearestNeighbor(queryPoint);
                assertEquals("Nearest neighbor to (0, 0) should be (1, 1).", new KDPoint(1, 1), nn);
        }

        @Test
        public void testNearestNeighborWithTies() {
                prQuadTree = new PRQuadTree(4, 2);
                KDPoint[] points = { new KDPoint(1, 1), new KDPoint(-1, -1), new KDPoint(1, -1), new KDPoint(-1, 1) };
                for (KDPoint point : points) {
                        prQuadTree.insert(point);
                }
                KDPoint queryPoint = new KDPoint(0, 0);
                KDPoint nn = prQuadTree.nearestNeighbor(queryPoint);
                // Asserting that one of the points equidistant to the query point is returned
                assertTrue("Nearest neighbor to (0, 0) should be one of the four equidistant points.",
                                Arrays.asList(points).contains(nn));
        }

        @Test
        public void testSinglePoint() {
                PRQuadTree tree = new PRQuadTree(4, 2); // Customize dimensions and bucketing parameter as needed
                KDPoint singlePoint = new KDPoint(5, 5);
                tree.insert(singlePoint);

                // Test with the same point
                KDPoint nnSame = tree.nearestNeighbor(singlePoint);
                assertNull("Expected null, but found a nearest neighbor", nnSame);

                // Test with a different point
                KDPoint diffPoint = new KDPoint(6, 6);
                KDPoint nnDiff = tree.nearestNeighbor(diffPoint);
                assertEquals("Expected the single point in the tree to be the nearest neighbor", singlePoint, nnDiff);
        }

        @Test
        public void testBPQIterator() {
                BoundedPriorityQueue<KDPoint> queue = new BoundedPriorityQueue<>(4);
                queue.enqueue(new KDPoint(1, 1), 1);
                queue.enqueue(new KDPoint(2, 2), 2);
                queue.enqueue(new KDPoint(3, 3), 3);
                queue.enqueue(new KDPoint(4, 4), 4);
                for (int i = 0; i < 4; i++) {
                        assertTrue(queue.iterator().hasNext());
                }

        }

        @Test
        public void testBlackMerge() {
                PRQuadNode black = new PRQuadBlackNode(new KDPoint(0, 0), 3, 1);
                black = black.insert(new KDPoint(1, 1), 3);
                black = black.insert(new KDPoint(-1, -1), 3);
                assertTrue(black.getClass().toString().contains("PRQuadGrayNode"));
                black = black.delete(new KDPoint(1, 1));
                assertTrue(black.getClass().toString().contains("PRQuadBlackNode"));

        }
}
