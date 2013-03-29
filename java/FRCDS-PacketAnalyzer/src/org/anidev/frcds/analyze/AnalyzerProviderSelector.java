package org.anidev.frcds.analyze;

import java.io.File;
import java.io.FileInputStream;
import org.anidev.frcds.analyze.gui.AnalyzerFrame;
import org.anidev.frcds.proto.CommData;
import org.anidev.frcds.proto.FRCCommunication;
import org.anidev.frcds.proto.FRCCommunicationListener;
import org.anidev.frcds.proto.torobot.FRCCommonControl;

public class AnalyzerProviderSelector {
	private AnalyzerFrame frame=null;
	private FRCCommunication frcComm=null;
	public void registerFrame(AnalyzerFrame frame) {
		this.frame=frame;
	}
	public void startNetworkStream() {
		frcComm=new FRCCommunication(false,true);
		frcComm.addDSDataListener(new FRCCommunicationListener() {
			public void receivedData(CommData data) {
				if(!(data instanceof FRCCommonControl)) {
					return;
				}
				FRCCommonControl control=(FRCCommonControl)data;
				frame.updateValues(control);
			}
		});
	}
	public void stopNetworkStream() {
		if(frcComm!=null) {
			frcComm.close();
			frcComm=null;
		}
	}
	public void startFile(File file) {
		stopNetworkStream();
		byte[] fileBytes=new byte[FRCCommonControl.SIZE];
		try {
			FileInputStream iStream=new FileInputStream(file);
			iStream.read(fileBytes);
			iStream.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		FRCCommonControl control=new FRCCommonControl();
		control.deserialize(fileBytes);
		frame.updateValues(control);
	}
}
