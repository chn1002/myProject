package src.com.dasan.testclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class tcpclient extends Thread{
	private int mDstPort = 110;
	private int mSrcPort = 1000;
	private String mIPAddress = "192.168.55.101";
	private int mRepeat = 10;
	private int mInterval = 1000; // 1 second
	private Socket mSocket;
	private PrintWriter mOutput;
	private BufferedReader mInput;
	private String mMessage = "test";
	
	public tcpclient(String ip, int port){
		mIPAddress = ip;
		mDstPort = port;
	}
	
	public void setMessage(String message){
		this.mMessage = message;
	}
	
	@Override
	public void run() {
		try {
			mSocket = new Socket(mIPAddress, mDstPort);
			
			System.out.println("Connect");
			mOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())));
			for(int i = 0; i < mRepeat; i++){
				mOutput.write(mMessage);
				mOutput.flush();
				
				Thread.sleep(mInterval);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.run();
	}
}
