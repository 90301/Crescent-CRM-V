package debugging.unitTest.testSuiteExecutors;

import java.util.ArrayList;

import ccrmV.CrmUI;
import ccrmV.MasterUI;
import clientInfo.*;
import debugging.Debugging;
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
	
	public int ClientsToCreate = 50;
	
	public CrmStressTestExecutor(CrmUI crmUi, MasterUI masterUi)  {
		super();
		this.masterUi = masterUi;
		this.crmUi = crmUi;
		//this.navBar = navBar;
	}
	
	@Override
	public Boolean runTests() {
		
		//populate table with clients
		
		ArrayList<Status> allStatusNames = new ArrayList<>();
		ArrayList<Location> allLocationNames = new ArrayList<>();
		ArrayList<Group> allGroupNames = new ArrayList<>();
		
		for (Status e: masterUi.userDataHolder.getAllStatus()) {
			allStatusNames.add(e);
		}
		
		for (Location e: masterUi.userDataHolder.getAllLocations()) {
			allLocationNames.add(e);
		}
		
		for (Group e: masterUi.userDataHolder.getAllGroups()) {
			allGroupNames.add(e);
		}
		
		RapidProfilingTimer rapidCreateClientTimer = new RapidProfilingTimer("rapid create Timer.");
		
		rapidCreateClientTimer.logTime();
		for (int i=0;i<ClientsToCreate;i++) {
			int statusNum = TestSuiteExecutor.rand.nextInt(allStatusNames.size());
			int groupNum = TestSuiteExecutor.rand.nextInt(allGroupNames.size());
			int locationNum = TestSuiteExecutor.rand.nextInt(allLocationNames.size());
			
			String name = genSmallRandomString("Rapid");
			
			this.crmUi.createClientName.setValue(name);
			
			this.crmUi.createClientStatus.setValue(allStatusNames.get(statusNum));
			this.crmUi.createClientGroup.setValue(allGroupNames.get(groupNum));
			this.crmUi.createClientLocation.setValue(allLocationNames.get(locationNum));
			
			Debugging.output("status: " + this.crmUi.createClientStatus.getValue(), Debugging.RAPID_CLIENT);
			Debugging.output("location: " + this.crmUi.createClientLocation.getValue(), Debugging.RAPID_CLIENT);
			Debugging.output("group: " + this.crmUi.createClientGroup.getValue(), Debugging.RAPID_CLIENT);
			
			this.crmUi.createClientClick();
			
			rapidCreateClientTimer.logTime();
		}
		
		//attempt various random filters
		
		
		
		//switch to another part of the program and switch back to the crm
		
		
		
		return true;
	}

}
