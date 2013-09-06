package org.anidev.frcds.analyze;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFileChooser;

@SuppressWarnings("unused")
public class FileProvider {
	private static final String KDIALOG="kdialog --getopenfilename ~";
	private static final String ZENITY="zenity --file-selection";
	private static Runtime runtime=null;
	public static File askForFile(Component parent) {
//		if(runtime==null) {
//			runtime=Runtime.getRuntime();
//		}
//		String filename=tryProcessSelection(KDIALOG);
//		if(filename==null) {
//			filename=tryProcessSelection(ZENITY);
//		}
		JFileChooser chooser=new JFileChooser();
		int ret=chooser.showOpenDialog(parent);
		if(ret==JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
	private static String tryProcessSelection(String cmdline) {
		try {
			Process proc=runtime.exec(cmdline);
			if(proc.exitValue()!=0) {
				return null;
			}
			Scanner inputScanner=new Scanner(proc.getInputStream());
			String filename=inputScanner.nextLine();
			inputScanner.close();
			return filename;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
