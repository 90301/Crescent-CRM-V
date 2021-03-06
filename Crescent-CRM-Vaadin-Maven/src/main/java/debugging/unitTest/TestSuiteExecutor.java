package debugging.unitTest;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import debugging.DebugObject;
import debugging.Debugging;

/**
 * Extend this class to allow execution of a set of tests
 * The extended class must acquire it's own data-structures
 * (For example UI Components, or User Data Holders if not created
 * within the test suite)
 * @author Owner
 *
 */
public abstract class TestSuiteExecutor {
	
	public static DebugObject defaultTestOutput = new DebugObject(Debugging.CONSOLE_OUTPUT,true,true,"Test Cases: ");
	
	public ArrayList<UnitTestCase> testCases = new ArrayList<UnitTestCase>();
	public DebugObject testResultDebugOutput = defaultTestOutput;//This can be changed to another  debug object if you so desire.
	public abstract Boolean runTests();
	
	
	public boolean hasOutput = false;
	public void debugOutputTestCases() {
		if (!hasOutput) {
		for (UnitTestCase testCase : testCases) {
			Debugging.output(testCase.toString(), testResultDebugOutput );
			testCase.outputProfiling();
		}
		
		hasOutput = true;
		}
	}
	
	public static Random rand = new Random();
	public String genRandomString() {
		String randString = "" + UUID.randomUUID();;
		return randString;
	}
	
	public String genSmallRandomString(String prefix) {
		String randString = prefix + rand.nextInt();
		
		return randString;
	}
}
