package debugging.unitTest.testSuiteExecutors;

import debugging.unitTest.TestSuiteExecutor;
import integrations.SuperRest;

public class RestTestExecutor extends TestSuiteExecutor {

	@Override
	public Boolean runTests() {
		
		
		SuperRest.connectToFirebase();
		
		
		return true;
	}

}
