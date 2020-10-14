
import java.time.Duration;
import java.util.ConcurrentModificationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Attempts to test the {@link SimpleReadWriteLock} and
 * {@link ThreadSafeIndexedSet}. These tests are not 100% accurate. They attempt
 * to create threads in such a way that problems will occur if the
 * implementation is incorrect, but the tests are inexact.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
@TestMethodOrder(Alphanumeric.class)
public class SimpleReadWriteLockTest {

	/** Specifies how long a worker thread should sleep. */
	public static final long WORKER_SLEEP = 500;

	/**
	 * Specifies how long to wait before starting a new worker. Must be less than
	 * {@link #WORKER_SLEEP}.
	 */
	public static final long OFFSET_SLEEP = WORKER_SLEEP / 2;

	/** Short timeout for testing threads. */
	public static final long TIMEOUT_SHORT = Math.round(WORKER_SLEEP * 1.75);

	/** Long timeout for testing threads. */
	public static final long TIMEOUT_LONG = Math.round(WORKER_SLEEP * 2.75);

	/** Used to format debug output when tests fail. */
	private static final String FORMAT = "Expected:%n%s%nActual:%n%s%n";

	/**
	 * Allows these tests to be run within this class.
	 */
	@Nested
	@DisplayName("Group 1: Simple Lock Tests")
	public class Group1_Tests extends StaticSimpleLockTests {

	}

	/**
	 * Allows these tests to be run within this class.
	 */
	@Nested
	@DisplayName("Group 2: Mixed Lock Tests")
	public class Group2_Tests extends StaticMixedLockTests {

	}

	/**
	 * Allows these tests to be run within this class.
	 */
	@Nested
	@DisplayName("Group 3: Multi Lock Tests")
	public class Group3_Tests extends StaticMultiLockTests {

	}

	/**
	 * Allows these tests to be run within this class.
	 */
	@Nested
	@DisplayName("Group 4: Invalid Lock Tests")
	public class Group4_Tests extends StaticInvalidLockTests {

	}

	/**
	 * Simple tests (only read or only write) for the {@link SimpleReadWriteLock} class.  Static to allow extending.
	 */
	@TestMethodOrder(OrderAnnotation.class)
	public static class StaticSimpleLockTests {
		/**
		 * Tests that two threads are able to simultaneously acquire the read lock
		 * without any exceptions. Should also finish in less than 200 milliseconds if
		 * both threads are able to execute simultaneously.
		 *
		 * @throws InterruptedException if interrupted
		 */
		@Test
		@Order(1)
		public void testTwoReaders() throws InterruptedException {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread reader1 = new ReadWorker(lock, buffer);
			Thread reader2 = new ReadWorker(lock, buffer);

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Read Lock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Read Unlock\n");

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_SHORT), () -> {
				reader1.start();
				reader2.start();
				reader2.join();
				reader1.join();
			});

			Assertions.assertEquals(expected.toString(), buffer.toString(), () -> String.format(FORMAT, expected, buffer));
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the write
		 * lock without any exceptions. Should also finish in over 200 milliseconds if
		 * both threads are able to execute simultaneously.
		 *
		 * @throws InterruptedException if interrupted
		 */
		@Test
		@Order(2)
		public void testTwoWriters() throws InterruptedException {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread writer1 = new Thread(new WriteWorker(lock, buffer));
			Thread writer2 = new Thread(new WriteWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_LONG), () -> {
				writer1.start();
				writer2.start();
				writer2.join();
				writer1.join();
			});

