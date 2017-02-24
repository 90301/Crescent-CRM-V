package integrations;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import debugging.Debugging;

public class ChatSocket {
	public static ServerSocket nodeSocket; 
	public static Integer NODE_PORT = 3000;
	
	public static void setupNodeSocket() {
		try {
			
			Debugging.output("Starting socket on port: " + NODE_PORT , Debugging.NODE_SOCKET_DEBUG);
			
			nodeSocket = new ServerSocket(NODE_PORT);
			
			Debugging.output("waiting for connection.  ", Debugging.NODE_SOCKET_DEBUG);
			
			//nodeSocket.accept();
			
			Debugging.output("connection accepted. ", Debugging.NODE_SOCKET_DEBUG);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendCredentials(String user,String pass) {
		
	}
	/**
	 * Update later to save the messages for the specified client
	 * @param clientId
	 */
	public static void getMessagesFor(String clientId) {
		
	}
}
