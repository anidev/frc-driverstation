package org.anidev.frcds.analyze;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.anidev.frcds.analyze.gui.AnalyzerFrame;

public class AnalyzerMain {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e) {
			System.err.println("Error while setting Nimbus L&F.");
			e.printStackTrace();
		}
		AnalyzerFrame frame=new AnalyzerFrame(new AnalyzerProviderSelector());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
