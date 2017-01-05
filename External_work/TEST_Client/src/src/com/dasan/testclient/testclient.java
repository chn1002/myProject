package src.com.dasan.testclient;

public class testclient {
	private static String mIP = "127.0.0.1";
	
	public static void main(String[] args ){
		int port = 8888;
		System.out.println("Test Client");
		
//		udpclient testUDPClient = new udpclient(mIP, port);
		tcpclient testTCPClient = new tcpclient(mIP, port);
		
//		testUDPClient.start();
		testTCPClient.start();
	}
}
