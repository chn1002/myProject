package com.chn.network;

import java.io.IOException;  
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.mail.MessagingException;

import com.chn.emailmain.EmailProcess;
import com.chn.emailmain.EmailProcess.MessageReadType;
import com.chn.emailmain.grobalConfig;
import com.chn.emailmain.mainServer;

public class networkServer extends mainServer{
	private boolean mNetworkStart = false;
	private ServerSocket mSerSockDes;
	private Socket mSockDes;
	
	public networkServer(){
		CreateNetworkServer(0);
	}

	public networkServer(int port){
		CreateNetworkServer(port);
	}

	// Create Network Socket
	private void CreateNetworkServer(int port){
		try{
			if(port != 0){
				mSerSockDes = new ServerSocket(port);
				if(mSerSockDes.isBound()){
					mNetworkStart = true;
				}
			}
			else{
				mNetworkStart = false;
			}
		} catch(MalformedURLException e){
			shutdownServer();
			e.printStackTrace();
		} catch (IOException e) {
			shutdownServer();
			e.printStackTrace();
		}
	}
	
	public Socket getSocket(){
		return this.mSockDes;
	}
	
	public ServerSocket getSerSocket(){
		return this.mSerSockDes;
	}

	public void Start(MessageReadType readType, grobalConfig grobalconf) throws MessagingException {
		try{
			EmailProcess netProcess = new EmailProcess(grobalconf);
			
			while(true){
				if(mNetworkStart && readType == MessageReadType.NETWORK_READ){
					mMainUI.mTextArea.append("System Message Watting\n");
					netProcess.run(mSerSockDes.accept());
				}
				else if(readType == MessageReadType.CONSOL_READ){
					netProcess.run();
				}
				else{
					mMainUI.mTextArea.append("Socket Open Error\n");
					break;
				}
			}
		} catch(IOException e){
			shutdownServer();
			e.printStackTrace();
		}
	}

	// Network Connection Shutdown
	public void shutdownServer() {
		try {
			if(mSockDes != null){
				if(mSockDes.isConnected()){
					if(!mSockDes.isInputShutdown())
						mSockDes.shutdownInput();
					
					if(!mSockDes.isOutputShutdown())
						mSockDes.shutdownOutput();
					
					if(!mSockDes.isClosed())
						mSockDes.close();
				}
			}
			if(mSerSockDes != null){
				if(mSerSockDes.isClosed())
					mSerSockDes.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
