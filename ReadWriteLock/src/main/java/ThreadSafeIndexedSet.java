/**
 * A thread-safe version of {@link IndexedSet} using a read/write lock.
 *
 * @param <E> element type
 * @see IndexedSet
 * @see SimpleReadWriteLock
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class ThreadSafeIndexedSet<E> extends IndexedSet<E> {

	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes an unsorted thread-safe indexed set.
	 */
	public ThreadSafeIndexedSet() {
		// NOTE: DO NOT MODIFY THIS METHOD
		this(false);
	}

	/**
	 * Initializes a thread-safe indexed set.
	 *
	 * @param sorted whether the set should be sorted
	 */
	public ThreadSafeIndexedSet(boolean sorted) {
		// NOTE: DO NOT MODIFY THIS METHOD
		super(sorted);
		lock = new SimpleReadWriteLock();
	}

	/**
	 * Returns the identity hashcode of the lock object. Not particularly useful.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		// NOTE: DO NOT MODIFY THIS METHOD
		return System.identityHashCode(lock);
	}

	// TODO Override methods below as necessary to create a thread-safe indexed set.
}
