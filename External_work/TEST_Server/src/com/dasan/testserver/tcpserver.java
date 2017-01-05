package com.dasan.testserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class tcpserver extends Thread{
	private int mPort = 100;
	private ServerSocket mServer = null;
	private Socket clientSocket;
	
	public tcpserver(int port){
		mPort = port;
	}
	
	@Override
	public void run() {
		try {
			mServer = new ServerSocket(mPort);
			while(true){
				System.out.println("TCP Server Ready port : " + mPort);
				clientSocket = mServer.accept();
				System.out.println("[Recive TCP Packet] IP:" + clientSocket.getInetAddress().getHostAddress() + " , Port : " + clientSocket.getPort());
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		super.run();
	}
}

class ServerClient extends Thread{
	private PrintWriter out;
	private BufferedReader in;
	private String msg;
	
	public ServerClient(Socket socket) throws IOException{
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.start();
	}
	
	@Override
	public void run() {
		while(true){
			try {
				msg = in.readLine();
				System.out.println("[Received] : " + msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
