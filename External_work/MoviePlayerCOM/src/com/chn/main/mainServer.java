package com.chn.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.chn.main.MovieProcess.MessageReadType;
import com.chn.network.networkServer;
import com.chn.ui.mainFrame;

public class mainServer{
	public static networkServer mServ;
	private static int mPort;
	private static MessageReadType mReadType;
	private static boolean mStarted =false;
	
	public static grobalConfig mGrobalconf;
	public static mainFrame mMainUI;
	
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
				mServ = new networkServer(mPort);
				mServ.Start(mReadType, mGrobalconf);
			}
			else if (mReadType == MessageReadType.CONSOL_READ){ // use Consol
				mServ = new networkServer();
				mServ.Start(mReadType, mGrobalconf);				
			}
		}
	}
	
	private static void initSystem(){
		
		mGrobalconf = new grobalConfig();
		mMainUI = new mainFrame();
		
		for(int temp=0; temp < mGrobalconf.getCommandLength(); temp++){
			mMainUI.mTextArea.append("Grobal Conf Name : " + mGrobalconf.getCommandName(temp)
					+ ", Grobal Conf Value : " + mGrobalconf.getCommandValue(temp) + "\n");
		}
		
		for(int temp=0; temp < mGrobalconf.getMediaLength(); temp++){
			mMainUI.mTextArea.append("Grobal Conf Name : " + mGrobalconf.getMediaName(temp)
					+ ", Grobal Conf Value : " + mGrobalconf.getMediaValue(temp) + "\n");
		}

		if(mGrobalconf.isTestMode()){
			mReadType = MessageReadType.CONSOL_READ;
		}
		else{
			mReadType = MessageReadType.NETWORK_READ;
			mPort = mGrobalconf.getPort();
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
		
		mServ.shutdownServer();
		System.exit(0);	
	}
}
