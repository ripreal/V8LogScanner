package org.v8LogScanner.LocalTCPConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.commonly.ExcpReporting;

public class SocketTemplates {
	
	private static SocketTemplates instance;

	private SocketTemplates(){}
	
	public static SocketTemplates instance(){
		if (instance == null)
			instance = new SocketTemplates();
		return instance;
	}	
	
	//INITIALITION///////////////////
	
	public ServerSocket createServerSocket(int port){
		ServerSocket sk = null;
		try {
			SocketAddress address = new InetSocketAddress(getLocalHost(), port);
			sk = new ServerSocket();
			sk.bind(address);
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);
			try {
				sk.close();
				sk = null;
			} catch (IOException e1) {
				ExcpReporting.LogError(this, e);
				sk = null;
			}
		}
		return sk;
	}
	
	public Socket createClientSocket(String ip, int port){
		
		Socket clientSocket = null;
		
		byte[] ipElements = fetchToByteIP(ip);
		
		try {
			InetAddress ipAddr = InetAddress.getByAddress(ipElements);
			clientSocket = new Socket();
			SocketAddress sa = new InetSocketAddress(ipAddr.getHostAddress(), port);		
			clientSocket.connect(sa, 5000);
		} catch (IOException e){
			// do nothing my be client mistake with IP or port
			clientSocket = null;
		}
		return clientSocket;
	}
	
	//CONNECTION////////////////////
	
	public Socket acceptClient(ServerSocket serverSocket){
		
		Socket socket = null;
		
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		finally{
			
		}
		return socket;
	}
	
	public void getPossibleHosts(int port, Consumer<String> getNewHost){
		
		// Fetch local host
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace(out);
			return;
		}
		
		byte[] ip = localhost.getAddress();
		getNewHost.accept("127.0.0.1");
		
		for (int i = 95; i <= 97; i++){
			
			ip[3] = (byte)i;
			
			try {
				InetAddress address = InetAddress.getByAddress(ip);
				V8LogScannerClient client = new V8LanLogScannerClient(
					address.getHostAddress());
				
				if (address.isReachable(1000) && client.pingServer()){
					getNewHost.accept(client.getHostIP());
				}
				else{
					//out.println(" Negative. Server not found");
				}
			}
			catch(IOException e){
				//out.println(" Negative. Server not found");
			}
		}
	}
	
	public boolean isConformIpv4(String ip) {
		return ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
	}
	
 	public String getHostIP(Socket clientSocket){
		return clientSocket.getInetAddress().getHostAddress();
	}
	
	public String getHostIP(ServerSocket socket){
		return socket.getInetAddress().getHostAddress();
	}
	
	/**
	 * Gets string representation of a network IP belonged to the local host. 
	 * @return  network IP, if it is not exist return empty string.
	 */
	public String getHostIP(){
		String localIP = "";
		try {
			localIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ExcpReporting.LogError(this, e);;
		}
		return localIP;
	}
	
	public String getHostIP(String hostName){
		String hostIP = "";
		try {
			hostIP = InetAddress.getByName(hostName).getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ExcpReporting.LogError(this, e);
		}
		return hostIP;
	}
	
	public String getHostName(String hostIP){
		
		String hostName = "";
		
		try {
			hostName = InetAddress.getByAddress(fetchToByteIP(hostIP)).getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ExcpReporting.LogError(this, e);
		}
		
		return hostName;
	}
	
	public void close(Socket socket){
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ExcpReporting.LogError(this, e);;
		}
	}
	
	//TEXT EXCHANGE STREAMS/////////////
	
 	public BufferedReader getInputTextReader(Socket socket){
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		return in;
	}
	
	public PrintWriter getOutTextReader(Socket socket){
		PrintWriter out = null;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		return out;
	}
	
	public String getStreamLine(BufferedReader inputReader){
		String line = null;
		try {
			line = inputReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ExcpReporting.LogError(this, e);;
		}
		return line;
	}
	
	public void sendStreamLine(PrintWriter outReader, String text){
		outReader.println(text);
	}
	
	//DATE EXCHANGE STREAMS////////////
	
	public ObjectInputStream getInputDataReader(Socket socket){
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		return in;
	}
	
	public ObjectOutputStream getOutDataReader(Socket socket){
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		return out;
	}
	
	public boolean sendData(Socket clientSocket, Object data){
		boolean dispatch = false;
		try {
			getOutDataReader(clientSocket).writeObject(data);
			dispatch = true;
		} catch (IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		return dispatch;
	}
	
	public Object getData(Socket clientSocket){
		Object result = null;
		try {
			// input stream will wait until corresponding stream 
			// on client side  had written in
			ObjectInputStream inputStream = getInputDataReader(clientSocket);
				if (inputStream != null)
					result = inputStream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			ExcpReporting.LogError(this, e);;
		}
		return result;
	}
	
	//OTHER//////////////////////////
	
	public InetAddress getLocalHost(){
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			ExcpReporting.LogError(this, e);;
		}
		return inetAddress;
	}
	
	private byte[] fetchToByteIP(String ip){
		byte[] ipElements = new byte[4];
		
		String[] ipStrokes = ip.split("\\Q.\\E");
		for (int i = 0; i < ipStrokes.length; i++)
			ipElements[i] = (byte) Integer.parseInt(ipStrokes[i]);
		return ipElements;
	}
}
