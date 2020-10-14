import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Runs both the {@link SimpleReadWriteLockTest} unit tests and the
 * {@link ThreadSafeIndexedSetTest} unit tests.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
@TestMethodOrder(Alphanumeric.class)
public class ReadWriteLockTest {

	/**
	 * Unit tests from {@link SimpleReadWriteLockTest.StaticSimpleLockTests}.
	 */
	@Nested
	public class A_SimpleLockTests extends SimpleReadWriteLockTest.StaticSimpleLockTests {

	}

	/**
	 * Unit tests from {@link SimpleReadWriteLockTest.StaticMixedLockTests}.
	 */
	@Nested
	public class B_MixedLockTests extends SimpleReadWriteLockTest.StaticMixedLockTests {

	}

	/**
	 * Unit tests from {@link SimpleReadWriteLockTest.StaticMultiLockTests}.
	 */
	@Nested
	public class C_MultiLockTests extends SimpleReadWriteLockTest.StaticMultiLockTests {

	}

	/**
	 * Unit tests from {@link SimpleReadWriteLockTest.StaticInvalidLockTests}.
	 */
	@Nested
	public class D_InvalidLockTests extends SimpleReadWriteLockTest.StaticInvalidLockTests {

	}

	/**
	 * Unit tests for the {@code ThreadSafeIndexedSetTest.StaticApproachTests}.
	 */
	@Nested
	public class E_ApproachTests extends ThreadSafeIndexedSetTest.StaticApproachTests {

	}

	/**
	 * Unit tests for the {@code ThreadSafeIndexedSetTest.StaticAddTests}.
	 */
	@Nested
	public class F_AddTests extends ThreadSafeIndexedSetTest.StaticAddTests {

	}

	/**
	 * Unit tests for the {@code ThreadSafeIndexedSetTest.StaticAddGetTests}.
	 */
	@Nested
	public class G_AddGetTests extends ThreadSafeIndexedSetTest.StaticAddGetTests {

	}

	/**
	 * Unit tests for the {@code ThreadSafeIndexedSetTest.StaticAddGetTests}.
	 */
	@Nested
	public class H_ComplexTests extends ThreadSafeIndexedSetTest.StaticComplexTests {

	}

}
