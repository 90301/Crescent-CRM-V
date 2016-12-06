package integrations;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Link;

import debugging.Debugging;

public class OauthUtils {


	//The point of this method is to maintain a single "session"
	
	
	public static void connectWithGoogle(VaadinRequest request) {
	// Create a state token to prevent request forgery.
	  // Store it in the session for later validation.
	  String stateString = new BigInteger(130, new SecureRandom()).toString(32);
	  
	  
	  //Object state = null;
	  //convert this to vaadin 
	  request.getWrappedSession().setAttribute("state", stateString);
	  // Read index.html into memory, and set the client ID,
	  // token state, and application name in the HTML before serving it.
	
	  
	}
	
	static String googleBaseURL = "https://accounts.google.com/o/oauth2/v2/auth?";
	
	static String googleClientID_Name = "client_id";
	static String googleClientID = "118137317828-cl9hpkac2j5ijvo2ilhs11fbvjh5q7i2.apps.googleusercontent.com";
	
	//Don't need secret yet
	//static String googleClientSecret_Name = "Client_Secret";
	//static String googleClientSecret = "GECKkL2PMd8LFtoAA086ZIs_";
	
	static String googleResponseType_Name = "response_type";
	static String googleResponseType = "token";
	
	static String googleScope_Name = "scope";
	static String googleScope = "openid%20email";
	
	static String googleRedirect_Name = "redirect_uri";
	static String googleRedirect = "https://localhost:8443/Crescent-CRM-Vaadin-Maven/GoogleIntegrationsServlet";
	//static String googleRedirect = "http%3A%2F%2Flocalhost%3A8080";
	
	static String googleState_Name = "state";
	static String googleState = "stateString";
	
	//Don't need hd yet.
	//static String googleHd_Name = "hd";
	//static String googleHd = "trkla@email.sc.edu";
	
	//TODO make this an interface or superclass so all o-auth things
	//don't need copy pasted code
	
	static ArrayList<String> googleParameterKeyList = new ArrayList<String>();
	static Map<String,String> googleParameterMap = new HashMap<String,String>();
		
		public static void addGoogleParameter(String key, String value) {
			
			googleParameterKeyList.add(key);
			
			googleParameterMap.put(key, value);
			
		}
		
	
	public static Link genGoogleLink() {
		
		
		//this keeps the keys in the correct order
		//in the event that order matters: use the keyList instead of the map's keyset
		clearGoogleParameters();
		addGoogleParameter(googleClientID_Name, googleClientID);
		//addGoogleParameter(googleClientSecret_Name, googleClientSecret);
		addGoogleParameter(googleResponseType_Name, googleResponseType);
		addGoogleParameter(googleScope_Name, googleScope);
		addGoogleParameter(googleRedirect_Name, googleRedirect);
		//addGoogleParameter(googleState_Name, googleState);
		//addGoogleParameter(googleHd_Name, googleHd);
		
		
		String parameterLink = "";
		Boolean firstRun = true;
		
		for (String key : googleParameterKeyList) {
			String value = googleParameterMap.get(key);
			if (firstRun) {
				firstRun = false;
			} else {
				//add the 'and' for every parameter other then the first one
				parameterLink +="&";
			}
			
			parameterLink += key+ "=" +value;
		}
		
		Debugging.output("Parameter String: " + parameterLink,Debugging.OAUTH_OUTPUT , Debugging.OAUTH_OUTPUT_ENABLED);
		
		String fullUrl = googleBaseURL + parameterLink;
		
		Debugging.output("Full URL: " + fullUrl,Debugging.OAUTH_OUTPUT , Debugging.OAUTH_OUTPUT_ENABLED);
		
		Link googleLink = new Link(null, new ExternalResource(fullUrl));
		googleLink.setIcon(new ThemeResource("images/btn_google_signin_light_normal_web.png"));
		return googleLink;
		
	}

	private static void clearGoogleParameters() {
		googleParameterKeyList.clear();
		googleParameterMap.clear();
	}
}
