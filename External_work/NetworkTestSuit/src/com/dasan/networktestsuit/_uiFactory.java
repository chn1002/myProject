package com.dasan.networktestsuit;
import java.awt.FlowLayout;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JLabel;

public final class _uiFactory {
	/**
	 * @wbp.factory
	 */
	public static JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		return menuBar;
	}
	/**
	 * @wbp.factory
	 */
	public static JPanel createJPanel() {
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		return panel;
	}
	/**
	 * @wbp.factory
	 * @wbp.factory.parameter.source arg0 "Servie Type"
	 */
	public static JLabel createJLabel(String arg0) {
		JLabel label = new JLabel(arg0);
		return label;
	}
}