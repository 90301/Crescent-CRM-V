package debugging.unitTest.testSuiteExecutors;

import ccrmV.LoginView;
import ccrmV.MasterUI;
import clientInfo.DataHolder;
import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;
import users.User;

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
		UnitTestCase loginUITest2 = new UnitTestCase("loginUITest2", "Switches database to test." , DatabaseTestExecutor.TEST_DB, UnitTestCase.TEST_TYPE_OBJECT, this);
		
		User user = masterUi.getUser();
		
		if (!user.getDatabasesAccsessable().contains(DatabaseTestExecutor.TEST_DB)) {
		
			user.addDatabaseAccsessable(DatabaseTestExecutor.TEST_DB);
			DataHolder.store(user, User.class);
		}
		//switch database
	    this.masterUi.setUserDataHolder(DatabaseTestExecutor.TEST_DB);
	    
	    loginUITest2.setActualResult(this.masterUi.userDataHolder.getDatabasePrefix());
		
		return true;
	}

}
