package debugging.unitTest.testSuiteExecutors;

import ccrmV.LoginView;
import ccrmV.MasterUI;
import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;

public class LoginTestExecutor extends TestSuiteExecutor {

	LoginView lView;
	MasterUI masterUi;
	
	
	
	public LoginTestExecutor(LoginView lView, MasterUI masterUi) {
		super();
		this.lView = lView;
		this.masterUi = masterUi;
	}



	@Override
	public Boolean runTests() {
		
		
		
		lView.userField.setValue("ccrmUser");
		lView.passField.setValue("ccrmPass");
		
		lView.attemptLogin();
		
		UnitTestCase loginUITest1 = new UnitTestCase("loginUITest1", "Attempts to login." , true, masterUi.loggedIn, UnitTestCase.TEST_TYPE_OBJECT, this);
		
		
		
		return true;
	}

}
