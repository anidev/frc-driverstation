package org.anidev.frcds.analyze;

import java.io.File;
import java.io.FileInputStream;
import org.anidev.frcds.analyze.gui.AnalyzerFrame;
import org.anidev.frcds.proto.CommData;
import org.anidev.frcds.proto.FRCCommunication;
import org.anidev.frcds.proto.FRCCommunicationListener;
import org.anidev.frcds.proto.nc.Netconsole;
import org.anidev.frcds.proto.nc.NetconsoleListener;
import org.anidev.frcds.proto.nc.NetconsoleMessage;
import org.anidev.frcds.proto.torobot.FRCCommonControl;

public class AnalyzerProviderSelector {
	private AnalyzerFrame frame=null;
	private FRCCommunication frcComm=null;
	private Netconsole netcon=null;

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
		netcon=new Netconsole();
		netcon.addNetconsoleListener(new NetconsoleListener() {
			@Override
			public void receivedData(NetconsoleMessage msg) {
				System.out.print(msg.getMessage());
			}

			@Override
			public void dataSent(NetconsoleMessage msg) {
			}
		});
		netcon.sendData("asdf");
	}

	public void stopNetworkStream() {
		if(frcComm!=null) {
			frcComm.close();
			frcComm=null;
		}
		if(netcon!=null) {
			netcon.close();
			netcon=null;
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
