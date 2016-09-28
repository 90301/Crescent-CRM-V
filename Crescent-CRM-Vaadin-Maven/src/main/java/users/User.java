package users;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;
import dbUtils.PasswordAuthentication;

public class User extends MaxObject {
	
	
	/*
	 * Here we define variables for the class.
	 * Each variable that is used must also have a field name
	 * -----------------------------------------------------
	 * IMPORTANT:
	 * if you update a variable or make a new one, you MUST update
	 * the following methods:
	 * > updateDBMap
	 * > loadInternalFromMap
	 * > createTableForClass
	 * > setupDBTypes
	 * 
	 */
	
	/*
	 * Logging in info
	 */
	String userName;
	public static final String userNameField = "UserName";
	String passHash;
	public static final String passHashField = "PassHash";
	//private static final SecureRandom random = new SecureRandom();
	PasswordAuthentication pa = new PasswordAuthentication();
	
	/*
	 * Database selection
	 */
	//TODO: make this concurrent
	ArrayList<String> databasesAccsessable = new ArrayList<String>();
	public static final String dataBasesAccsessableField = "databasesAccsessable";
	
	//Serialized with gson as json text
	
	
	String databaseSelected;
	public static final String databaseSelectedField = "databaseSelectedField";
	
	Boolean admin;
	public static final String adminField = "admin";
	
	
	/*
	 * TODO:
	 * Add permissions (Must be extensible)
	 * User Authentication
	 * Options
	 * >database connection / table?
	 * 
	 * 
	 */
	public User() {
		init();
	}
	
	public void init() {
		
	}

	@Override
	public void loadInternalFromMap() {
		
		this.userName = (String) dbMap.get(userNameField);
		this.passHash = (String) dbMap.get(passHashField);
		this.databaseSelected = (String) dbMap.get(databaseSelectedField);
		this.admin = (boolean) dbMap.get(adminField);
		
		safetyCheck();
	}

	@Override
	public void updateDBMap() {
		safetyCheck();
		dbMap.put(userNameField, userName);
		dbMap.put(passHashField, passHash);
		dbMap.put(databaseSelectedField, this.databaseSelected);
		dbMap.put(adminField, admin);
	}

	private void safetyCheck() {
		if (this.databaseSelected==null){
			this.databaseSelected="";
		}
		
	}

	@Override
	public String getPrimaryKey() {
		return userName;
	}

	@Override
	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(userNameField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(passHashField, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		//database selection
		table.addDatatype(dataBasesAccsessableField, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(databaseSelectedField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		//Administration / rights
		table.addDatatype(adminField, MaxDBTable.DATA_MYSQL_TYPE_BOOLEAN);
		table.setPrimaryKeyName(userNameField);
		table.createTable();
	}
	
	public void setPassword(String pass) {
		
		this.passHash = pa.hash(pass.toCharArray());
		updateDBMap();
	}
	
	public Boolean checkPassword(String pass) {
		Boolean authenticated = pa.authenticate(pass.toCharArray(), this.passHash);
		
		return authenticated;
	}
	
	/*
	 * 
	 * @see dbUtils.MaxObject#setupDBDatatypes()
	 */
	@Override
	public void setupDBDatatypes() {
		if (dbDatatypes == null) {
			dbDatatypes = new HashMap<String, Class<?>>();
		}
		
		dbDatatypes.put(userNameField, String.class);
		//Traditional Way ^^^
		dbDatatypes.put(passHashField, String.class);
		//Would this work? ^^^
		dbDatatypes.put(databaseSelectedField, String.class);
		
		dbDatatypes.put(dataBasesAccsessableField, String.class);//JSON converted
		
		dbDatatypes.put(adminField, Boolean.class);
	}

	public void setUserName(String userName) {
		this.userName = userName;
		updateDBMap();
		
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
		updateDBMap();
		
	}
	
	public void setDatabaseSelected(String selectedDB) {
		//TODO: Error checking and access requirements
		this.databaseSelected = selectedDB;
	}
	public String getDatabaseSelected() {
		return databaseSelected;
		
	}

	public void addDatabaseAccsessable(String string) {
		this.databasesAccsessable.add(string);
		
	}

}
