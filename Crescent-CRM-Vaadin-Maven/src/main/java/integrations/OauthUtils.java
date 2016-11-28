package integrations;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;

public class OauthUtils {

	
	
	// Create a state token to prevent request forgery.
	  // Store it in the session for later validation.
	  String stateString = new BigInteger(130, new SecureRandom()).toString(32);
	  request.session().attribute("state", state);
	  // Read index.html into memory, and set the client ID,
	  // token state, and application name in the HTML before serving it.
	  return new Scanner(new File("index.html"), "UTF-8")
	      .useDelimiter("\\A").next()
	      .replaceAll("[{]{2}\\s*CLIENT_ID\\s*[}]{2}", CLIENT_ID)
	      .replaceAll("[{]{2}\\s*STATE\\s*[}]{2}", state)
	      .replaceAll("[{]{2}\\s*APPLICATION_NAME\\s*[}]{2}",
	                  APPLICATION_NAME);

	  Link link = new Link("Login with Google",
		        new ExternalResource("
https://accounts.google.com/o/oauth2/v2/auth?
client_id=118137317828-cl9hpkac2j5ijvo2ilhs11fbvjh5q7i2.apps.googleusercontent.com&
response_type=code&
scope=openid%20email&
redirect_uri=https://localhost:8080
state=stateString&
hd=trkla@email.sc.edu"));

}
