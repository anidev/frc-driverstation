package org.anidev.frcds.proto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.anidev.frcds.proto.tods.FRCRobotControl;
import org.anidev.frcds.proto.torobot.FRCCommonControl;

public class FRCCommunication {
	public static final int TOROBOT_PORT=1110;
	public static final int TODS_PORT=1150;
	private volatile boolean closed=false;
	private volatile int queueTimeout=40;
	private volatile int teamID;
	private volatile InetAddress robotAddress=null;
	private volatile InetAddress dsAddress=null;
	private volatile boolean receivingFromRobot;
	private volatile boolean receivingFromDS;
	private volatile List<FRCCommunicationListener> robotListeners=Collections
			.synchronizedList(new ArrayList<FRCCommunicationListener>());
	private volatile List<FRCCommunicationListener> dsListeners=Collections
			.synchronizedList(new ArrayList<FRCCommunicationListener>());
	private DatagramSocket sendDataSocket=null;
	private DatagramSocket receiveFromRobotSocket;
	private DatagramSocket receiveFromDSSocket;
	private Thread sendToRobotThread;
	private Thread sendToDSThread;
	private Thread receiveFromRobotThread;
	private Thread receiveFromDSThread;
	private BlockingQueue<byte[]> sendToRobotQueue;
	private BlockingQueue<byte[]> sendToDSQueue;

	public FRCCommunication() {
		this(true,false);
	}

	/**
	 * Will close if cannot open send socket.
	 * 
	 * @param rRobot
	 * @param rDS
	 */
	public FRCCommunication(boolean rRobot,boolean rDS) {
		try {
			sendDataSocket=new DatagramSocket();
		} catch(SocketException e) {
			e.printStackTrace();
			close();
		}
		setReceivingFromRobot(rRobot);
		setReceivingFromDS(rDS);
		sendToRobotQueue=new LinkedBlockingQueue<byte[]>();
		sendToDSQueue=new LinkedBlockingQueue<byte[]>();
		sendToRobotThread=new Thread(new SendToRobotWorker(),"FRCComm Send to Robot");
		sendToDSThread=new Thread(new SendToDSWorker(),"FRCComm Send to DS");
	}

