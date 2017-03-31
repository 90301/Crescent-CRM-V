package integrations;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import configuration.Configuration;
import dbUtils.InhalerUtils;
import debugging.Debugging;
import elemental.json.JsonArray;
import users.User;

public class SuperRest {
	
	
	/*
	 * START Firebase
	 */
	public static void connectToFirebase() {
		RestTemplate firebaseConnection = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Authorization","key="+ Configuration.get(Configuration.FIRE_BASE_KEY));
		
		Map<String,String> vars = new HashMap<String,String>();
		
		//authenticationVars.put("code", Configuration.get(Configuration.FIRE_BASE_KEY));
		//authenticationVars.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8" );//plain text
		
		Debugging.output("Headers: " + headers.toString(), Debugging.FIREBASE);
		
		Debugging.output("Auth Key: " + Configuration.get(Configuration.FIRE_BASE_KEY), Debugging.FIREBASE);
		
		HttpEntity<Map<String,String>> request = new HttpEntity<Map<String,String>>(vars,headers);
		
		/*
		ResponseEntity<String> response = firebaseConnection.
				postForEntity("https://fcm.googleapis.com/fcm/send",
						request, String.class);
		*/
		
		//Debugging.output("Response: " + response, Debugging.FIREBASE);
		JavaScript.getCurrent().addFunction("serverTokenFunction(e)", e -> firebaseDevice(e));
		
		JavaScript.getCurrent().execute("permRequest()");
		
		JavaScript.getCurrent().execute("tokenAquire()");
	}

	/**
	 * Handles the acquring of a firebase device.
	 * @param e
	 * @return
	 */
	private static void firebaseDevice(JsonArray e) {
		Debugging.output("JSON ARRAY: " + e, Debugging.FIREBASE);
		
		
	}

	public static void getToken() {
		JavaScript.getCurrent().execute("tokenAquire()");
		
	}

	public static void requestPermission() {
		JavaScript.getCurrent().execute("permRequest()");
	}
	
	public static void regServiceWorker() {
		JavaScript.getCurrent().execute("regServiceWorker()");
	}
	/*
	 * END Firebase
	 */
	
	/*
	 * Start Push Bullet
	 */
	
	public static void connectToPushBullet(User u) {
		RestTemplate pushBulletConnection = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		
		Map<String,String> vars = new HashMap<String,String>();
		
		
		
		String key = u.getPushBulletKey();
		if (key!="") {
			headers.add("Access-Token",key);
			
			HttpEntity<Map<String,String>> request = new HttpEntity<Map<String,String>>(vars,headers);
			
			Debugging.output("Headers: " +headers, Debugging.PUSH_BULLET);
			
			ResponseEntity<String> response = pushBulletConnection.exchange("https://api.pushbullet.com/v2/users/me",
							HttpMethod.GET,request, String.class);
			
			String resp = response.getBody();
			
			Debugging.output("Response: " +resp, Debugging.PUSH_BULLET);
			
		}
	}

}
