/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package users;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.Cookie;

import com.vaadin.server.VaadinService;

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
	 */

	private static SecureRandom random = new SecureRandom();
	public static final String COOKIE_NAME = "remember-me";

	/*
	 * Logging in info
	 */

	MaxField<String> userName = new MaxField<String>("UserName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	MaxField<String> passHash = new MaxField<String>("PassHash", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "", this);

	MaxField<ArrayList<String>> authCookies = new MaxField<ArrayList<String>>("authCookies",
			MaxDBTable.DATA_MYSQL_TYPE_STRING, new ArrayList<String>(), new ArrayList<String>(), this);

	MaxField<ArrayList<String>> databasesAccsessable = new MaxField<ArrayList<String>>("databasesAccsessable",
			MaxDBTable.DATA_MYSQL_TYPE_STRING, new ArrayList<String>(), new ArrayList<String>(), this);

	MaxField<String> databaseSelected = new MaxField<String>("databaseSelectedField",
			MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	MaxField<Boolean> admin = new MaxField<Boolean>("admin", MaxDBTable.DATA_MYSQL_TYPE_BOOLEAN, false, false, this);

	ArrayListToString arraylistToString = new ArrayListToString();

	//API fields
	MaxField<String> facebookKey = new MaxField<String>("facebookKey", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "", this);
	MaxField<String> googleKey = new MaxField<String>("googleKey", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "", this);

	MaxField<String> pushBulletKey = new MaxField<String>("pushBulletKey", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "",
			this);

	//private static final SecureRandom random = new SecureRandom();
	PasswordAuthentication pa = new PasswordAuthentication();

	//Themes
	MaxField<String> theme = new MaxField<String>("theme", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	MaxField<String> viewMode = new MaxField<String>("viewMode", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,
			VIEW_MODE_DEFAULT, VIEW_MODE_DEFAULT, this);

	public static final String VIEW_MODE_DEFAULT = "Default View";
	public static final String VIEW_MODE_DESKTOP = "Desktop View";
	public static final String VIEW_MODE_MOBILE = "Mobile View";
	public static final String VIEW_MODE_SMALL = "Small View";

	public static final String[] VIEW_MODES = { VIEW_MODE_DEFAULT, VIEW_MODE_DESKTOP, VIEW_MODE_MOBILE,
			VIEW_MODE_SMALL };

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
		arraylistToString.setDefaultStoreValue("");

		databasesAccsessable.setConversion(arraylistToString);

		authCookies.setConversion(arraylistToString);
		

		this.setKeyField(userName);

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

	public String getPushBulletKey() {
		return pushBulletKey.getFieldValue();
	}

	public void setPushBulletKey(String pushBulletKey) {
		this.pushBulletKey.setFieldValue(pushBulletKey);
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

	//Auth Keys
	public ArrayList<String> getAuthKeys() {
		return this.authCookies.getFieldValue();
	}

	public void setAuthKeys(ArrayList<String> authCookies) {

		this.authCookies.setFieldValue(authCookies);

	}

	public void addAuthKey(String key) {
		this.authCookies.getFieldValue().add(key);
		this.updateDBMap();
	}

	public void removeAuthKey(String key) {
		this.authCookies.getFieldValue().remove(key);
		this.updateDBMap();
	}

	public boolean containsAuthKey(String key) {
		return this.authCookies.getFieldValue().contains(key);
	}

	public static String genRememberKey() {
		String randomId = new BigInteger(130, random).toString(32);

		return randomId;
	}

	public void rememberUser() {

		String authKey = genRememberKey();

		this.addAuthKey(authKey);
		Cookie cookie = new Cookie(COOKIE_NAME, authKey);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 30); // valid for 30 days
		VaadinService.getCurrentResponse().addCookie(cookie);

		//DataHolder.store(this, User.class);
	}

	public void deleteRememberMeCookie() {
		//gets cookie to delete it from the database.
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		Optional<Cookie> cookieUser = Arrays.stream(cookies).filter(c -> c.getName().equals(User.COOKIE_NAME)).findFirst();
		if (cookieUser.isPresent()) {
			String val = cookieUser.get().getValue();
			this.removeAuthKey(val);
		}
		DataHolder.store(this, User.class);
		//overwrites the cookie to delete it from client
		Cookie cookie = new Cookie(COOKIE_NAME, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		VaadinService.getCurrentResponse().addCookie(cookie);
		
	}

	/**
	 * This SETS the databases that are accessible to a user.
	 * 
	 * @param databases
	 *            a list of the databases the user will be able to use
	 */
	public void setDatabaseAccessible(Collection<String> databases) {
		this.databasesAccsessable.setFieldValue(new ArrayList<String>(databases));
	}

}
