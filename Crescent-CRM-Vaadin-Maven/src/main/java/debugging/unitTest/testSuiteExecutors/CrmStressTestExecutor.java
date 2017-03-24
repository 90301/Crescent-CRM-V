package debugging.unitTest.testSuiteExecutors;

import java.util.ArrayList;

import ccrmV.CrmUI;
import ccrmV.MasterUI;
import clientInfo.*;
import debugging.profiling.RapidProfilingTimer;
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
		
		ArrayList<String> allStatusNames = new ArrayList<String>();
		ArrayList<String> allLocationNames = new ArrayList<String>();
		ArrayList<String> allGroupNames = new ArrayList<String>();
		
		for (Status e: masterUi.userDataHolder.getAllStatus()) {
			allStatusNames.add(e.getPrimaryKey());
		}
		
		for (Location e: masterUi.userDataHolder.getAllLocations()) {
			allLocationNames.add(e.getPrimaryKey());
		}
		
		for (Group e: masterUi.userDataHolder.getAllGroups()) {
			allGroupNames.add(e.getPrimaryKey());
		}
		
		RapidProfilingTimer rapidCreateClientTimer = new RapidProfilingTimer("rapid create Timer.");
		
		rapidCreateClientTimer.logTime();
		for (int i=0;i<ClientsToCreate;i++) {
			int statusNum = TestSuiteExecutor.rand.nextInt(allStatusNames.size());
			int groupNum = TestSuiteExecutor.rand.nextInt(allGroupNames.size());
			int locationNum = TestSuiteExecutor.rand.nextInt(allLocationNames.size());
			
			this.crmUi.createClientStatus.setValue(allStatusNames.get(statusNum));
			this.crmUi.createClientGroup.setValue(allGroupNames.get(groupNum));
			this.crmUi.createClientLocation.setValue(allLocationNames.get(locationNum));
			
			this.crmUi.createClientClick();
			
			rapidCreateClientTimer.logTime();
		}
		
		//attempt various random filters
		
		
		
		//switch to another part of the program and switch back to the crm
		
		
		
		return true;
	}

}
