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
			boolean isStop = false;  //  flag value(깃발 값)
			
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
					if(message.equals("exit")) { // 홍길동#exit, 종료하겠다는 뜻
						broadCasting(message);//모든 사용자에게 내용 전달
						isStop = true;  //  종료
					} else {
						broadCasting(message);//모든 사용자에게 채팅 내용 전달
					}//else

					mMainUI.mTextArea.append(mSocketDes.getInetAddress() + " : " + message + "\n");
				}//while
				
				mList.remove(this);//홍길동을 뺀다.
				mMainUI.mTextArea.append(mSocketDes.getInetAddress() + 
						" IP 주소의 사용자께서 종료하셨습니다.\n");
			} catch (Exception e) {
				e.printStackTrace();
				
				mList.remove(this);
				mMainUI.mTextArea.append(mSocketDes.getInetAddress() + 
						" IP 주소의 사용자께서 비정상 종료하셨습니다.\n");
			}//catch
		}//run
		
		public void broadCasting(String message) {//모두에게 전송
			for (RelayProcess ct : mList) {
				ct.send(message);
			}//for
		}//broadCasting
		
		public void send(String message) {  //  한 사용자에게 전송
			try {
				mOutMsg.write(message);				
				mOutMsg.newLine();
				mOutMsg.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}//catch
		}//send
	}//내부 클래스
}
