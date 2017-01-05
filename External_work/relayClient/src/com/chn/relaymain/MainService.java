package com.chn.relaymain;

import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.chn.network.MessageListener;
import com.chn.network.NetworkClient;
import com.chn.network.NetworkServer;
import com.chn.ui.MainFrame;

public class MainService{
	public static NetworkServer mServer;
	public static NetworkClient mClient;
	private static int mPort;
	private static boolean mStarted =false;
	
	public static GrobalConfig mGrobalconf;
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
				mServer = new NetworkServer(mPort);
				mServer.Start(mGrobalconf);

				mClient = new NetworkClient(mMlistner);
		}
	}
	
	private static void initSystem(){
		
		mGrobalconf = new GrobalConfig();
		mMainUI = new MainFrame();
		mPort = mGrobalconf.getPort();
		
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
}
