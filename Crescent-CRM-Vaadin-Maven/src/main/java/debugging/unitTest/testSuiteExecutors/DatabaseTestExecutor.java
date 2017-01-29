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
	@Override
	public Boolean runTests() {
		
		//Create Test cases
		UnitTestCase createDatabaseTest1 = new UnitTestCase("createDatabaseTest1", "Ensures prefix for the user data holder works correctly", "test", UnitTestCase.TEST_TYPE_STRING_CONTAINS);
		this.testCases.add(createDatabaseTest1);
		
		UnitTestCase createDatabaseTest2 = new UnitTestCase("createDatabaseTest2", "Test not run.", true, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(createDatabaseTest2);
		
		UnitTestCase inventoryItemCreateTest1 = new UnitTestCase("inventoryItemCreateTest1", "Create Item, set name", inventoryItem1TestName, UnitTestCase.TEST_TYPE_OBJECT);
		this.testCases.add(inventoryItemCreateTest1);
		
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
		invItem.setItemCategory("Dye");
		invItem.setItemBarcode("0000001");
		invItem.setItemURL("www.google.com");
		invItem.setItemStock(12);
		invItem.setItemReorderPoint(6);
		
		inventoryItemCreateTest1.setActualResult(invItem.getItemName());
		
		
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
