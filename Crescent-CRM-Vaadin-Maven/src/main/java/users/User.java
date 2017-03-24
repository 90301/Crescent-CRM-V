/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package users;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import ccrmV.MasterUI;
import clientInfo.DataHolder;
import clientInfo.UserDataHolder;
import dbUtils.InhalerUtils;
import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import dbUtils.PasswordAuthentication;
import dbUtils.Conversions.ArrayListToString;

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
	
	
	/*
	 
	String userName;
	public static final String userNameField = "UserName";
	String passHash;
	public static final String passHashField = "PassHash";
	
	*/
	
	MaxField<String> userName = new MaxField<String>("UserName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);
	
	MaxField<String> passHash = new MaxField<String>("PassHash", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "",
			this);
	
	MaxField<ArrayList<String>> databasesAccsessable = new MaxField<ArrayList<String>>("databasesAccsessable", MaxDBTable.DATA_MYSQL_TYPE_STRING,
			new ArrayList<String>(), new ArrayList<String>(), this);
	
	MaxField<String> databaseSelected = new MaxField<String>("databaseSelectedField", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);
	

	
	MaxField<Boolean> admin = new MaxField<Boolean>("admin", MaxDBTable.DATA_MYSQL_TYPE_BOOLEAN, false, false,
			this);
	
	ArrayListToString arraylistToString = new ArrayListToString();
	
	//API fields
	MaxField<String> facebookKey = new MaxField<String>("facebookKey", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "",
			this);
	MaxField<String> googleKey = new MaxField<String>("googleKey", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "",
			this);
	//private static final SecureRandom random = new SecureRandom();
	PasswordAuthentication pa = new PasswordAuthentication();
	
	
	//Themes
	MaxField<String> theme = new MaxField<String>("theme", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);
	
	MaxField<String> viewMode = new MaxField<String>("viewMode", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, VIEW_MODE_DEFAULT, VIEW_MODE_DEFAULT,
			this);
	
	
	public static final String VIEW_MODE_DEFAULT = "Default View";
	public static final String VIEW_MODE_DESKTOP = "Desktop View";
	public static final String VIEW_MODE_MOBILE = "Mobile View";
	public static final String VIEW_MODE_SMALL = "Small View";
	
	public static final String[] VIEW_MODES = {VIEW_MODE_DEFAULT,VIEW_MODE_DESKTOP,VIEW_MODE_MOBILE,VIEW_MODE_SMALL};
	/*
	 * Database selection
	 */
	//TODO: make this concurrent
	//ArrayList<String> databasesAccsessable = new ArrayList<String>();
	//public static final String dataBasesAccsessableField = "databasesAccsessable";
	
	//Serialized with gson as json text
	
	
	//String databaseSelected;
	//public static final String databaseSelectedField = "databaseSelectedField";
	
	//Boolean admin;
	//public static final String adminField = "admin";
	
	
	/*
	 * TODO:
	 * Add permissions (Must be extensible)
	 * User Authentication
	 * Options
	 * >database connection / table?
	 * 
	 * 
	 */
	
	{
		
		databasesAccsessable.setConversion(arraylistToString);
		
		this.setKeyField(userName);
		
		this.addMaxField(userName);
		this.addMaxField(passHash);
		this.addMaxField(databasesAccsessable);
		this.addMaxField(databaseSelected);
		this.addMaxField(admin);
		
		this.addMaxField(facebookKey);
		this.addMaxField(googleKey);
		
		this.addMaxField(theme);
		this.addMaxField(viewMode);
	}
	public User() {
		init();
	}
	
	public void init() {
		
	}
	
	@Override
	public String toString() {
		return this.getPrimaryKey();
	}

	
	@Override
	public String getPrimaryKey() {
		return userName.getFieldValue();
	}
	
	public void setPassword(String pass) {
		
		this.passHash.setFieldValue(pa.hash(pass.toCharArray()));
		updateDBMap();
	}
	
	public Boolean checkPassword(String pass) {
		Boolean authenticated = pa.authenticate(pass.toCharArray(), this.passHash.getFieldValue());
		
		return authenticated;
	}
	
	
	public void setUserName(String userName) {
		this.userName.setFieldValue(userName);
		
	}

	public void setAdmin(boolean admin) {
		this.admin.setFieldValue(admin);
		
	}
	
	public void setDatabaseSelected(String selectedDB) {
		this.databaseSelected.setFieldValue(selectedDB);
		//updateDBMap();
	}
	public String getDatabaseSelected() {
		return databaseSelected.getFieldValue();
		
	}

	public void addDatabaseAccsessable(String string) {
		this.databasesAccsessable.getFieldValue().add(string);
		this.updateDBMap();//Must be called.
		
	}
	public void addDatabaseAccsessable(UserDataHolder udh) {
		this.addDatabaseAccsessable(udh.getPrimaryKey());
		
	}
	
	public Collection<String> getDatabasesAccsessable() {
		return databasesAccsessable.getFieldValue();
	}

	public boolean getAdmin() {
		return admin.getFieldValue();
	}
	

	
	public String getFacebookKey() {
		return facebookKey.getFieldValue();
	}

	public void setFacebookKey(String facebookKey) {
		this.facebookKey.setFieldValue(facebookKey);
	}
	
	public String getGoogleKey() {
		return googleKey.getFieldValue();
	}

	public void setGoogleKey(String googleKey) {
		this.googleKey.setFieldValue(googleKey);
	}
	
	public String getTheme() {
		return theme.getFieldValue();
	}
	public void setTheme(String theme) {
		this.theme.setFieldValue(theme);
	}
	
	public String getViewMode() {
		return viewMode.getFieldValue();
	}
	public void setViewMode(String viewMode) {
		this.viewMode.setFieldValue(viewMode);
	}
	/**
	 * This SETS the databases that are accessible to a user.
	 * @param databases a list of the databases the user will be able to use
	 */
	public void setDatabaseAccessible(Collection<String> databases) {
		this.databasesAccsessable.setFieldValue(new ArrayList<String>(databases));
	}

}
