package com.dasan.networktestsuit;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.FlowLayout;
import java.nio.file.AccessMode;
import javax.swing.JTable;
import javax.swing.Box;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import net.miginfocom.swing.MigLayout;

public class mainWindowApplication {

	private JFrame frmNetworkTestSuit;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainWindowApplication window = new mainWindowApplication();
					window.frmNetworkTestSuit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mainWindowApplication() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNetworkTestSuit = new JFrame();
		frmNetworkTestSuit.setTitle("Network Test Suit");
		frmNetworkTestSuit.setBounds(100, 100, 771, 447);
		frmNetworkTestSuit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = _uiFactory.createJMenuBar();
		frmNetworkTestSuit.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Menu");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnNewMenu.add(mntmExit);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmNetworkTestSuit.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel server_panel = _uiFactory.createJPanel();
		tabbedPane.addTab("Server", null, server_panel, null);
		server_panel.setLayout(new MigLayout("", "[750px]", "[179px][179px]"));
		
		JPanel client_panel = _uiFactory.createJPanel();
		tabbedPane.addTab("Client", null, client_panel, null);
		
		JLabel lblServieType = _uiFactory.createJLabel("Servie Type");
		client_panel.add(lblServieType);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"TCP Client", "UDP Client"}));
		client_panel.add(comboBox_1);
	}
}
