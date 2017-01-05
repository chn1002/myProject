package com.chn.main;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class MovieProcess extends mainServer{
	public enum MessageReadType{
		NETWORK_READ,
		CONSOL_READ
	};
	
	private Socket mSocket;
	private BufferedReader mInMsg;
	private grobalConfig mGroConf;
	private String mCommand;
	private Timer mPlayerTimer;
	
	MessageReadType mReadType;
	
	public MovieProcess(grobalConfig grobalconf){
		mGroConf = grobalconf;
	}
	
	public void run(){
		mMainUI.mTextArea.append("Movie Player Thr Start\n");
		mReadType = MessageReadType.CONSOL_READ;

		MessageBufferRead(mReadType);
		
		try {
			processMsg(mInMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(Socket socket){
		mMainUI.mTextArea.append("Movie Player Thr Start\n");
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
	private void processMsg(BufferedReader InMsg) throws IOException {
		while(true){
			String inLine = InMsg.readLine();
			
			if(inLine == null){
				mMainUI.mTextArea.append("command is null\n");
				return;
			} else{
				mMainUI.mTextArea.append(inLine + "\n");
			}
			
			StringTokenizer st = new StringTokenizer(inLine, "|");
			mMainUI.mTextArea.append("token counter " + st.countTokens() + "\n");
			if(st.countTokens() < 2){
				mMainUI.mTextArea.append("text Command Error \n");
				return;				
			}
			
			String command = st.nextToken();
			mCommand = st.nextToken();
			
			for(int temp=0; temp < mGroConf.getCommandLength(); temp ++){
				if(mGroConf.getCommandValue(temp).equals(command)){
					//command play
					if(mGroConf.getCommandName(temp).equals("play")){
						mMainUI.mTextArea.append("Command : " + mGroConf.getCommandName(temp) + "\n");
						playVideo(mCommand);	
						
						// Terminate the Player timer
						if(mPlayerTimer != null)
							mPlayerTimer.cancel();
					}
					else if(mGroConf.getCommandName(temp).equals("replay")){
						// Terminate the Player timer
						if(mPlayerTimer == null)
							mPlayerTimer = new Timer(false);
						else{
							mPlayerTimer.cancel();
							mPlayerTimer = new Timer(false);							
						}

						mMainUI.mTextArea.append("Command : " + mGroConf.getCommandName(temp) + "\n");
						mMainUI.mTextArea.append("Command : Timer ," + mGroConf.getCommandTime(temp) + "\n");
						mPlayerTimer.scheduleAtFixedRate(new PlayerTimer(), 0, 1000 * mGroConf.getCommandTime(temp));
					}
				}
				
			}
		}
	}
	
	private void playVideo(String value){
		mMainUI.mTextArea.append("Command Message : " + value + "\n");
		String mediaPlay = mGroConf.getMediaValue("video") + " " + value;
		
		if(mGroConf.isBatexecu()){
			mediaPlay = value;
		}
		else{
			mediaPlay = mGroConf.getMediaValue("video") + " " + value;
		}
		
		mMainUI.mTextArea.append("Player : " + mediaPlay +"\n");
		
		try {
			Runtime.getRuntime().exec(mediaPlay);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	class PlayerTimer extends TimerTask {

		@Override
		public void run() {

			if(mCommand != null){
				mMainUI.mTextArea.append(new Date() + " Timer Check\n");
				playVideo(mCommand);
			}
		}
	}
}
