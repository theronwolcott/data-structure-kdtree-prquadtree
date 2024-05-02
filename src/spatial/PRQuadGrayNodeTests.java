package spatial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static spatial.kdpoint.KDPoint.*;

import java.util.ArrayList;

import org.junit.Test;

import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.nodes.PRQuadGrayNode;
import spatial.nodes.PRQuadNode;
import spatial.trees.PRQuadTree;

public class PRQuadGrayNodeTests {
    PRQuadNode gray;

    @Test
    public void constructorTest1() {
        gray = new PRQuadGrayNode(new KDPoint(4, 4), 3, 1);
        assertEquals(0, gray.count());
        assertEquals(0, gray.height());
    }

    @Test
    public void insertTest1() {
        gray = new PRQuadGrayNode(new KDPoint(4, 4), 3, 1);
        gray = gray.insert(ONEONE, 3);
        assertEquals(1, gray.count());
        assertEquals(1, gray.height());
    }

    @Test
    public void insertTest2() {
        gray = new PRQuadGrayNode(new KDPoint(4, 4), 3, 1);
        gray = gray.insert(ONEONE, 3);
        gray = gray.insert(new KDPoint(6, 6), 3);
        assertEquals(2, gray.count());
        assertEquals(1, gray.height());
    }

    @Test
    public void insertTest3() {
        gray = new PRQuadGrayNode(new KDPoint(4, 4), 3, 1);
        gray = gray.insert(ONEONE, 3);
        gray = gray.insert(new KDPoint(6, 6), 3);
        gray = gray.insert(new KDPoint(3, 3), 3);
        assertEquals(3, gray.count());
        assertEquals(2, gray.height());
    }

    @Test
    public void deleteTest1() {
        insertTest1();
        gray = gray.delete(ONEONE);
        assertEquals(null, gray);
    }

    @Test
    public void deleteTest2() {
        insertTest2();
        gray = gray.delete(ONEONE);
        assertEquals(1, gray.count());
        assertEquals(1, gray.height());
        gray = gray.delete(new KDPoint(6, 6));
        assertEquals(null, gray);
    }

    @Test
    public void deleteTest3() {
        insertTest3();
        gray = gray.delete(ONEONE);
        assertEquals(2, gray.count());
        assertEquals(2, gray.height());
        gray = gray.delete(new KDPoint(6, 6));
        assertEquals(1, gray.count());
        assertEquals(2, gray.height());
        gray = gray.delete(new KDPoint(3, 3));
        assertEquals(null, gray);
    }

    @Test
    public void searchTest1() {
        insertTest3();
        assertTrue(gray.search(ONEONE));
        assertTrue(gray.search(new KDPoint(6, 6)));
        assertTrue(gray.search(new KDPoint(3, 3)));
        assertFalse(gray.search(new KDPoint(3, 4)));
    }

    @Test
    public void rangeTest1() {
        insertTest3();
        ArrayList<KDPoint> results = new ArrayList<>();
        gray.range(ONEONE, results, 1);
        // assertTrue(results.isEmpty()); // shouldn't return the exact match to the
        // anchor point
        gray.range(new KDPoint(4, 5), results, 3);
        assertFalse(results.isEmpty());
        assertTrue(results.contains(new KDPoint(3, 3)));
        assertTrue(results.contains(new KDPoint(6, 6)));
        // assertFalse(results.contains(ONEONE));
    }

    @Test
    public void nearestNeighborTest1() {
        insertTest3();
        var n = new NNData<KDPoint>(null, PRQuadTree.INFTY);
        var nearest = gray.nearestNeighbor(ZERO, n);
        assertEquals(ONEONE, nearest.getBestGuess());
        n = new NNData<KDPoint>(null, PRQuadTree.INFTY);
        nearest = gray.nearestNeighbor(new KDPoint(7, 7), n);
        assertEquals(new KDPoint(6, 6), nearest.getBestGuess());

    }

    @Test
    public void nearestNeighborsTest1() {
        insertTest3();
        var q = new BoundedPriorityQueue<KDPoint>(2);
        gray.kNearestNeighbors(2, ZERO, q);
        assertTrue(q.contains(ONEONE));
        assertEquals(ONEONE, q.first());
        assertTrue(q.contains(new KDPoint(3, 3)));
        assertFalse(q.contains(new KDPoint(6, 6)));

    }
}