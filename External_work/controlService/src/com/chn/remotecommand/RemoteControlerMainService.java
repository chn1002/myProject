package com.chn.remotecommand;

import java.awt.event.ActionEvent; 

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import com.chn.network.MessageListener;
import com.chn.network.NetworkClient;
import com.chn.remotecommand.controlProcess.MessageReadType;
import com.chn.remotecommand.grobalConfig.COMMAND_TYPE;
import com.chn.ui.mainFrame;

public class RemoteControlerMainService{
	private static MessageReadType mReadType;
	private static boolean mStarted =false;

	public static grobalConfig mGrobalconf;
	public static mainFrame mMainUI;

	public static void main(String[] args){
		// Program Start
		initSystem();
		mMainUI.setVisible(false);

		serviceStart(mStarted);	
	}

	private static void serviceStart(boolean bStart){
		int StartTimer, RepeatTimer, PingpongTimeer;

		if(bStart){
			// use Network 
			if(mReadType == MessageReadType.NETWORK_READ){
				mMainUI.textFiledApped("Type: Network");

				mMainUI.textFiledApped("Max Remote Number : " + + mGrobalconf.getRemoteServerNumber());
				for(int i=0; i < mGrobalconf.getRemoteServerNumber(); i++){
					mMainUI.textFiledApped("Item : " + i);
					mMainUI.textFiledApped("Remote Server : " + mGrobalconf.getRemoteServerAddress(i));
					mMainUI.textFiledApped("Remote Server Port : " + mGrobalconf.getRemoteServerPort(i));
				}

				StartTimer = mGrobalconf.StartCommandTime() * 1000;
				RepeatTimer = mGrobalconf.RepeatCommandTime() * 1000;
				PingpongTimeer = mGrobalconf.PingpongTime() * 1000;

				mGrobalconf.setCommandReady(true);
				sendCommand(COMMAND_TYPE.PINGPONG_FUNCTION);

				TimerTask startCommand = new TimerTask() {

					@Override
					public void run() {
						if(mGrobalconf.isCommandReady()){
							sendCommand(COMMAND_TYPE.START_COMMAND);
						} else {
							mMainUI.textFiledApped("Start Command: Command Server is not Ready");
						}
					}
				};

				TimerTask repeatCommand = new TimerTask() {

					@Override
					public void run() {
						if(mGrobalconf.isCommandReady()){
							sendCommand(COMMAND_TYPE.REPEAT_COMMAND);
						} else {
							mMainUI.textFiledApped("Repeat Command: Command Server is not Ready");
						}
					}
				};

				TimerTask pingpongFunction = new TimerTask() {

					@Override
					public void run() {
						mGrobalconf.setCommandReady(true);
						sendCommand(COMMAND_TYPE.PINGPONG_FUNCTION);
					}
				};

				Timer jobScheduler = new Timer();

				if(StartTimer != 0){
					jobScheduler.schedule(startCommand, StartTimer);
				}

				//				if(RepeatTimer != 0){
				//					jobScheduler.scheduleAtFixedRate(repeatCommand, RepeatTimer+StartTimer, RepeatTimer);
				//				}

				//				if(PingpongTimeer != 0){
				//					jobScheduler.scheduleAtFixedRate(pingpongFunction, PingpongTimeer, PingpongTimeer);
				//				}

			}
			else if (mReadType == MessageReadType.CONSOL_READ){ // use Consol
				System.out.println("Test Mode Console");
				mMainUI.textFiledApped("Type: Consle");
			}
		}
	}

