package org.anidev.frcds.proto.nc;

public abstract class NetconsoleListener {
	public void dataSent(NetconsoleMessage msg) {
	}
	public void receivedData(NetconsoleMessage msg) {
	}
	public void messagesCleared() {
	}
}
