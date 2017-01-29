package debugging.unitTest.testSuiteExecutors;

import clientInfo.Client;
import clientInfo.DataHolder;
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
	static String inventoryItem1TestName = "7RR red dye";
	static String inventoryItemTestKey = "RR2016";
	static String inventoryItem1TestCategory = "dye";
	static String inventoryItem1TestBarcode = "0000001";
	static String inventoryItem1URL = "www.google.com";
	
	@Override
	public Boolean runTests() {
		
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
		
		UnitTestCase inventoryItemInDBTest1 = new UnitTestCase("inventoryItemInDBTest1", "Check to make sure inventory item in DB", true, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemInDBTest1);
		
		UnitTestCase inventoryItemInDBTest2 = new UnitTestCase("inventoryItemInDBTest2", "Check to make sure the version has same name we assigned ", inventoryItem1TestName, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemInDBTest2);
		
		
		
		
		//Execute code for tests
		

		UserDataHolder udhTest = new UserDataHolder();
		udhTest.setDatabasePrefix("test");
		udhTest.initalizeDatabases();
		DataHolder.store(udhTest, UserDataHolder.class);
		
		createDatabaseTest1.setActualResult(udhTest.getDatabasePrefix());
		
		Boolean udhClientMapExists = (udhTest.getMap(Client.class) != null);
		createDatabaseTest2.setTestMessage("Client Map" + udhTest.getMap(Client.class) );
		createDatabaseTest2.setActualResult(udhClientMapExists);
		
		InventoryItem invItem = new InventoryItem();
		invItem.setItemKey(inventoryItemTestKey);
		invItem.setItemName(inventoryItem1TestName);
		invItem.setItemCategory(inventoryItem1TestCategory);
		invItem.setItemBarcode(inventoryItem1TestBarcode);
		invItem.setItemURL("www.google.com");
		invItem.setItemStock(12);
		invItem.setItemReorderPoint(6);
		
		inventoryItemCreateTest1.setActualResult(invItem.getItemName());
		inventoryItemCreateTest2.setActualResult(invItem.getItemCategory());
		inventoryItemCreateTest3.setActualResult(invItem.getItemBarcode());
		inventoryItemCreateTest4.setActualResult(invItem.getItemURL());
		
		udhTest.store(invItem, InventoryItem.class);
		
		Boolean inventoryTestItem1Exists = (udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey)!=null);
		
		inventoryItemCreateTest1.setTestMessage("" + udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey));
		inventoryItemInDBTest1.setActualResult(inventoryTestItem1Exists);
		
		//Actually from the cache(not the SQL Database)
		InventoryItem fromDBItem1 = udhTest.getMap(InventoryItem.class).get(inventoryItemTestKey);
		
		inventoryItemInDBTest2.setActualResult(fromDBItem1.getItemName());
		
		return true;
	}

}
