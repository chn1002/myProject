package com.chn.network;

import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.IOException;   
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.chn.relaymain.GrobalConfig;
import com.chn.relaymain.MainService;

public class NetworkServer extends MainService{
	private boolean mNetworkStart = false;
	private ServerSocket mSerSockDes;
	private Socket mSockDes;
	private ArrayList<RelayProcess> mList;

	public NetworkServer(){
		CreateNetworkServer(0);
	}

	public NetworkServer(int port){
		CreateNetworkServer(port);
	}

	// Create Network Socket
	private void CreateNetworkServer(int port){
		try{
			if(port != 0){
				mSerSockDes = new ServerSocket(port);
				mList = new ArrayList<RelayProcess>();
				
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

	public void Start(GrobalConfig grobalconf){
		try{
			mMainUI.mTextArea.append("Server IP : "  + mServer.getSerSocket().getLocalSocketAddress() + "\n");
			RelayProcess netProcess = null;

			while(true){
				if(mNetworkStart){
					mMainUI.mTextArea.append("System Message Watting\n");
					netProcess = new RelayProcess(mSerSockDes.accept());
					mList.add(netProcess);
					netProcess.start();
				} else{
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

	class RelayProcess extends Thread{
		private BufferedReader mInMsg;
		private BufferedWriter mOutMsg;
		private Socket mSocketDes;
		
		/**
		 * @param accept
		 */
		public RelayProcess(Socket accept) {
			mSocketDes = accept;
		}

		@Override
		public void run() {
			boolean isStop = false;  //  flag value(��� ��)
			
			try {
				mInMsg = new BufferedReader(new InputStreamReader(mSocketDes.getInputStream()), 1024);
				mOutMsg = new BufferedWriter(new OutputStreamWriter(mSocketDes.getOutputStream()));

				String message = null;  
			
				while(! isStop) {
					if(!mSocketDes.isConnected()){
						break;
					}
					
					String context = mInMsg.readLine();
					
					if(context != null){
						message = context;
//						message = String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?><MESSAGE>%s</MESSAGE>", context);
					} else {
						message = "exit";
//						message = String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?><MESSAGE>%s</MESSAGE>", "exit");
					}
					if(message.equals("exit")) { // ȫ�浿#exit, �����ϰڴٴ� ��
						broadCasting(message);//��� ����ڿ��� ���� ����
						isStop = true;  //  ����
					} else {
						broadCasting(message);//��� ����ڿ��� ä�� ���� ����
					}//else

					mMainUI.mTextArea.append(mSocketDes.getInetAddress() + " : " + message + "\n");
				}//while
				
				mList.remove(this);//ȫ�浿�� ����.
				mMainUI.mTextArea.append(mSocketDes.getInetAddress() + 
						" IP �ּ��� ����ڲ��� �����ϼ̽��ϴ�.\n");
			} catch (Exception e) {
				e.printStackTrace();
				
				mList.remove(this);
				mMainUI.mTextArea.append(mSocketDes.getInetAddress() + 
						" IP �ּ��� ����ڲ��� ������ �����ϼ̽��ϴ�.\n");
			}//catch
		}//run
		
		public void broadCasting(String message) {//��ο��� ����
			for (RelayProcess ct : mList) {
				ct.send(message);
			}//for
		}//broadCasting
		
		public void send(String message) {  //  �� ����ڿ��� ����
			try {
				mOutMsg.write(message);				
				mOutMsg.newLine();
				mOutMsg.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}//catch
		}//send
	}//���� Ŭ����
}
