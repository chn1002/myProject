package com.chn.resource;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import com.chn.resource.ui.MoniterResourcePanel;
import com.chn.resource.ui.NetworkControlPanel;
import com.chn.resource.ui.USBResourcePanel;

public class mainApplication implements ActionListener{
	private static mainApplication mMainWindow;

	private JFrame frame;

	private SystemTray mSystemTray;
	private PopupMenu mPopup;
	private MenuItem mItemFrameVisible;
	private MenuItem mItemExit;

	private JTabbedPane mMainTabPane;
	private NetworkControlPanel mNetworkControlPanel;
	private USBResourcePanel mProjectControlPanel;
	private MoniterResourcePanel mMoniterPanel;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.out.println("Start :::");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mMainWindow = new mainApplication();
					mMainWindow.frame.setVisible(true);

				}  catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mainApplication() {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				System.out.println("Programe Shutdown");
				mNetworkControlPanel.exit();
			}
		});
		
		try{
			initSystemTrayIcon();
		} catch(AWTException e){
			e.printStackTrace();
		}

		initialize();

	}

	private void initSystemTrayIcon() throws AWTException {
		if(SystemTray.isSupported()){
			mPopup = new PopupMenu();
			mItemFrameVisible = new MenuItem("Invisible");
			mItemExit = new MenuItem("Exit");

			mItemFrameVisible.addActionListener(this);
			mItemExit.addActionListener(this);

			mPopup.add(mItemFrameVisible);
			mPopup.add(mItemExit);

			Image image = Toolkit.getDefaultToolkit().getImage("message_icon.png");
			TrayIcon icon = new TrayIcon(image, "Tray", mPopup);

			mSystemTray = SystemTray.getSystemTray();
			mSystemTray.add(icon);
		} else {
			System.out.println("is not SystemTray");
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblPcResourceProgram = new JLabel("PC Resource Program");
		frame.getContentPane().add(lblPcResourceProgram, BorderLayout.NORTH);

		mMainTabPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(mMainTabPane, BorderLayout.CENTER);

		mNetworkControlPanel = new NetworkControlPanel(this);
		mMainTabPane.addTab("Network Manager", null, mNetworkControlPanel, null);

		mProjectControlPanel = new USBResourcePanel(this);
		mMainTabPane.addTab("USB Manager", null, mProjectControlPanel, null);

		mMoniterPanel = new MoniterResourcePanel(this);
		mMainTabPane.addTab("Moniter Manager", null, mMoniterPanel, null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if(source.equals(mItemFrameVisible)){
			if(mMainWindow.frame.isVisible()){
				mItemFrameVisible.setLabel("Invisible");
				mMainWindow.frame.setVisible(false);
			} else {
				mItemFrameVisible.setLabel("Visible");
				mMainWindow.frame.setVisible(true);
			}
		} else if(source.equals(mItemExit)){
			systemExit();
		}
	}
	
	public void systemExit(){
		mNetworkControlPanel.exit();
		System.exit(0);
	}
}
