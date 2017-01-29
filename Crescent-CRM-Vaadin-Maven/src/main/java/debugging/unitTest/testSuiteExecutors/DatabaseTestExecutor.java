package debugging.unitTest.testSuiteExecutors;

import clientInfo.DataHolder;
import clientInfo.UserDataHolder;
import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;

public class DatabaseTestExecutor extends TestSuiteExecutor {

	@Override
	public Boolean runTests() {
		
		//Create Test cases
		UnitTestCase createDatabaseTest1 = new UnitTestCase("createDatabaseTest1", "Ensures prefix for the user data holder works correctly", "test", UnitTestCase.TEST_TYPE_STRING_CONTAINS);
		this.testCases.add(createDatabaseTest1);
		
		//Execute code for tests
		

		UserDataHolder udhTest = new UserDataHolder();
		udhTest.setDatabasePrefix("test");
		udhTest.initalizeDatabases();
		DataHolder.store(udhTest, UserDataHolder.class);
		
		createDatabaseTest1.setActualResult(udhTest.getDatabasePrefix());
		
		
		
		
		return true;
	}

}
