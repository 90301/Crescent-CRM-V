package users;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

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
	ArrayList<String> databasesAccsessable;
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
		//constructor currently does nothing.
	}

	@Override
	public void loadInternalFromMap() {
		this.userName = (String) dbMap.get(userNameField);
		this.passHash = (String) dbMap.get(passHashField);
		this.admin = (boolean) dbMap.get(adminField);
	}

	@Override
	public void updateDBMap() {
		dbMap.put(userNameField, userName);
		dbMap.put(passHashField, passHash);
		dbMap.put(adminField, admin);
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
		dbDatatypes.put(userNameField, String.class);
		//Traditional Way ^^^
		dbDatatypes.put(passHashField, String.class);
		//Would this work? ^^^
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

}
