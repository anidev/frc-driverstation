package org.anidev.frcds.pc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.anidev.frcds.common.DriverStation;
import org.anidev.frcds.common.types.NetconsoleMessage;
import org.anidev.frcds.proto.Netconsole;
import org.anidev.frcds.proto.NetconsoleListener;
import org.anidev.frcds.pc.gui.DriverStationFrame;
import org.anidev.frcds.pc.gui.NetconsolePanel;

public class PCDriverStation extends DriverStation {
	private DriverStationFrame frame=null;
	private final Netconsole netconsole=new Netconsole();
	private final List<NetconsolePanel> netconsolePanels=Collections
			.synchronizedList(new ArrayList<NetconsolePanel>());
	private final List<NetconsoleMessage> netconsoleMessages=Collections
			.synchronizedList(new ArrayList<NetconsoleMessage>());

	public PCDriverStation() {
		this.startLoops();
		netconsole.addNetconsoleListener(new PCDSNetconsoleListener());
//		netconsoleMessages.add(new NetconsoleMessage(NetconsoleMessage.Type.TODS,"asdf"));
	}
	
	public void setFrame(DriverStationFrame frame) {
		this.frame=frame;
		setElapsedTimeImpl();
		setTeamIDImpl();
		setBatteryPercentImpl();
		setModeImpl();
	}
	
	public DriverStationFrame getFrame() {
		return frame;
	}

	public void addNetconsolePanel(NetconsolePanel panel) {
		netconsolePanels.add(panel);
	}

	public void removeNetconsolePanel(NetconsolePanel panel) {
		netconsolePanels.remove(panel);
	}

	public List<NetconsoleMessage> getNetconsoleMessages() {
		return netconsoleMessages;
	}

	@Override
	protected void setEnabledImpl() {
	}

	@Override
	protected void setElapsedTimeImpl() {
		if(frame==null) {
			return;
		}
		frame.setElapsedTime(elapsedTime);
	}

	@Override
	protected void setTeamIDImpl() {
		if(frame==null) {
			return;
		}
		frame.setTeamID(teamID);
	}

	@Override
	protected void setBatteryPercentImpl() {
		if(frame==null) {
			return;
		}
		frame.setBatteryPercent(batteryPercent);
	}

	@Override
	protected void setModeImpl() {
	}

	private class PCDSNetconsoleListener implements NetconsoleListener {
		@Override
		public void receivedData(String data) {
			NetconsoleMessage msg=new NetconsoleMessage(
					NetconsoleMessage.Type.TODS,data);
			synchronized(netconsoleMessages) {
				netconsoleMessages.add(msg);
			}
			synchronized(netconsolePanels) {
				for(NetconsolePanel panel:netconsolePanels) {
					panel.fireMessagesAdded();
				}
			}
		}
	}
}
