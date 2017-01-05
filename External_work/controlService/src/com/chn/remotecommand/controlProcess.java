package com.chn.remotecommand;

import java.io.BufferedReader;    
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class controlProcess extends RemoteControlerMainService{
	public enum MessageReadType{
		NETWORK_READ,
		CONSOL_READ
	};

	private Socket mSocket;
	private BufferedReader mInMsg;
	private BufferedWriter mOutMsg;

	private grobalConfig mGroConf;

	MessageReadType mReadType;

	public controlProcess(grobalConfig grobalconf){
		mGroConf = grobalconf;
	}

	public void run() {
		mMainUI.mTextArea.append("Control Process Thr Start\n");
		mReadType = MessageReadType.CONSOL_READ;

		MessageBufferRead(mReadType);

		try {
			processMsg(mInMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(Socket socket){
		mMainUI.textFiledApped("Control Process Thr Start");
		mSocket = socket;
		mReadType = MessageReadType.NETWORK_READ;

		MessageBufferRead(mReadType);

		try {
			processMsg(mInMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Message Process
	private void processMsg(BufferedReader InMsg) throws IOException{
		boolean sendMailSuccess = false;

		while(true){
			String inLine = InMsg.readLine();

			if(inLine == null){
				mMainUI.mTextArea.append("command is null\n");
				System.out.println("command is null");
				return;
			} 

			if(mReadType == MessageReadType.NETWORK_READ){
				if(!mSocket.isConnected()){
					return;
				}
			} 

			// Process

			mMainUI.mTextArea.append("Client Send Msg " + sendMailSuccess + " \n");
		}
	}

	// Message Buffer Init
	private void MessageBufferRead(MessageReadType readType){
		try {
			if(readType == MessageReadType.NETWORK_READ){
				mInMsg = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "KSC5601"), 1024);
			}
			else if(readType == MessageReadType.CONSOL_READ){
				System.out.println("CONSOLMODE : ");
				InputStreamReader isr = new InputStreamReader(System.in);
				mInMsg = new BufferedReader(isr);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
