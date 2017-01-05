package com.chn.resource.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.chn.event.IEventHandler;
import com.chn.resource.VisitedEventArgs;
import com.chn.resource.mainApplication;
import com.chn.service.engine.PeerNetworkControler;

public class NetworkControlPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1976570725691978793L;
	private mainApplication mApplication;

	private EventProcess mProcess;
	private PeerNetworkControler mNetorkControl;
	private Document mDocument;

	JButton mBtnNewButton;
	JButton mBtnPCShutdown;
	JButton mBtnExit; 


	public NetworkControlPanel(mainApplication mMainWindow) {
		mApplication = mMainWindow;
		
		mProcess = new EventProcess();
		mNetorkControl = new PeerNetworkControler();
		mNetorkControl.mVisited.addEventHandler(mProcess);

		Thread NetowrkTreahd = new Thread(new Runnable() {

			@Override
			public void run() {
				mNetorkControl.reveiceBr();

			}
		});

		NetowrkTreahd.start();

		setLayout(new BorderLayout(0, 0));
		initialize();
	}


	public void initialize() {
		final JTextPane textPane = new JTextPane();
		add(textPane);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		mBtnNewButton = new JButton("Information");
		mBtnNewButton.addActionListener(this);

		mBtnPCShutdown = new JButton("PC Exit");
		mBtnPCShutdown.addActionListener(this);

		mBtnExit = new JButton("Exit");
		mBtnExit.addActionListener(this);

		panel.add(mBtnPCShutdown);
		panel.add(mBtnNewButton);
		panel.add(mBtnExit);
		mDocument = textPane.getDocument();

		try {
			mDocument.insertString(mDocument.getLength(), "Start Service\n", null);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if(source.equals(mBtnExit)){
			mApplication.systemExit();
		} else if(source.equals(mBtnNewButton)){
			showInformation();
		} else if(source.equals(mBtnPCShutdown)){
			pcExit();
		}
	}

	private void showInformation() {
		String Core = "Available processors (cores): " + 
				Runtime.getRuntime().availableProcessors()+ " \n";
		String freeMemory = "Free memory : " + 
				Runtime.getRuntime().freeMemory()/1024  + "KB\n";

		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		String maxMemoryString = "Maximum memory : " + 
				(maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1024)  + " KB\n";

		String totalMemory = "Total memory available to JVM (bytes): " + 
				Runtime.getRuntime().totalMemory()/1024 +" KB\n";

		try {
			mDocument.remove(0, mDocument.getLength());
			mDocument.insertString(mDocument.getLength(), Core, null);
			mDocument.insertString(mDocument.getLength(), freeMemory, null);
			mDocument.insertString(mDocument.getLength(), maxMemoryString, null);
			mDocument.insertString(mDocument.getLength(), totalMemory, null);
		} catch(BadLocationException exc) {
			exc.printStackTrace();
		}
	}

	void pcExit(){
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("C:\\WINDOWS\\system32\\cmd.exe");
			OutputStream os = process.getOutputStream();
			os.write("shutdown -s -f -t 10 \n\r".getBytes());
			os.close();
			process.waitFor();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void exit(){
		mNetorkControl.setmReviceBroadcastSockeEnable(false);
	}

	class EventProcess  implements IEventHandler<VisitedEventArgs>{
		@Override
		public void eventReceived(Object sender, VisitedEventArgs e) {
			System.out.println("Recive Message : " + e.getMessage());
			try {

				switch(e.getCommand()){
				case VisitedEventArgs.NETWORK_MESSGAE :
					mDocument.insertString(mDocument.getLength(), e.getMessage() + "\n", null);
					break;
				case VisitedEventArgs.POWER_OFF :
					mDocument.insertString(mDocument.getLength(), e.getMessage() + "\n", null);
					System.out.println("System is Exit");
					pcExit();		
					break;
				}
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

		}
	}

}
