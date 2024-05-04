package spatial.knnutils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import spatial.exceptions.UnimplementedMethodException;

/**
 * <p>
 * {@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is
 * surpassed,
 * its length is not expanded, but rather the maximum priority element is
 * ejected
 * (which could be the element just attempted to be enqueued).
 * </p>
 *
 * <p>
 * <b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b>
 * </p>
 *
 * @author <a href = "https://github.com/jasonfillipou/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T> {

	/* *********************************************************************** */
	/* ************* PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */

	private TreeSet<PriorityQueueNode<T>> set;
	private int capacity;
	private int counter;

	/* *********************************************************************** */
	/* *************** IMPLEMENT THE FOLLOWING PUBLIC METHODS: ************ */
	/* *********************************************************************** */

	/**
	 * Constructor that specifies the size of our queue.
	 * 
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a
	 *             positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException {
		if (size < 1) {
			throw new IllegalArgumentException();
		}
		set = new TreeSet<PriorityQueueNode<T>>();
		capacity = size;
		counter = 0;
	}

	/**
	 * <p>
	 * Enqueueing elements for BoundedPriorityQueues works a little bit differently
	 * from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at
	 * its
	 * appropriate location in the sequence. On the other hand, if the object is at
	 * capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists,
	 * based on its priority) and
	 * the maximum priority element is ejected from the structure.
	 * </p>
	 * 
	 * @param element  The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, double priority) {
		set.add(new PriorityQueueNode<T>(element, priority, counter++));
		while (set.size() > capacity) {
			set.pollLast();
		}
	}

	@Override
	public T dequeue() {
		if (set.isEmpty()) {
			return null;
		}
		return set.pollFirst().getData();
	}

	@Override
	public T first() {
		if (set.isEmpty()) {
			return null;
		}
		return set.first().getData();
	}

	/**
	 * Returns the last element in the queue. Useful for cases where we want to
	 * compare the priorities of a given quantity with the maximum priority of
	 * our stored quantities. In a minheap-based implementation of any
	 * {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based
	 * implementation,
	 * it takes constant time.
	 * 
	 * @return The maximum priority element in our queue, or null if the queue is
	 *         empty.
	 */
	public T last() {
		if (set.isEmpty()) {
			return null;
		}
		return set.last().getData();
	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * 
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false}
	 *         otherwise.
	 */
	public boolean contains(T element) {
		for (PriorityQueueNode<T> node : this.set) {
			if (node.getData().equals(element)) {
				return true;
			}
		}
		return false;
		// return set.contains(element);
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		// return new Iterator<T>() {
		// List<T> list = set.stream().map(node -> node.getData()).toList();
		// Iterator<T> it = list.iterator();

		// @Override
		// public boolean hasNext() {
		// return it.hasNext();
		// }

		// @Override
		// public T next() {
		// if (set.size() != list.size()) {
		// throw new ConcurrentModificationException();
		// }
		// return it.next();
		// }
		// };
		return new Iterator<T>() {
			private int currIndex = 0;
			int counterCheck = counter;

			@Override
			public boolean hasNext() {
				if (counterCheck != counter) {
					throw new ConcurrentModificationException();
				}
				return currIndex < set.size();
			}

			public T next() {
				ArrayList<PriorityQueueNode<T>> arr = new ArrayList<PriorityQueueNode<T>>();

				for (PriorityQueueNode<T> node : set) {
					arr.add(node);
				}
				if (counterCheck != counter) {
					throw new ConcurrentModificationException();
				}
				currIndex++;
				return arr.get(currIndex).getData();
			}
		};
	}
}
