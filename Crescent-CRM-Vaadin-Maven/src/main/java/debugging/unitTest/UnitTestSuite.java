package debugging.unitTest;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds Unit Test Cases
 * These can be executed via "test case executers"
 * 
 * @author Josh Benton / With help from Mark Layton
 *
 */
public class UnitTestSuite {
	
	private static Set<UnitTestCase> testSet = new HashSet<UnitTestCase>();//May need to change this later
	public static void addTestCase(UnitTestCase testCase) {
		testSet.add(testCase);
	}
	
	private static Set<TestSuiteExecutor> tesExecutortSet = new HashSet<TestSuiteExecutor>();//May need to change this later
	public static void addTestExecutor(TestSuiteExecutor testExecutorCase) {
		tesExecutortSet.add(testExecutorCase);
	}
	

}
