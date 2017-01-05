package com.dasan.testserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class udpserver extends Thread{
	private int mPort = 100;

	public udpserver(int port){
		mPort = port;
	}

	@Override
	public void run() {
		DatagramSocket serS;
		try {
			serS = new DatagramSocket(mPort);
			DatagramPacket inPac;

			byte [] inMSG = new byte[100];

			while(true){
				System.out.println("UDP Server Ready port : " + mPort);

				inPac = new DatagramPacket(inMSG, inMSG.length);

				serS.receive(inPac);

				InetAddress add = inPac.getAddress();
				int port = inPac.getPort();

				System.out.println("[Recive UDP Packet] IP:" + add.getHostAddress() + " , Port : " + port);
				serS.send(inPac);;
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.run();
	}	
}