	private static void initSystem(){

		mGrobalconf = new grobalConfig();
		mMainUI = new mainFrame();

		if(mGrobalconf.isConsleMode()){
			mReadType = MessageReadType.CONSOL_READ;
		}
		else{
			mReadType = MessageReadType.NETWORK_READ;
		}

		mMainUI.mQuit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				shutdownSystem();
			}

		});

		mMainUI.mView.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(mMainUI.isVisible()){
					mMainUI.mView.setLabel("View");
					mMainUI.setVisible(false);
				}
				else{
					mMainUI.mView.setLabel("Hide");
					mMainUI.setVisible(true);					
				}
			}
		});

		mMainUI.getBtnCommandButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(event.getSource().equals(mMainUI.getBtnCommandButton())){
					sendCommand(COMMAND_TYPE.INPUT_COMMNAD);
				}
			}
		});


		mMainUI.mTrayIcon.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getClickCount() == 2){
					if(mMainUI.isVisible()){
						mMainUI.mView.setLabel("View");
						mMainUI.setVisible(false);
					}
					else{
						mMainUI.mView.setLabel("Hide");
						mMainUI.setVisible(true);					
					}				
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		mStarted =true;
	}

	private static void shutdownSystem(){
		if(mMainUI.isActive()){
			mMainUI.dispose();
		}

		System.exit(0);	
	}

	private static MessageListener mMlistner= new MessageListener() {

		@Override
		public void ReviceMessage(String Message) {
		}

	};
	
	static class commandThread extends Thread{
		int number = 0;
		COMMAND_TYPE commandType;
		
		public commandThread() {
		}
		
		public void setNumber(int num){
			this.number = num;
		}
		
		public void setCommandType(COMMAND_TYPE type){
			this.commandType = type;
		}
		
		@Override
		public void run() {
			int port = 0;
			boolean connection = true;
			NetworkClient client;
			client = new NetworkClient(mMlistner);
			String command = "";
			boolean functionEnable = false;
			boolean isPong = false;

			port = Integer.parseInt(mGrobalconf.getRemoteServerPort(number));
			mMainUI.textFiledApped("Item : " + number);
			client.setIpAddress(mGrobalconf.getRemoteServerAddress(number), port);//
			connection = client.socketConnect();
			if(connection){
				switch(commandType){
				case INPUT_COMMNAD :
					command = mMainUI.getCommandText();
					break;
				case REPEAT_COMMAND :
					command = mGrobalconf.getRemoteServerRepeatCommand(number);
					break;
				case START_COMMAND :
					command = mGrobalconf.getRemoteServerCommand(number);
					break;
				case PINGPONG_FUNCTION :
					command = grobalConfig.PING;
					functionEnable = mGrobalconf.getPingpongfunction(number);
					break;
				default:
					command = "play";
					break;

				}

				if(commandType == COMMAND_TYPE.PINGPONG_FUNCTION &&
						mGrobalconf.getPingpongfunction(number)){
					if(functionEnable){
						isPong = client.sendPingpong(command);

						mMainUI.textFiledApped(number + " Remote Server (PINGPONG Function) : " + mGrobalconf.getRemoteServerAddress(number) + ":" 
								+ mGrobalconf.getRemoteServerPort(number));
						mMainUI.textFiledApped(command + " :: " + isPong);

						if(!isPong){
							mGrobalconf.setCommandReady(false);
						}

					} else {
						mMainUI.textFiledApped(number + " Remote Server (PINGPONG Function) : " + mGrobalconf.getRemoteServerAddress(number) + ":" 
								+ mGrobalconf.getRemoteServerPort(number));
						mMainUI.textFiledApped(command);
					}
				} else {
					client.sendMessage(command);
					mMainUI.textFiledApped(number + " Remote Server : " + mGrobalconf.getRemoteServerAddress(number) + ":" 
							+ mGrobalconf.getRemoteServerPort(number));
					mMainUI.textFiledApped(command);
				}

			} else {
				mGrobalconf.setCommandReady(false);
				mMainUI.textFiledApped(number + " Remote Server not connection : " + mGrobalconf.getRemoteServerAddress(number) + ":"
						+ mGrobalconf.getRemoteServerPort(number));
			}

			super.run();
		}
	
	}

	protected static void sendCommand(COMMAND_TYPE commandType) {
		for(int i=0; i < mGrobalconf.getRemoteServerNumber(); i++){
			commandThread command = new commandThread();
			command.setNumber(i);
			command.setCommandType(commandType);
			command.start();
		}
	}
}
