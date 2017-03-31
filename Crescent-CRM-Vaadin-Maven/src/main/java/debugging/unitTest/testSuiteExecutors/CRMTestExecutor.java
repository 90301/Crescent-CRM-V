package debugging.unitTest.testSuiteExecutors;

import ccrmV.CrmUI;
import ccrmV.MasterUI;
import clientInfo.DataHolder;
import clientInfo.Group;
import clientInfo.Location;
import clientInfo.Status;
import debugging.DebugObject;
import debugging.Debugging;
import debugging.profiling.MasterTimer;
import debugging.profiling.ProfilingTimer;
import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;

public class CRMTestExecutor extends TestSuiteExecutor {
	
	private static final DebugObject CRM_TEST_DEBUG = new DebugObject(Debugging.CONSOLE_OUTPUT, true, true,"CRM TEST DEBUG: ");
	MasterUI masterUi;
	CrmUI crmUi;
	
	
	
	public CRMTestExecutor(CrmUI crmUi, MasterUI masterUi)  {
		super();
		this.masterUi = masterUi;
		this.crmUi = crmUi;
	}
	
		//expected values
		public String locationTest1Name = "Columbia";
	    public String statusTest1Name = "Prospect";
	    public String groupTest1Name = "Referral";
	    public String clientTest1Name = "Jane";
	    
	@Override
	public Boolean runTests() {
		
		ProfilingTimer runTime = new ProfilingTimer("CRM Test Executor");
		
		//randomize values
		
		locationTest1Name = genSmallRandomString("City");
		statusTest1Name = genSmallRandomString("Status"); 
		groupTest1Name = genSmallRandomString("Group");
		clientTest1Name = genSmallRandomString("Client");
		//UI test for CRM 
	   
		UnitTestCase addLocationTest1 = new UnitTestCase("addLocationTest1", "Set new location ", locationTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
		UnitTestCase addLocationTest2 = new UnitTestCase("addLocationTest2", "Clear location name textbox ", locationTest1Name , UnitTestCase.TEST_TYPE_NOT_EQUAL,this);
		
	    UnitTestCase addStatusTest1 = new UnitTestCase("addStatusTest1", "Set new status ", statusTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addStatusTest2 = new UnitTestCase("addStatusTest2", "Clear Status name textbox ", statusTest1Name , UnitTestCase.TEST_TYPE_NOT_EQUAL,this);
	    //UnitTestCase addStatusTest3 = new UnitTestCase("addStatusTest3", "Clear Status name textbox ", statusTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    
	    UnitTestCase addGroupTest1 = new UnitTestCase("addGroupTest1", "Set new group ", groupTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addGroupTest2 = new UnitTestCase("addGroupTest2", "Clear Group name textbox ", groupTest1Name , UnitTestCase.TEST_TYPE_NOT_EQUAL,this);
		
	    UnitTestCase addClientTest1 = new UnitTestCase("addClientTest1", "Ensure the name of the client is correct", clientTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addClientTest2 = new UnitTestCase("addClientTest2", "Ensure the Location of the client is correct", locationTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addClientTest3 = new UnitTestCase("addClientTest3", "Ensure the Status of the client is correct", statusTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    UnitTestCase addClientTest4 = new UnitTestCase("addClientTest4", "Ensure the Group of the client is correct", groupTest1Name , UnitTestCase.TEST_TYPE_OBJECT,this);
	    
	    addLocationTest1.startProfiling();
	    //switch database
	    this.masterUi.setUserDataHolder(DatabaseTestExecutor.TEST_DB);
	    
		//location test
	    
	    crmUi.createLocationName.setValue(locationTest1Name);
		//fire  off "button click"
		crmUi.createLocationClick();
		
		Location locationTest1 = masterUi.userDataHolder.getMap(Location.class).get(locationTest1Name);
		
		addLocationTest1.setActualResult(locationTest1.getLocationName());
		
		addLocationTest2.setActualResult(crmUi.createLocationName.getValue());
		
		//Status Test
		
		crmUi.createStatusName.setValue(statusTest1Name);//set's the status textbox's value
		
		crmUi.createStatusClick();//fire "button click"
		
		Status statusTest1 = masterUi.userDataHolder.getMap(Status.class).get(statusTest1Name);
		
		addStatusTest1.setActualResult(statusTest1.getStatusName());
		
		addStatusTest2.setActualResult(crmUi.createStatusName.getValue());
		
		//group test
		
		crmUi.createGroupName.setValue(groupTest1Name);//set's the group's textbox's value
		
		crmUi.createGroupClick();//fire "button click"
		
		Group groupTest1 = masterUi.userDataHolder.getMap(Group.class).get(groupTest1Name);
		
		addGroupTest1.setActualResult(groupTest1.getGroupName());
		
		addGroupTest2.setActualResult(crmUi.createGroupName.getValue());
		
		//create a client using existing categories and create client using new categories 
		addClientTest1.startProfiling();
		
		crmUi.createClientName.setValue(clientTest1Name);
		
		//ensure client creation categories contain the newly created fields
		
		//this assumes it's the name that is added to the combo box
		
		/*
		for (Object id : crmUi.createClientStatus.getItemIds()) {
			Debugging.output("ID: " + id, CRM_TEST_DEBUG);
		}
		*/
		crmUi.createClientLocation.setValue(locationTest1Name);
		
		crmUi.createClientStatus.setValue(statusTest1Name);
		
		crmUi.createClientGroup.setValue(groupTest1Name);
		
		crmUi.createClientClick();
		
		
		
		//ensure the client is selected
		
		
		
		//ensure boxes clear
		
		
		addClientTest1.setActualResult(crmUi.selectedClient.getName());
		addClientTest2.setActualResult(crmUi.selectedClient.getLocation().getLocationName());
		addClientTest3.setActualResult(crmUi.selectedClient.getStatus().getStatusName());
		addClientTest4.setActualResult(crmUi.selectedClient.getGroup().getGroupName());
		
		//attempt to edit the client.
		
		
		
		
		//further testing
		
		//TEMPLATE testing
		
		//select the template.
		
		//attempt to edit the template
		
		//update the template
		
		//add template field?
		
		//ensure template field shows up for exising client.
		
		//attempt to edit a template field on existing clinet
		
		
		runTime.stopTimer();
		
		
		return true;
	}

}