	public void sendToRobot(FRCCommonControl dsData) {
		checkClosed();
		if(robotAddress==null) {
			throw new IllegalStateException(
					"No robot address; invaid or unset team ID");
		}
		byte[] dsDataBytes=dsData.serialize();
		try {
			sendToRobotQueue.offer(dsDataBytes,queueTimeout,
					TimeUnit.MILLISECONDS);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void sendToDS(FRCRobotControl robotData) {
		checkClosed();
		if(dsAddress==null) {
			throw new IllegalStateException(
					"No DS address set; wait for DS or manually set.");
		}
		byte[] robotDataBytes=robotData.serialize();
		try {
			sendToDSQueue.offer(robotDataBytes,queueTimeout,
					TimeUnit.MILLISECONDS);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void addRobotDataListener(FRCCommunicationListener listener) {
		checkClosed();
		robotListeners.add(listener);
	}

	public void addDSDataListener(FRCCommunicationListener listener) {
		checkClosed();
		dsListeners.add(listener);
	}

	public int getQueueTimeout() {
		checkClosed();
		return queueTimeout;
	}

	public void setQueueTimeout(int queueTimeout) {
		checkClosed();
		this.queueTimeout=queueTimeout;
	}

	public int getTeamID() {
		checkClosed();
		return teamID;
	}

	public void setTeamID(int teamID) {
		checkClosed();
		if(teamID<=0) {
			robotAddress=null;
			return;
		}
		int teamUpper=teamID/100;
		int teamLower=teamID-teamUpper*100;
		byte[] addrBytes=new byte[] {10,(byte)teamUpper,(byte)teamLower,2};
		try {
			robotAddress=InetAddress.getByAddress(addrBytes);
			if(!sendToRobotThread.isAlive()) {
				sendToRobotThread.start();
			}
		} catch(UnknownHostException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid team ID: "+teamID);
		}
		this.teamID=teamID;
	}

	public InetAddress getDsAddress() {
		checkClosed();
		return dsAddress;
	}

	public void setDsAddress(InetAddress dsAddress) {
		checkClosed();
		this.dsAddress=dsAddress;
		if(!sendToDSThread.isAlive()) {
			sendToDSThread.start();
		}
	}

	public boolean isReceivingFromRobot() {
		checkClosed();
		return receivingFromRobot;
	}

	public boolean setReceivingFromRobot(boolean receivingFromRobot) {
		checkClosed();
		if(receivingFromRobot) {
			try {
				receiveFromRobotSocket=new DatagramSocket(TODS_PORT);
			} catch(SocketException e) {
				e.printStackTrace();
				System.err.println("Disabling receive from robot");
				this.receivingFromRobot=false;
				receiveFromRobotSocket=null;
				return false;
			}
			receiveFromRobotThread=new Thread(new ReceiveFromRobotWorker(),"FRCComm Receive from Robot");
			receiveFromRobotThread.start();
		} else {
			if(receiveFromRobotSocket!=null) {
				receiveFromRobotSocket.close();
				receiveFromRobotSocket=null;
			}
			if(receiveFromDSThread!=null) {
				receiveFromDSThread.interrupt();
				receiveFromDSThread=null;
			}
		}
		this.receivingFromRobot=receivingFromRobot;
		return true;
	}

	public boolean isReceivingFromDS() {
		checkClosed();
		return receivingFromDS;
	}

	public boolean setReceivingFromDS(boolean receivingFromDS) {
		checkClosed();
		if(receivingFromDS) {
			try {
				receiveFromDSSocket=new DatagramSocket(TOROBOT_PORT);
			} catch(SocketException e) {
				e.printStackTrace();
				System.err.println("Disabling receive from DS");
				this.receivingFromDS=false;
				receiveFromDSSocket=null;
				return false;
			}
			receiveFromDSThread=new Thread(new ReceiveFromDSWorker(),"FRCComm Receive from DS");
			receiveFromDSThread.start();
		} else {
			if(receiveFromDSSocket!=null) {
				receiveFromDSSocket.close();
				receiveFromDSSocket=null;
			}
			if(receiveFromDSThread!=null) {
				receiveFromRobotThread.interrupt();
				receiveFromDSThread=null;
			}
		}
		this.receivingFromDS=receivingFromDS;
		return true;
	}

	public synchronized void close() {
		checkClosed();
		setReceivingFromRobot(false);
		setReceivingFromDS(false);
		robotListeners.clear();
		robotListeners=null;
		dsListeners.clear();
		dsListeners=null;
		if(sendDataSocket!=null) {
			sendDataSocket.close();
		}
		if(sendToRobotThread!=null) {
			sendToRobotThread.interrupt();
		}
		if(sendToDSThread!=null) {
			sendToDSThread.interrupt();
		}
		if(sendToRobotQueue!=null) {
			sendToRobotQueue.clear();
		}
		if(sendToDSQueue!=null) {
			sendToDSQueue.clear();
		}
		closed=true;
	}

	private void checkClosed() {
		if(closed) {
			throw new IllegalStateException("Communication has been closed");
		}
	}

	private class ReceiveFromRobotWorker implements Runnable {
		@Override
		public void run() {
			while(receiveFromRobotSocket!=null&&!Thread.interrupted()
					&&!receiveFromRobotSocket.isClosed()) {
				int length=FRCRobotControl.SIZE;
				byte[] buffer=new byte[length];
				DatagramPacket dataPacket=new DatagramPacket(buffer,length);
				try {
					receiveFromRobotSocket.receive(dataPacket);
				} catch(SocketException e) {
					break;
				} catch(IOException e) {
					e.printStackTrace();
				}
				FRCRobotControl robotData=new FRCRobotControl();
				robotData.deserialize(buffer);
				synchronized(robotListeners) {
					for(FRCCommunicationListener listener:robotListeners) {
						listener.receivedData(robotData);
					}
				}
			}
		}
	}

	private class ReceiveFromDSWorker implements Runnable {
		@Override
		public void run() {
			while(receiveFromDSSocket!=null&&!Thread.interrupted()
					&&!receiveFromDSSocket.isClosed()&&!closed) {
				int length=FRCCommonControl.SIZE;
				byte[] buffer=new byte[length];
				DatagramPacket dataPacket=new DatagramPacket(buffer,length);
				try {
					receiveFromDSSocket.receive(dataPacket);
				} catch(SocketException e) {
					break;
				} catch(IOException e) {
					e.printStackTrace();
				}
				if(dsAddress==null) {
					InetAddress packetAddress=dataPacket.getAddress();
					if(closed) {
						break;
					}
					setDsAddress(packetAddress);
				}
				FRCCommonControl controlData=new FRCCommonControl();
				controlData.deserialize(buffer);
				synchronized(dsListeners) {
					for(FRCCommunicationListener listener:dsListeners) {
						listener.receivedData(controlData);
					}
				}
			}
		}
	}

	private class SendToRobotWorker implements Runnable {
		@Override
		public void run() {
			while(sendDataSocket!=null&&!Thread.interrupted()
					&&!sendDataSocket.isClosed()&&!closed) {
				byte[] dsData=null;
				try {
					dsData=sendToRobotQueue.take();
				} catch(InterruptedException e) {
					break;
				}
				DatagramPacket dsDataPacket=new DatagramPacket(dsData,
						dsData.length);
				dsDataPacket.setAddress(robotAddress);
				dsDataPacket.setPort(TOROBOT_PORT);
				try {
					sendDataSocket.send(dsDataPacket);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class SendToDSWorker implements Runnable {
		@Override
		public void run() {
			while(!Thread.interrupted()&&!sendDataSocket.isClosed()&&!closed) {
				byte[] robotData=null;
				try {
					robotData=sendToDSQueue.take();
				} catch(InterruptedException e) {
					break;
				}
				DatagramPacket robotDataPacket=new DatagramPacket(robotData,
						robotData.length);
				robotDataPacket.setAddress(dsAddress);
				robotDataPacket.setPort(TODS_PORT);
				try {
					sendDataSocket.send(robotDataPacket);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
