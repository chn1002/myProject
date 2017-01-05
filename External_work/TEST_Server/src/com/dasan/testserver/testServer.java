package com.dasan.testserver;

public class testServer {
	
	public static void main(String[] args ){
		int port = 10000;
		System.out.println("Test Server");
		udpserver testUDPServer = new udpserver(port);
//		tcpserver testTCPServer = new tcpserver(10000);
		
		testUDPServer.start();
//		testTCPServer.start();
	}
	
}
