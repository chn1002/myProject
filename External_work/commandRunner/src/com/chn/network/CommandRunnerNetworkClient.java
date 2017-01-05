/**
 * 
 */
package com.chn.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * @FileName  : mainClient.java 
 * @Project   : relayClient 
 * @Date      : 2013. 3. 26. 
 * @작성자    : dasan1 
 * @변경이력  : 
 * @프로그램설명 : 
 */
public class CommandRunnerNetworkClient{
	private Socket mSocket = null;
	private String mIPAddress = "";
	private int mPortNumber;
	private MessageListener mListener;
	private boolean mReviceMessage;
	private Thread mReviceThread;

	final String DEFAULT_IPADDRESS = "127.0.0.1";
	final int DEFAULT_PORT = 5000;

	public CommandRunnerNetworkClient(MessageListener listener){
		mIPAddress = DEFAULT_IPADDRESS;	
		mPortNumber = DEFAULT_PORT;
		mListener = listener;
	}

	public void setIpAddress(String ipAddress) {
		this.mIPAddress = ipAddress;
		this.mPortNumber = DEFAULT_PORT;
	}

	public void setIpAddress(String ipAddress, int portNumber){
		this.mIPAddress = ipAddress;
		this.mPortNumber = portNumber;
	}

	public boolean socketConnect(){
		try {
			mSocket = new Socket(mIPAddress, mPortNumber);
			mReviceMessage = true;
			reviceMessage(new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "KSC5601"), 1024));

			return true;
		} catch (UnknownHostException error) {
			error.printStackTrace();

			return true;
		} catch (IOException error) {
			error.printStackTrace();

			return true;
		}
	}

	public boolean socketClose(){
		try {
			if(mReviceThread.isAlive()){
				mReviceMessage = false;
			}

			mSocket.close();

			return true;
		} catch (IOException error) {
			error.printStackTrace();

			return false;
		}
	}

	public void sendMessage(String message){
		try {

			BufferedWriter OutMsg = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
			OutMsg.write(message);
			OutMsg.newLine();
			OutMsg.flush();

			System.out.println("Output : " + message);
		} catch (IOException error) {
			error.printStackTrace();
		}
	}

	private void reviceMessage(final BufferedReader InMsg){
		mReviceThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(mReviceMessage){
					try {
						String inLine = InMsg.readLine();
						mListener.ReviceMessage(inLine);
					} catch (IOException error) {
						error.printStackTrace();
					}

				}
			}
		});

		mReviceThread.start();
	}

	public void setReviceMessage(boolean ReviceMessage) {
		this.mReviceMessage = ReviceMessage;
	}
}
