package Server;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class UserServer {
	ArrayList<Socket> ThreadList ;
	public static final int ServerPort = 5050;
	ServerSocket serverSocket = null; 
	InetAddress local;

	public UserServer() {
		ThreadList = new ArrayList<Socket>();
	}

	public void ServerOpen() throws Exception {
		try {
			local = InetAddress.getLocalHost();
			String ip = local.getHostAddress();
			System.out.println("IP : " + ip);
			serverSocket = new ServerSocket(ServerPort);
			System.out.println("Server Open");
			while (true) {
				Socket client = serverSocket.accept();
				ClientThread clientThread = new ClientThread(client);
				clientThread.start();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}

	}
}
