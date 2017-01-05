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
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class mainFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final String mTitle = "Remote Controler";
	Container mMainContainer;
	public JTextArea mTextArea;
	private JScrollPane mScroolPane;
	public PopupMenu menu;
	public MenuItem mView;
	public MenuItem mQuit;
	public TrayIcon mTrayIcon;
	public SystemTray mTray;
	private JButton mBtnCommandButton;

	public JButton getBtnCommandButton() {
		return mBtnCommandButton;
	}

	private JTextField textFieldCommand;
		
	public String getCommandText() {
		return textFieldCommand.getText();
	}

	public mainFrame(){
		mMainContainer = this.getContentPane();
		setDesign();
	}
	
	private void setDesign(){
		setSize(600, 500);
		setTitle(mTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel CommandPanel = new JPanel();
		getContentPane().add(CommandPanel, BorderLayout.NORTH);
		
		JLabel lblCommandabel = new JLabel("Command");
		CommandPanel.add(lblCommandabel);
		
		textFieldCommand = new JTextField();
		CommandPanel.add(textFieldCommand);
		textFieldCommand.setColumns(40);
		
		mBtnCommandButton = new JButton("Send");
		CommandPanel.add(mBtnCommandButton);
		
		mScroolPane = new JScrollPane();
		mScroolPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		mTextArea = new JTextArea();
		mTextArea.setLineWrap(true);
		mTextArea.setWrapStyleWord(true);
		mTextArea.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		mScroolPane.setViewportView(mTextArea);
		
		

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
			mTrayIcon = new TrayIcon(iconImg, "RemoteControl", menu);
			try {
				mTray.add(mTrayIcon);
				mTrayIcon.setImageAutoSize(true);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void textFiledApped(String string) {
		mTextArea.append(string + "\n");		
	}
}
