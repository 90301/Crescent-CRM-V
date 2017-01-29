package debugging.unitTest.testSuiteExecutors;

import java.util.HashSet;

import clientInfo.Client;
import clientInfo.DataHolder;
import clientInfo.Location;
import clientInfo.Status;
import clientInfo.UserDataHolder;
import debugging.unitTest.TestSuiteExecutor;
import debugging.unitTest.UnitTestCase;
import inventory.InventoryItem;

/**
 * 
 * @author Owner
 *
 */
public class DatabaseTestExecutor extends TestSuiteExecutor {
	//inventory items testing
	static String inventoryItem1TestName = "7RR red dye";
	static String inventoryItemTestKey = "RR2016";
	static String inventoryItem1TestCategory = "dye";
	static String inventoryItem1TestBarcode = "0000001";
	static String inventoryItem1URL = "www.google.com";
	static int inventoryItem1Stock = 12;
	static int inventoryItem1ReorderPoint = 6;
	
	//status testing
	static String statusTest1Name = "Prospect";  
	static int statusTest1Color = 122;
	
	//location testing
	static String locationTest1Name = "Columbia";//set to random string
	
	@Override
	public Boolean runTests() {
		
		//Gen Random Values
		
		locationTest1Name = genSmallRandomString("City:");
		
		
		//Create Test cases
		UnitTestCase createDatabaseTest1 = new UnitTestCase("createDatabaseTest1", "Ensures prefix for the user data holder works correctly", "test", UnitTestCase.TEST_TYPE_STRING_CONTAINS);
		this.testCases.add(createDatabaseTest1);
		
		UnitTestCase createDatabaseTest2 = new UnitTestCase("createDatabaseTest2", "Test not run.", true, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(createDatabaseTest2);
		
		UnitTestCase inventoryItemCreateTest1 = new UnitTestCase("inventoryItemCreateTest1", "Create Item, set name", inventoryItem1TestName, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest1);
		
		UnitTestCase inventoryItemCreateTest2 = new UnitTestCase("inventoryItemCreateTest2", "Create item,set category", inventoryItem1TestCategory, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest2);
		
		UnitTestCase inventoryItemCreateTest3 = new UnitTestCase("inventoryItemCreateTest3", "Set Barcode", inventoryItem1TestBarcode, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest3);
		
		UnitTestCase inventoryItemCreateTest4 = new UnitTestCase("inventoryItemCreateTest4", "Set URL", inventoryItem1URL, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest4);
		
		UnitTestCase inventoryItemCreateTest5 = new UnitTestCase("inventoryItemCreateTest5", "Set Stock", inventoryItem1Stock, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest5);
		
		UnitTestCase inventoryItemCreateTest6 = new UnitTestCase("inventoryItemCreateTest6", "Set Reorder Point", inventoryItem1ReorderPoint, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest6);
		
		UnitTestCase inventoryItemInDBTest1 = new UnitTestCase("inventoryItemInDBTest1", "Check to make sure inventory item in DB", true, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemInDBTest1);
		
		UnitTestCase inventoryItemInDBTest2 = new UnitTestCase("inventoryItemInDBTest2", "Check to make sure the version has same name we assigned ", inventoryItem1TestName, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemInDBTest2);
		
		//UnitTestCase createClientTest1 = new UnitTestCase("createClientTest1", "Creating a client", client1TestName, UnitTestCase.TEST_TYPE_OBJECT);
		
		UnitTestCase createStatusTest1 = new UnitTestCase("createStatusTest1", "Set status name ", statusTest1Name, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		//UnitTestCase createStatusTest2 = new UnitTestCase("createStatusTest2", "Set color ", statusTest1Color, UnitTestCase.TEST_TYPE_OBJECT);
		//this.testCases.add(createStatusTest2);
		
		UnitTestCase createLocationTest1 = new UnitTestCase("createLocationTest1", "Set location name  ", locationTest1Name, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createLocationTest2 = new UnitTestCase("createLocationTest2", "Close locations testing  ", true, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		
		
		
		//Execute code for tests
		

		UserDataHolder udhTest = new UserDataHolder();
		udhTest.setDatabasePrefix("test");
		udhTest.initalizeDatabases();
		DataHolder.store(udhTest, UserDataHolder.class);
		
		createDatabaseTest1.setActualResult(udhTest.getDatabasePrefix());
		
		Boolean udhClientMapExists = (udhTest.getMap(Client.class) != null);
		createDatabaseTest2.setTestMessage("Client Map" + udhTest.getMap(Client.class));
		createDatabaseTest2.setActualResult(udhClientMapExists);
		
		InventoryItem invItem = new InventoryItem();
		invItem.setItemKey(inventoryItemTestKey);
		invItem.setItemName(inventoryItem1TestName);
		invItem.setItemCategory(inventoryItem1TestCategory);
		invItem.setItemBarcode(inventoryItem1TestBarcode);
		invItem.setItemURL(inventoryItem1URL);
		invItem.setItemStock(inventoryItem1Stock);
		invItem.setItemReorderPoint(inventoryItem1ReorderPoint);
		
		
		//inventory testing
		inventoryItemCreateTest1.setActualResult(invItem.getItemName());
		inventoryItemCreateTest2.setActualResult(invItem.getItemCategory());
		inventoryItemCreateTest3.setActualResult(invItem.getItemBarcode());
		inventoryItemCreateTest4.setActualResult(invItem.getItemURL());
		inventoryItemCreateTest5.setActualResult(invItem.getItemStock());
		inventoryItemCreateTest6.setActualResult(invItem.getItemReorderPoint());
		
		
		
		
		udhTest.store(invItem, InventoryItem.class);
		
		Boolean inventoryTestItem1Exists = (udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey)!=null);
		
		inventoryItemCreateTest1.setTestMessage("" + udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey));
		inventoryItemInDBTest1.setActualResult(inventoryTestItem1Exists);
		
		//Actually from the cache(not the SQL Database)
		InventoryItem fromDBItem1 = udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey);
		
		inventoryItemInDBTest2.setActualResult(fromDBItem1.getItemName());
		
		
		//status testing
		Status testStatus1 = new Status();
		testStatus1.setStatusName(statusTest1Name);
		//testStatus1.setStatusColor(statusTest1Color);
		
		createStatusTest1.setActualResult(testStatus1.getStatusName());
		//TODO: do DB testing
		
		//location testing
		Location testLocation1 = new Location();
		testLocation1.setLocationName(locationTest1Name);
		
		udhTest.store(testLocation1, Location.class);//store in DB
		
		createLocationTest1.setActualResult(testLocation1.getLocationName());
		
		//testing for close locations
		
		Location testLocation2 = new Location();
		testLocation2.setLocationName(genSmallRandomString("closeCity:"));
		
		HashSet<String> closeLocationsTest1 = new HashSet<String>();
		closeLocationsTest1.add(testLocation1.getPrimaryKey());
		testLocation2.setCloseLocations(closeLocationsTest1);
		udhTest.store(testLocation2, Location.class);//store in DB
		createLocationTest2.setExpectedResult(closeLocationsTest1);
		createLocationTest2.setActualResult(testLocation2.getCloseLocations());
		
		return true;
	}

}
