package debugging.unitTest;

import java.util.ArrayList;

/**
 * Extend this class to allow execution of a set of tests
 * The extended class must acquire it's own data-structures
 * (For example UI Components, or User Data Holders if not created
 * within the test suite)
 * @author Owner
 *
 */
public abstract class testSuiteExecutor {
	
	public ArrayList<UnitTestCase> testCases = new ArrayList<UnitTestCase>();
	public abstract Boolean runTests();
	
}
