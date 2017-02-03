package debugging.unitTest.testSuiteExecutors;

import ccrmV.CrmUI;
import ccrmV.MasterUI;
import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;

public class CRMTestExecutor extends TestSuiteExecutor {
	
	MasterUI masterUi;
	CrmUI crmUi;
	
	
	public CRMTestExecutor(MasterUI masterUi, CrmUI crmUi) {
		super();
		this.masterUi = masterUi;
		this.crmUi = crmUi;
	}
	
		//expected values
		public static String locationTest1Name = "Columbia";
	    public static String statusTest1Name = "Prospect";
	    public static String groupTest1Name = "Referral";

	@Override
	public Boolean runTests() {
		//randomize values
		
		locationTest1Name = genSmallRandomString("City");
		statusTest1Name = genSmallRandomString("Status"); 
		groupTest1Name = genSmallRandomString("Group");
		
		//UI test for CRM 
	   
		UnitTestCase addLocationTest1 = new UnitTestCase("addLocationTest1", "Set new location ", locationTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addStatusTest1 = new UnitTestCase("addStatusTest1", "Set new status ", statusTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addGroupTest1 = new UnitTestCase("addGroupTest1", "Set new group ", groupTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
		
		
		
		
		return true;
	}

}
