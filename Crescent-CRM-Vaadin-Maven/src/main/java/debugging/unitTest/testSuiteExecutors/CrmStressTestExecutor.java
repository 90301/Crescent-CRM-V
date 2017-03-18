package debugging.unitTest.testSuiteExecutors;

import ccrmV.CrmUI;
import ccrmV.MasterUI;
import debugging.unitTest.TestSuiteExecutor;
import uiElements.NavBar;

/**
 * This is an optional test that can be run. It will take much longer to run
 * and is intended for performance testing
 * @author Joshua Benton
 * (c) 2017 Joshua Benton All Rights Reserved.
 */
public class CrmStressTestExecutor extends TestSuiteExecutor {

	private MasterUI masterUi;
	private CrmUI crmUi;
	
	public int ClientsToCreate = 200;
	
	public CrmStressTestExecutor(CrmUI crmUi, MasterUI masterUi)  {
		super();
		this.masterUi = masterUi;
		this.crmUi = crmUi;
		//this.navBar = navBar;
	}
	
	@Override
	public Boolean runTests() {
		
		//populate table with clients
		
		
		//attempt various random filters
		
		
		
		//switch to another part of the program and switch back to the crm
		
		
		
		return true;
	}

}
