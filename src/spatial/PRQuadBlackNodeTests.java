package spatial;

import static org.junit.Assert.*;
import static spatial.kdpoint.KDPoint.*;

import java.util.ArrayList;

import org.junit.Test;

import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.nodes.PRQuadBlackNode;
import spatial.nodes.PRQuadNode;
import spatial.trees.PRQuadTree;

public class PRQuadBlackNodeTests {
    private PRQuadNode black;

    @Test
    public void constructorTest1() {
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 1);
        assertEquals(0, black.count());
        assertFalse(black.search(ONEONE));
    }

    @Test
    public void constructorTest2() {
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 1, ONEONE);
        assertEquals(1, black.count());
        assertEquals(0, black.height());
        assertTrue(black.search(ONEONE));
        assertFalse(black.search(new KDPoint(2, 1)));
        assertTrue(black.getClass().toString().contains("PRQuadBlackNode"));
    }

    @Test
    public void insertTest1() {
        // bucket size of 2
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 2, ONEONE);
        black = black.insert(new KDPoint(2, 2), 3); // center point -- should go top-right
        assertEquals(2, black.count());
        assertEquals(0, black.height());
        assertTrue(black.getClass().toString().contains("PRQuadBlackNode"));
    }

    @Test // Requires GRAY NODE implementation complete
    public void insertTest2() {
        // bucket size of 1
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 1, ONEONE);
        assertEquals(1, black.count());
        black = black.insert(new KDPoint(2, 2), 3); // center point -- should go top-right
        assertEquals(2, black.count());
        assertEquals(2, black.height());
        assertTrue(black.getClass().toString().contains("PRQuadGrayNode"));
        assertTrue(black.search(ONEONE));
        assertTrue(black.search(new KDPoint(2, 2)));
        assertFalse(black.search(new KDPoint(2, 1)));
    }

    @Test
    public void deleteTest1() {
        // bucket size of 2
        insertTest1();
        black = black.delete(ONEONE);
        assertFalse(black.search(ONEONE));
        assertEquals(1, black.count());
        assertEquals(0, black.height());
        black = black.delete(ONEZERO); // no effect
        assertEquals(1, black.count());
        assertEquals(0, black.height());
        black = black.delete(new KDPoint(2, 2)); // last node deleted; returns null
        assertEquals(null, black);
    }

    @Test // Requires GRAY NODE implementation complete
    public void deleteTest2() {
        // bucket size of 1
        insertTest2();
        black = black.delete(ONEONE);
        assertFalse(black.search(ONEONE));
        assertEquals(1, black.count());
        assertEquals(2, black.height());
        black = black.delete(ONEZERO); // no effect
        assertEquals(1, black.count());
        assertEquals(2, black.height());
        black = black.delete(new KDPoint(2, 2)); // last node deleted; returns null
        assertEquals(null, black);
    }

    @Test
    public void rangeTest1() {
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 4, ONEONE);
        black = black.insert(new KDPoint(2, 2), 3);
        black = black.insert(new KDPoint(3, 3), 3);
        assertTrue(black.getClass().toString().contains("PRQuadBlackNode"));

        ArrayList<KDPoint> results = new ArrayList<>();

        black.range(ONEONE, results, 1);
        assertTrue(results.isEmpty()); // shouldn't return the exact match to the anchor point

        black.range(new KDPoint(2, 1), results, 1);
        assertFalse(results.isEmpty());
        assertTrue(results.contains(ONEONE));
        assertTrue(results.contains(new KDPoint(2, 2)));
        assertFalse(results.contains(new KDPoint(4, 4)));
    }

    @Test
    public void nearestNeighborTest1() {
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 4, ONEONE);
        black = black.insert(new KDPoint(2, 2), 3);
        black = black.insert(new KDPoint(3, 3), 3);
        assertTrue(black.getClass().toString().contains("PRQuadBlackNode"));
        var n = new NNData<KDPoint>(null, PRQuadTree.INFTY);
        var nearest = black.nearestNeighbor(ZERO, n);
        assertEquals(ONEONE, nearest.getBestGuess());
        n = new NNData<KDPoint>(null, PRQuadTree.INFTY);
        nearest = black.nearestNeighbor(new KDPoint(7, 7), n);
        assertEquals(new KDPoint(3, 3), nearest.getBestGuess());
    }

    @Test
    public void nearestNeighborsTest1() {
        black = new PRQuadBlackNode(new KDPoint(4, 4), 3, 4, ONEONE);
        black = black.insert(new KDPoint(2, 2), 3);
        black = black.insert(new KDPoint(3, 3), 3);
        assertTrue(black.getClass().toString().contains("PRQuadBlackNode"));
        var q = new BoundedPriorityQueue<KDPoint>(2);
        black.kNearestNeighbors(2, ZERO, q);
        assertTrue(q.contains(ONEONE));
        assertEquals(ONEONE, q.first());
        assertTrue(q.contains(new KDPoint(2, 2)));
        assertFalse(q.contains(new KDPoint(3, 3)));
    }

}