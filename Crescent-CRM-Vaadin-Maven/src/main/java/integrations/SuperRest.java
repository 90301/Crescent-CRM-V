package integrations;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import configuration.Configuration;
import dbUtils.InhalerUtils;
import debugging.Debugging;
import elemental.json.JsonArray;

public class SuperRest {
	
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

}
