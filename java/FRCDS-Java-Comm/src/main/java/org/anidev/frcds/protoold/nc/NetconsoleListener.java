package org.anidev.frcds.protoold.nc;

public abstract class NetconsoleListener {
	public void dataSent(NetconsoleMessage msg) {
	}
	public void receivedData(NetconsoleMessage msg) {
	}
	public void messagesCleared() {
	}
	public void pauseChanged(boolean paused) {
	}
}
