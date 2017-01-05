package com.chn.resource.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.chn.resource.mainApplication;

public class USBResourcePanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private mainApplication mApplication;

	private Document mDocument;

	JButton mBtnNewButton;
	JButton mBtnExit; 


	public USBResourcePanel(mainApplication application) {
		mApplication = application;

		setLayout(new BorderLayout(0, 0));
		initialize();
		initDevice();
	}

	private void initDevice() {
	}

	public void initialize() {
		final JTextPane textPane = new JTextPane();
		add(textPane);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		mBtnNewButton = new JButton("Information");
		mBtnNewButton.addActionListener(this);

		mBtnExit = new JButton("Exit");
		mBtnExit.addActionListener(this);

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
		if(e.getSource().equals(mBtnExit)){
			mApplication.systemExit();
		}

	}
}
