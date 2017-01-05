package com.chn.resource;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResourceTrayIcon implements ActionListener{
	private SystemTray mSystemTray;
	private PopupMenu mPopup;
	private MenuItem mItemOpen;

	public ResourceTrayIcon() {
		try{
			initSystemTrayIcon();
		} catch(AWTException e){
			e.printStackTrace();
		}
	}
	
	private void initSystemTrayIcon() throws AWTException {
		if(SystemTray.isSupported()){
			mPopup = new PopupMenu();
			mItemOpen = new MenuItem("Open");
			mItemOpen.addActionListener(this);
			
			mPopup.add(mItemOpen);
			Image image = Toolkit.getDefaultToolkit().getImage("./message_icon.png");
			TrayIcon icon = new TrayIcon(image, "Tray", mPopup);
			
			mSystemTray = SystemTray.getSystemTray();
			mSystemTray.add(icon);
		} else {
			System.out.println("is not SystemTray");
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
