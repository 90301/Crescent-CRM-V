package debugging.unitTest.testSuiteExecutors;

import debugging.unitTest.TestSuiteExecutor;
import integrations.GoogleMail;

public class MailTestExecutor extends TestSuiteExecutor {

	@Override
	public Boolean runTests() {
		GoogleMail.sendMail();
		return true;
	}

}
