package integrations;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import configuration.Configuration;
import dbUtils.InhalerUtils;
import debugging.Debugging;

public class SuperRest {
	
	public static void connectToFirebase() {
		RestTemplate firebaseConnection = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		
		Map<String,String> authenticationVars = new HashMap<String,String>();
		
		authenticationVars.put("code", Configuration.get(Configuration.FIRE_BASE_KEY));
		authenticationVars.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8" );//plain text
		
		Debugging.output("Auth Key: " + Configuration.get(Configuration.FIRE_BASE_KEY), Debugging.FIREBASE);
		
		HttpEntity<Map<String,String>> request = new HttpEntity<Map<String,String>>(authenticationVars,headers);
		
		ResponseEntity<String> response = firebaseConnection.
				postForEntity("https://fcm.googleapis.com/fcm/send",
						request, String.class);
		
		Debugging.output("Response: " + response, Debugging.FIREBASE);
		
	}

}
