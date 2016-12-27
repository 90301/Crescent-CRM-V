package integrations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;

import debugging.Debugging;

/**
 * Servlet implementation class GoogleIntegrationsServlet
 */
@WebServlet("/GoogleIntegrationsServlet")
public class GoogleIntegrationsServlet extends HttpServlet {// AbstractAuthorizationCodeServlet {
	private static final long serialVersionUID = 1L;
	//private static final DataStoreFactory DATA_STORE_FACTORY = new ;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoogleIntegrationsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Debugging.output("GET REQUEST: "+ request,Debugging.OAUTH2);
		
		Enumeration<String> attributes = request.getAttributeNames();
		while (attributes.hasMoreElements()) {
			String atribute = attributes.nextElement();
			String value = request.getAttribute(atribute).toString();
			Debugging.output("Found Attribute: "+ atribute + " Value: " + value,Debugging.OAUTH2);
		}
		Map<String, String[]> parameters = request.getParameterMap();
		
		String queryString = request.getQueryString();
		Debugging.output("Query String: " + queryString ,Debugging.OAUTH2);
		
		for (String parameterKey : parameters.keySet()) {
			String[] value = parameters.get(parameterKey);
			
			Debugging.output("Found Parameter: "+ parameterKey + " Value: " + value,Debugging.OAUTH2);
		}
		
		String requestUrl = request.getRequestURI();
		Debugging.output("Request URL: " + requestUrl ,Debugging.OAUTH2);
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.sendRedirect("/");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	//@Override
	protected String getRedirectUri(HttpServletRequest arg0) throws ServletException, IOException {
		// TODO Auto-generated method stub
		return OauthUtils.getGoogleRedirectUri();
	}

	//@Override
	protected String getUserId(HttpServletRequest arg0) throws ServletException, IOException {
		// TODO Auto-generated method stub
		return OauthUtils.getGoogleID();
	}

	//@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		Debugging.output("Initalizeing flow. " ,Debugging.OAUTH2);
		//ERROR is occuring here.
		GoogleAuthorizationCodeFlow x = null;
		try {
			
		NetHttpTransport netHttpTransport = new NetHttpTransport();
		x = new GoogleAuthorizationCodeFlow.Builder(
		        netHttpTransport, JacksonFactory.getDefaultInstance(),
		        OauthUtils.getGoogleID(), OauthUtils.getGoogleSecret(),
		        OauthUtils.getGoogleScopes()).build();
		
		//.setDataStoreFactory(
        //DATA_STORE_FACTORY).setAccessType("offline")
		} catch (Exception e) {
			e.printStackTrace();
		}
		return x;
	}

}
