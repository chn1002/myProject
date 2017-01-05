package com.chn.videoplayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class VideoPlayer extends javax.swing.JPanel{
	public VideoPlayer(URL mediaUrl){
		initComponets();
		setLayout(new BorderLayout());

		try {
			Player mediaPlayer = Manager.createRealizedPlayer(new MediaLocator(mediauUrl));

			Component video=mediaPlayer.getVisualComponent();
			Component control=mediaPlayer.getControlPanelComponent();

			if (video!=null) {
				add(video, BorderLayout.CENTER);          // place the video component in the panel
			}

			add(control, BorderLayout.SOUTH);            // place the control in  panel
			mediaPlayer.start();
		} catch (Exception e) {

		}
	}
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(null);
		URL mediaUrl=null;
		try {
			mediaUrl = fileChooser.getSelectedFile().toURI().toURL();
		} catch (MalformedURLException ex) {
			System.out.println(ex);
		}

		JFrame mediaTest = new JFrame( "Movie Player" );
		mediaTest.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		VideoPlayer mediaPanel = new VideoPlayer( mediaUrl );

		mediaTest.add( mediaPanel );
		mediaTest.setSize( 800, 700 ); // set the size of the player
		mediaTest.setLocationRelativeTo(null);
		mediaTest.setVisible( true );
	}

}
