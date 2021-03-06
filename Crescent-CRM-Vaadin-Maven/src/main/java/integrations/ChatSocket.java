package integrations;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.*;

import configuration.Configuration;
import debugging.Debugging;

public class ChatSocket {
	public static ServerSocket nodeSocket; 
	public static ServerSocket javaSocket;
	public static Integer NODE_PORT_SEND = 3000;
	public static Integer NODE_PORT_RECEIVE = 3002;
	public static String NODE_IP = "127.0.0.1";
	public static Socket socket;
	public static Boolean DISABLE_SOCKET_CODE = false;
	
	//List of constants for node JS
	public static final String LOGIN1 = "login1";
	public static final String LOGIN2 = "login2";
	public static final String THREAD_ID = "threadID";
	
	
	public static void setupNodeSocket() {
		
		if (!DISABLE_SOCKET_CODE) {
		try {
			
			//LOAD IP FROM CONFIG
			NODE_IP = Configuration.get(Configuration.NODE_HOSTNAME);
			//int threadID;
			ChatSocket client = new ChatSocket();
			
			Debugging.output("Attempting to connect to: " + NODE_IP, Debugging.NODE_SOCKET_DEBUG);
			client.socketConnect(NODE_IP, NODE_PORT_SEND);
			
			
			String message = "login1:" + Configuration.get(Configuration.FB_CHAT_EMAIL) + "/" + Configuration.get(Configuration.FB_CHAT_PASS);//retrieve friends list
			
			String message2 = "THREAD_ID:"+ Configuration.get(Configuration.FB_CHAT_EMAIL) + "/" + Configuration.get(Configuration.FB_CHAT_PASS);//retrieve messages

			System.out.println("Sending: " + message);
			String returnStr = client.SendAndReceive(message);
			System.out.println("Receiving: " + returnStr);
			
			System.out.println("Sending threadID...");
			String threadID = parseThreadIDs(returnStr);
			String messageReturn = client.SendAndReceive("threadID:" + threadID);
			System.out.println("Receiving messages: " + messageReturn);
			
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

	public String SendAndReceive(String message){
		try {
			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
			out.println(message);	
			
			InputStream input;
			javaSocket = new ServerSocket(NODE_PORT_RECEIVE);
			Socket client;
			client = javaSocket.accept();
			
			input = client.getInputStream();
			String inputString = ChatSocket.inputStreamAsString(input);
			//System.out.println(inputString);

            client.close();
            javaSocket.close();
			
            return inputString;
		} catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String parseThreadIDs(String jsonThreadIds) throws IOException{
		
		String[] sectionOne = jsonThreadIds.split("\"");
		String threadID = sectionOne[3];
		
		/*PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
		out.println("threadID/" + threadID);
		
		InputStream input;
		javaSocket = new ServerSocket(NODE_PORT_RECEIVE);
		Socket client;
		client = javaSocket.accept();
		
		input = client.getInputStream();
		String inputString = ChatSocket.inputStreamAsString(input);
		
		client.close();
		javaSocket.close();*/
		
		return threadID;
	}
	
	public String echo(String message) {
		try {
			
			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));


			out.println(message);
			String returnStr = in.readLine();
			return returnStr;


		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}

	public static String inputStreamAsString(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }

        br.close();
        return sb.toString();
    }
	
	private static Socket getSocket() {
		return socket;
	}

}
