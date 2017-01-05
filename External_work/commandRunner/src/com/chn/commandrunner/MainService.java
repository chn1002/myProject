package com.chn.commandrunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.chn.network.MessageListener;
import com.chn.network.CommandRunnerNetworkClient;
import com.chn.network.CommandRunnerNetworkServer;
import com.chn.ui.MainFrame;

public class MainService{
	public static CommandRunnerNetworkServer mServer;
	public static CommandRunnerNetworkClient mClient;
	private static int mPort;
	private static boolean mStarted =false;

	private static MessageReadType mReadType;
	public static grobalConfig mGrobalconf;
	public static MainFrame mMainUI;

	public static void main(String[] args){
		// Program Start
		initSystem();
		mMainUI.setVisible(false);
		
		serverStart(mStarted);		
	}

	private static void serverStart(boolean bStart){
		if(bStart){
			// use Network 
			if(mReadType == MessageReadType.NETWORK_READ){
				// use Network 
				mServer = new CommandRunnerNetworkServer(mPort);
				mServer.Start(mGrobalconf);
			}
			else if (mReadType == MessageReadType.CONSOL_READ){ // use Consol
				System.out.println("Test Mode Console");
			}

		}
	}

	private static void initSystem(){

		mGrobalconf = new grobalConfig();
		mMainUI = new MainFrame();
		mPort = mGrobalconf.getPort();

		mMainUI.textFiledApped("Command Runner init System");

		mMainUI.textFiledApped("Mouse Point X : " + mGrobalconf.getMousePointX(0));
		mMainUI.textFiledApped("Mouse Point Y : " + mGrobalconf.getMousePointY(0));
		
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

		mMainUI.mTrayIcon.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
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
			}

		});

		mStarted =true;
	}

	private static void shutdownSystem(){
		if(mMainUI.isActive()){
			mMainUI.dispose();
		}

		if(mServer != null){
			mServer.shutdownServer();
		}

		System.exit(0);	
	}

	private static MessageListener mMlistner= new MessageListener() {
		@Override
		public void ReviceMessage(String Message) {
		}

	};
	
	public enum MessageReadType{
		NETWORK_READ,
		CONSOL_READ
	};
}
