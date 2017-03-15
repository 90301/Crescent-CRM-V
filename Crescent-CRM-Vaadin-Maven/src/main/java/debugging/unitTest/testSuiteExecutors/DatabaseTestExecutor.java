package debugging.unitTest.testSuiteExecutors;

import java.util.HashSet;

import clientInfo.Client;
import clientInfo.DataHolder;
import clientInfo.Group;
import clientInfo.Location;
import clientInfo.Status;
import clientInfo.TemplateField;
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
	
	public static String TEST_DB = "test";
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
	
	//group testing 
	static String GroupTest1Name = "Referral";//set to random string
	static int GroupTest1Color = 155;
	
	//client testing
	static String clientTest1Name = "Karen Smith";
	static String clientTest1Location = "Lexington";
	static String clientTest1Status = "Prospect";
	static String clientTest1Group = "Referral";
	static Boolean clientTest1ContactNow = true;
	static String clientTest1LastUpdated = "Yesterday";
	static String clientTest1Notes = "Little Dog";
	
	//custom field testing
	private String customField1 = "PhoneNumber";
	private String customField1Datatype = TemplateField.DATA_TYPE_TEXT;
	private String customField1Default = "NoPhoneOnRecord";
	
	
	private String clientTest2Name = "Teddy Fields";
	
	private String clientTest3Name = "Rufus Jet";
	private String customFieldValue1 = "555-5555";
	
	@Override
	public Boolean runTests() {
		
		//Gen Random Values 
		
		locationTest1Name = genSmallRandomString("City:");
		
		GroupTest1Name = genSmallRandomString("Group");
		
		
		
		//Create Test cases
		//inventory testing
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
		
		//location testing
		
		UnitTestCase createLocationTest1 = new UnitTestCase("createLocationTest1", "Set location name  ", locationTest1Name, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createLocationTest2 = new UnitTestCase("createLocationTest2", "Close locations testing  ", true, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createGroupTest1 = new UnitTestCase("createGroupTest1", "Set group name", GroupTest1Name, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createGroupTest2 = new UnitTestCase("createGroupTest2", "Set client name", GroupTest1Color, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		
		//Client Testing
		UnitTestCase createClientTest1 = new UnitTestCase("createClientTest1", "Set client name", null, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createClientTest2 = new UnitTestCase("createClientTest2", "Set client location", null, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createClientTest3 = new UnitTestCase("createClientTest3", "Set client status", null, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createClientTest4 = new UnitTestCase("createClientTest4", "Set client group", clientTest1ContactNow, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createClientTest5 = new UnitTestCase("createClientTest5", "Set client last updated to now", null, UnitTestCase.TEST_TYPE_NOT_NULL,this);
		
		UnitTestCase createClientTest6 = new UnitTestCase("createClientTest6", "Set client notes", null, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase createClientTest7 = new UnitTestCase("createClientTest7", "Set client notes", null, UnitTestCase.TEST_TYPE_OBJECT,this);

		
		//custom field testing
		UnitTestCase customFieldTest1 = new UnitTestCase("customFieldTest1", "Set custom field name ", customField1, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase customFieldTest2 = new UnitTestCase("customFieldTest2", "Set custom field data type", customField1Datatype, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase customFieldTest3 = new UnitTestCase("customFieldTest3", "custom field server cache", null, UnitTestCase.TEST_TYPE_NOT_NULL,this);
		
		UnitTestCase customFieldTest4 = new UnitTestCase("customFieldTest4", "client custom field default value", customField1Default, UnitTestCase.TEST_TYPE_OBJECT,this);
		
		UnitTestCase customFieldTest5 = new UnitTestCase("customFieldTest5", "Set custom field for client", customFieldValue1, UnitTestCase.TEST_TYPE_OBJECT,this);
			
		//Execute code for tests
		
		createDatabaseTest1.startProfiling();
		
		UserDataHolder udhTest = new UserDataHolder();
		udhTest.setDatabasePrefix(TEST_DB);
		udhTest.initalizeDatabases();
		DataHolder.store(udhTest, UserDataHolder.class);
		
		//createDatabaseTest1.stopProfiling();
		createDatabaseTest1.setActualResult(udhTest.getDatabasePrefix());
		
		Boolean udhClientMapExists = (udhTest.getMap(Client.class) != null);
		createDatabaseTest2.setTestMessage("Client Map" + udhTest.getMap(Client.class));
		createDatabaseTest2.setActualResult(udhClientMapExists);
		
		inventoryItemCreateTest1.startProfiling();
		
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
		
		
		inventoryItemInDBTest1.startProfiling();
		
		udhTest.store(invItem, InventoryItem.class);
		
		Boolean inventoryTestItem1Exists = (udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey)!=null);
		
		inventoryItemCreateTest1.setTestMessage("" + udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey));
		inventoryItemInDBTest1.setActualResult(inventoryTestItem1Exists);
		
		//Actually from the cache(not the SQL Database)
		InventoryItem fromDBItem1 = udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey);
		
		inventoryItemInDBTest2.setActualResult(fromDBItem1.getItemName());
		
		createStatusTest1.startProfiling();
		//status testing
		Status testStatus1 = new Status();
		testStatus1.setStatusName(statusTest1Name);
		//testStatus1.setStatusColor(statusTest1Color);
		udhTest.store(testStatus1, Status.class);
		
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
		
		
		
		//group testing
		Group testGroup1 = new Group();
		testGroup1.setGroupName(GroupTest1Name);
		testGroup1.setColor(GroupTest1Color);
		
		udhTest.store(testGroup1, Group.class);
		
		
		createGroupTest1.setActualResult(testGroup1.getGroupName());
		createGroupTest2.setActualResult(testGroup1.getColor());
		

		//Client Testing
		
		Client clientTest1 = new Client();
		
		clientTest1.setUserDataHolder(udhTest);
		
		clientTest1.setName(clientTest1Name);
		
		clientTest1.setLocation(testLocation2);
		
		clientTest1.setStatus(testStatus1);
		
		clientTest1.setGroup(testGroup1);
		
		clientTest1.setLastUpdatedToNow();
		
		clientTest1.setNotes(clientTest1Notes);
		
		clientTest1.setContactNow(clientTest1ContactNow);
		
		udhTest.store(clientTest1, Client.class);
		
		createClientTest1.setExpectedResult(clientTest1Name);
		createClientTest2.setExpectedResult(testLocation2);
		createClientTest3.setExpectedResult(testStatus1);
		createClientTest4.setExpectedResult(testGroup1);
		//createClientTest5.setExpectedResult(null);
		createClientTest6.setExpectedResult(clientTest1Notes);
		createClientTest7.setExpectedResult(clientTest1ContactNow);
		
		createClientTest1.setActualResult(clientTest1.getName());
		createClientTest2.setActualResult(clientTest1.getLocation());
		createClientTest3.setActualResult(clientTest1.getStatus());
		createClientTest4.setActualResult(clientTest1.getGroup());
		createClientTest5.setActualResult(clientTest1.getLastUpdated());
		createClientTest6.setActualResult(clientTest1.getNotes());
		createClientTest7.setActualResult(clientTest1.getContactNow());
		
		//Custom Field Testing
		
		TemplateField tf = new TemplateField();
		tf.setDataType(customField1Datatype);
		tf.setDefaultValue(customField1Default );
		tf.setFieldName(customField1);
		tf.setUserDataHolder(udhTest);
		udhTest.store(tf, TemplateField.class);
		
		customFieldTest1.setActualResult(tf.getFieldName());
		customFieldTest2.setActualResult(tf.getDataType());
		
		//Ensure udhTest Contains this template field
		TemplateField tfDBTest1 = udhTest.getMap(TemplateField.class).get(tf.getPrimaryKey());
		
		customFieldTest3.setActualResult(tfDBTest1);
		// Client Testing for template field
		
		Client clientTest2 = new Client();
		
		clientTest2.setUserDataHolder(udhTest);
		
		clientTest2.setupCustomFieldsFromTemplate();
		clientTest2.setName(clientTest2Name );
		clientTest2.setLocation(testLocation2);
		clientTest2.setStatus(testStatus1);
		clientTest2.setGroup(testGroup1);
		clientTest2.setLastUpdatedToNow();
		clientTest2.setNotes(clientTest1Notes);
		clientTest2.setContactNow(clientTest1ContactNow);
		udhTest.store(clientTest2, Client.class);
		
		//Ensure default value for custom field works
		
		customFieldTest4.setActualResult(clientTest2.getCustomFieldValue(customField1));
		
		Client clientTest3 = new Client();
		
		clientTest3.setUserDataHolder(udhTest);
		
		clientTest3.setupCustomFieldsFromTemplate();
		clientTest3.setName(clientTest3Name );
		clientTest3.setLocation(testLocation2);
		clientTest3.setStatus(testStatus1);
		clientTest3.setGroup(testGroup1);
		clientTest3.setLastUpdatedToNow();
		clientTest3.setNotes(clientTest1Notes);
		clientTest3.setContactNow(clientTest1ContactNow);
		//set custom field value
		clientTest3.setCustomFieldValue(customField1, customFieldValue1 );
		
		udhTest.store(clientTest3, Client.class);
		
		//ensure the custom field was set
		
		customFieldTest5.setActualResult(clientTest3.getCustomFieldValue(customField1));
		
		return true;
	}

}
