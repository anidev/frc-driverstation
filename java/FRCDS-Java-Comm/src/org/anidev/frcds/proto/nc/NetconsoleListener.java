package org.anidev.frcds.proto.nc;

public interface NetconsoleListener {
	public void dataSent(NetconsoleMessage msg);
	public void receivedData(NetconsoleMessage msg);
}
