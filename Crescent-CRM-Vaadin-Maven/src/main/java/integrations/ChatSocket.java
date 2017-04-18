package integrations;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import debugging.Debugging;

public class ChatSocket {
	public static ServerSocket nodeSocket; 
	public static Integer NODE_PORT_SEND = 3000;
	public static Integer NODE_PORT_RECEIVE = 3002;
	public static String NODE_IP = "127.0.0.1";
	public static Socket socket;
	public static Boolean DISABLE_SOCKET_CODE = false; 
	
	public static void setupNodeSocket() {
		
		if (!DISABLE_SOCKET_CODE) {
		try {
			
			ChatSocket client = new ChatSocket();
			
			client.socketConnect(NODE_IP, NODE_PORT_SEND);
			
			String message = "login1:troywingert20@gmail.com/test1234"; //retrieve friends list


			System.out.println("Sending: " + message);
			String returnStr = client.echo(message);
			System.out.println("Receiving: " + returnStr);
			
			//ChatSocket.sendCredentials("troywingert20@gmail.com", "zigzag14"); This is the test account
			
			Debugging.output("Connecting to socket on port: " + NODE_PORT_SEND , Debugging.NODE_SOCKET_DEBUG);
			
			
			Debugging.output("waiting for connection.  ", Debugging.NODE_SOCKET_DEBUG);


			Debugging.output("connection accepted. ", Debugging.NODE_SOCKET_DEBUG);
			
			//nodeSocket = new ServerSocket(NODE_PORT_RECEIVE);
			//socket = nodeSocket.accept();
			
			//sendCredentials("troywingert20@gmail.com", "zigzag14");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	public static void sendCredentials(String user,String pass) throws IOException {
		
		String code = "login";
		
		
		
		
		//OutputStream socketStream = socket.getOutputStream();
		//socketStream.write((user + " " + pass).getBytes());		
	}
	/**
	 * Update later to save the messages for the specified client
	 * @param clientId
	 * @throws IOException 
	 */
	public static void getMessagesFor(String clientId) throws IOException {
		
		OutputStream os = socket.getOutputStream();
		os.write(clientId.getBytes());
		
	}
	
	public static void closeSocket() throws IOException{
		
		socket.close();
	}
	
	private void socketConnect(String node_ip, int node_port) throws UnknownHostException, IOException {
		System.out.println("[Connecting to socket...]");
		ChatSocket.socket = new Socket(node_ip, node_port);
	}

	public String echo(String message) {
		try {
			
			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));


			out.println(message);
			String returnStr = in .readLine();
			return returnStr;


		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}

	private Socket getSocket() {
		return socket;
	}

}
