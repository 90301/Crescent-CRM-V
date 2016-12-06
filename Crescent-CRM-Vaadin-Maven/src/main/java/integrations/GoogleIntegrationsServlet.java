package integrations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import debugging.Debugging;

/**
 * Servlet implementation class GoogleIntegrationsServlet
 */
@WebServlet("/GoogleIntegrationsServlet")
public class GoogleIntegrationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
		for (String parameterKey : parameters.keySet()) {
			String[] value = parameters.get(parameterKey);
			
			Debugging.output("Found Parameter: "+ parameterKey + " Value: " + value,Debugging.OAUTH2);
		}
		
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
