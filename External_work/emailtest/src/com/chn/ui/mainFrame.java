package com.chn.ui;

import java.awt.AWTException; 
import java.awt.Container;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
 
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class mainFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final String mTitle = "Email Server";
	Container mMainContainer;
	public JTextArea mTextArea;
	private JScrollPane mScroolPane;
	public PopupMenu menu;
	public MenuItem mView;
	public MenuItem mQuit;
	public TrayIcon mTrayIcon;
	public SystemTray mTray;
		
	public mainFrame(){
		mMainContainer = this.getContentPane();
		setDesign();
	}
	
	private void setDesign(){
		setSize(600, 500);
		setTitle(mTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mScroolPane = new JScrollPane();
		mScroolPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		mTextArea = new JTextArea();
		mTextArea.setLineWrap(true);
		mTextArea.setWrapStyleWord(true);
		mTextArea.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		mScroolPane.getViewport().add(mTextArea);
		
		

		mMainContainer.add(mScroolPane);
		mTextArea.setText("System Init.\n");
		
		menu = new PopupMenu("Menu");
		
		mView = new MenuItem("View");
		menu.add(mView);
		
		mQuit = new MenuItem("Quit");
		menu.add(mQuit);
		
		if(SystemTray.isSupported()){
			mTray = SystemTray.getSystemTray();
			Image iconImg = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("image/glass_green.png"));
//			Image iconImg = Toolkit.getDefaultToolkit().getImage("image/glass_green.png");
			mTrayIcon = new TrayIcon(iconImg, "Test", menu);
			try {
				mTray.add(mTrayIcon);
				mTrayIcon.setImageAutoSize(true);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
