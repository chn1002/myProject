package src.com.dasan.testclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class udpclient extends Thread{
	private int mDstPort = 110;
	private int mSrcPort = 1000;
	private String IPAddress = "10.90.135.101";
	private int repeat = 1;
	private int interval = 10; // 1 second
	
	public udpclient(String ip, int port){
		IPAddress = ip;
		mDstPort = port;
	}
	
	@Override
	public void run() {
		DatagramSocket dataGramSocket;
		try {
			dataGramSocket = new DatagramSocket(mSrcPort);
			InetAddress serverAdd = InetAddress.getByName(IPAddress);
			String inMSG = "Test Message";

			DatagramPacket recivePacket;
			DatagramPacket sendPacket =  new DatagramPacket(inMSG.getBytes(), inMSG.getBytes().length, serverAdd, mDstPort);
			for(int i = 0; i < repeat ; i ++){
				dataGramSocket.send(sendPacket);
				System.out.println("[Send Packet] IP: " + i + " : " + serverAdd.getHostAddress() + " , Port : " + mDstPort);

				byte[] buffer = new byte[inMSG.length()];
				
				recivePacket = new DatagramPacket(buffer, buffer.length);
				dataGramSocket.receive(recivePacket);
				
				String msg2 = new String(recivePacket.getData(), 0, recivePacket.getLength());
				System.out.println("[Echo Message] " + msg2);

				Thread.sleep(interval);
			}
			
			dataGramSocket.close();

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}
}
