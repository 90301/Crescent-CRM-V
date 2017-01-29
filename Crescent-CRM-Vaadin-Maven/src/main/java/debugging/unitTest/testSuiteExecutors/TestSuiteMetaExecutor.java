package debugging.unitTest.testSuiteExecutors;

import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;

public class TestSuiteMetaExecutor extends TestSuiteExecutor {

	@Override
	public Boolean runTests() {
		
		//Tests a random number
		UnitTestCase randomStringTest1 = new UnitTestCase("random string test 1","Create a random string",
				genRandomString(),genRandomString(),UnitTestCase.TEST_TYPE_NOT_EQUAL,this);
		
		UnitTestCase randomStringTest2 = new UnitTestCase("random string test 2","Create a small random string",
				genSmallRandomString("first"),genSmallRandomString("second"),UnitTestCase.TEST_TYPE_NOT_EQUAL,this);
		
		
		return true;
	}

}