			Assertions.assertEquals(expected.toString(), buffer.toString(), () -> String.format(FORMAT, expected, buffer));
		}
	}

	/**
	 * Mixed tests (one read, one write simultaneously) for the {@link SimpleReadWriteLock} class.  Static to allow extending.
	 */
	@TestMethodOrder(OrderAnnotation.class)
	public static class StaticMixedLockTests {
		/**
		 * Tests that two threads are NOT able to simultaneously acquire the read lock
		 * and write lock without any exceptions. Should also finish in over 200
		 * milliseconds if both threads are able to execute simultaneously.
		 *
		 * @throws InterruptedException if interrupted
		 */
		@Test
		@Order(1)
		public void testReaderWriter() throws InterruptedException {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread reader = new Thread(new ReadWorker(lock, buffer));
			Thread writer = new Thread(new WriteWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_LONG), () -> {
				reader.start();

				// wait a little bit before starting next thread
				Thread.sleep(OFFSET_SLEEP);
				writer.start();

				writer.join();
				reader.join();
			});

			Assertions.assertEquals(expected.toString(), buffer.toString(), () -> String.format(FORMAT, expected, buffer));
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the read lock
		 * and write lock without any exceptions. Should also finish in over 200
		 * milliseconds if both threads are able to execute simultaneously.
		 *
		 * @throws InterruptedException if interrupted
		 */
		@Test
		@Order(2)
		public void testWriterReader() throws InterruptedException {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread writer = new Thread(new WriteWorker(lock, buffer));
			Thread reader = new Thread(new ReadWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_LONG), () -> {
				writer.start();

				// wait a little bit before starting next thread
				Thread.sleep(OFFSET_SLEEP);
				reader.start();

				reader.join();
				writer.join();
			});

			Assertions.assertEquals(expected.toString(), buffer.toString(), () -> String.format(FORMAT, expected, buffer));
		}
	}

	/**
	 * Complex tests (multiple read/write locks simultaneously)  for the {@link SimpleReadWriteLock} class.  Static to allow extending.
	 */
	@TestMethodOrder(OrderAnnotation.class)
	public static class StaticMultiLockTests {
		/**
		 * Tests that two threads are NOT able to simultaneously acquire the read lock
		 * and write lock without any exceptions, but multiple threads may acquire
		 * read locks (even if a writer is waiting). Should also finish in over 200
		 * milliseconds if all threads are able to execute properly.
		 *
		 * @throws InterruptedException if interrupted
		 */
		@Test
		@Order(1)
		public void testMultiReadFirst() throws InterruptedException {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread reader1 = new Thread(new ReadWorker(lock, buffer));
			Thread reader2 = new Thread(new ReadWorker(lock, buffer));

			Thread writer1 = new Thread(new WriteWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Read Lock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Read Unlock\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_LONG), () -> {
				reader1.start();

				// wait a little bit before starting next thread
				Thread.sleep(OFFSET_SLEEP);
				writer1.start();
				reader2.start();

				reader2.join();
				writer1.join();
				reader1.join();
			});

			Assertions.assertEquals(expected.toString(), buffer.toString(), () -> String.format(FORMAT, expected, buffer));
		}

		/**
		 * Tests that two threads are NOT able to simultaneously acquire the read lock
		 * and write lock without any exceptions, but multiple threads may acquire
		 * read locks (even if a writer is waiting). Should also finish in over 200
		 * milliseconds if all threads are able to execute properly.
		 *
		 * @throws InterruptedException if interrupted
		 */
		@Test
		@Order(2)
		public void testMultiWriteFirst() throws InterruptedException {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			StringBuffer buffer = new StringBuffer("\n");

			Thread writer1 = new Thread(new WriteWorker(lock, buffer));

			Thread reader1 = new Thread(new ReadWorker(lock, buffer));
			Thread reader2 = new Thread(new ReadWorker(lock, buffer));

			StringBuffer expected = new StringBuffer("\n");
			expected.append("Write Lock\n");
			expected.append("Write Unlock\n");
			expected.append("Read Lock\n");
			expected.append("Read Lock\n");
			expected.append("Read Unlock\n");
			expected.append("Read Unlock\n");

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_LONG), () -> {
				writer1.start();

				// wait a little bit before starting next thread
				Thread.sleep(OFFSET_SLEEP);
				reader1.start();
				reader2.start();

				reader2.join();
				reader1.join();
				writer1.join();
			});

			Assertions.assertEquals(expected.toString(), buffer.toString(), () -> String.format(FORMAT, expected, buffer));
		}
	}

	/**
	 * Tests code fails as expected when locks are used incorrectly. Static to allow extending.
	 */
	@TestMethodOrder(OrderAnnotation.class)
	public static class StaticInvalidLockTests {

		/**
		 * Tests that calling unlock() without first calling lock() for a read lock
		 * throws a {@link IllegalStateException}.
		 */
		@Test
		@Order(1)
		public void testInvalidReadUnlock() {
			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_SHORT), () -> {
				Assertions.assertThrows(IllegalStateException.class, () -> {
					SimpleReadWriteLock lock = new SimpleReadWriteLock();
					lock.readLock().unlock(); // unlock without calling lock
				});
			});
		}

		/**
		 * Tests that calling unlock() without first calling lock() for a write lock
		 * throws a {@link IllegalStateException}.
		 */
		@Test
		@Order(2)
		public void testInvalidWriteUnlock() {
			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_SHORT), () -> {
				Assertions.assertThrows(IllegalStateException.class, () -> {
					SimpleReadWriteLock lock = new SimpleReadWriteLock();
					lock.writeLock().unlock(); // unlock without calling lock
				});
			});
		}

		/**
		 * Tests that calling unlock() twice for a single lock() call for a read lock
		 * throws a {@link IllegalStateException}.
		 */
		@Test
		@Order(3)
		public void testValidInvalidReadUnlock() {
			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_SHORT), () -> {
				Assertions.assertThrows(IllegalStateException.class, () -> {
					SimpleReadWriteLock lock = new SimpleReadWriteLock();
					lock.readLock().lock();
					lock.readLock().unlock();
					lock.readLock().unlock(); // unlock twice
				});
			});
		}

		/**
		 * Tests that calling unlock() twice for a single lock() call for a write lock
		 * throws a {@link IllegalStateException}.
		 */
		@Test
		@Order(4)
		public void testValidInvalidWriteUnlock() {
			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_SHORT), () -> {
				Assertions.assertThrows(IllegalStateException.class, () -> {
					SimpleReadWriteLock lock = new SimpleReadWriteLock();
					lock.writeLock().lock();
					lock.writeLock().unlock();
					lock.writeLock().unlock(); // unlock twice
				});
			});
		}

		/**
		 * Tests that the wrong thread calling unlock() for a write lock throws a
		 * {@link ConcurrentModificationException}.
		 */
		@Test
		@Order(5)
		public void testIncorrectWriterUnlock() {
			Assertions.assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_SHORT), () -> {
				Assertions.assertThrows(ConcurrentModificationException.class, () -> {
					SimpleReadWriteLock lock = new SimpleReadWriteLock();
					StringBuffer buffer = new StringBuffer();

					Thread worker = new WriteWorker(lock, buffer);
					worker.start();

					try {
						Thread.sleep(OFFSET_SLEEP);
					}
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}

					lock.writeLock().unlock();
					worker.join();
				});
			});
		}
	}

	/**
	 * Used to test read operations.
	 */
	private static class ReadWorker extends Thread {

		/** Shared data being protected. */
		private final StringBuffer buffer;

		/** Lock object being used to protect shared data. */
		private final SimpleReadWriteLock lock;

		/**
		 * Initializes a worker with the given lock and buffer.
		 *
		 * @param lock the lock object to use to protect shared data
		 * @param buffer the shared data to use
		 */
		public ReadWorker(SimpleReadWriteLock lock, StringBuffer buffer) {
			this.lock = lock;
			this.buffer = buffer;
		}

		@Override
		public void run() {
			lock.readLock().lock();
			buffer.append("Read Lock\n");

			try {
				Thread.sleep(WORKER_SLEEP);
			}
			catch (Exception ex) {
				buffer.append("Read Error\n");
			}

			buffer.append("Read Unlock\n");
			lock.readLock().unlock();
		}
	}

	/**
	 * Used to test write operations.
	 */
	private static class WriteWorker extends Thread {

		/** Shared data being protected. */
		private final StringBuffer buffer;

		/** Lock object being used to protect shared data. */
		private final SimpleReadWriteLock lock;

		/**
		 * Initializes a worker with the given lock and buffer.
		 *
		 * @param lock the lock object to use to protect shared data
		 * @param buffer the shared data to use
		 */
		public WriteWorker(SimpleReadWriteLock lock, StringBuffer buffer) {
			this.lock = lock;
			this.buffer = buffer;
		}

		@Override
		public void run() {
			lock.writeLock().lock();
			buffer.append("Write Lock\n");

			try {
				Thread.sleep(WORKER_SLEEP);
			}
			catch (Exception ex) {
				buffer.append("Write Error\n");
			}

			buffer.append("Write Unlock\n");
			lock.writeLock().unlock();
		}
	}

}
