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
import org.anidev.frcds.proto.tods.FRCRobotControl;
import org.anidev.frcds.proto.torobot.FRCCommonControl;
import org.anidev.utils.Utils;

/**
 * This class provides easy-to-use network communication between the driver
 * station and the robot. It sends all outbound data on a separate thread in
 * order to prevent the main thread from freezing due to network I/O. It also
 * listens for incoming data from a robot and/or a driver station. All
 * communication is done over UDP.
 * <p>
 * All network communication performed by objects of this class can be stopped
 * by calling {@link #close()}. Once closed, the object cannot be used again.
 * 
 * @author Anirudh Bagde
 */
public class FRCCommunication {
	/**
	 * Data is sent to this port on the robot.
	 */
	public static final int TOROBOT_PORT=1110;
	/**
	 * Data is sent to this port on the driver station.
	 */
	public static final int TODS_PORT=1150;
	/**
	 * Indicates whether this object has closed network communication.
	 */
	private volatile boolean closed=false;
	/**
	 * Team ID. This is primarily used to calculated the robot address.
	 * 
	 * @see #setTeamID(int)
	 */
	private volatile int teamID;
	/**
	 * IP address of robot.
	 */
	private volatile InetAddress robotAddress=null;
	/**
	 * IP address of driver station.
	 */
	private volatile InetAddress dsAddress=null;
	/**
	 * Indicates if currently receiving data from the robot.
	 */
	private volatile boolean receivingFromRobot;
	/**
	 * Indicates if currently receiving data from the driver station.
	 */
	private volatile boolean receivingFromDS;
	// TODO These listeners whould be changed to ConcurrentLinkedLists
	/**
	 * List of registered listeners for data being sent from the robot.
	 */
	private volatile List<FRCCommunicationListener> robotListeners=Collections
			.synchronizedList(new ArrayList<FRCCommunicationListener>());
	/**
	 * List of registered listeners for data being sent from the driver station.
	 */
	private volatile List<FRCCommunicationListener> dsListeners=Collections
			.synchronizedList(new ArrayList<FRCCommunicationListener>());
	/**
	 * UDP socket for sending data. All UDP datagrams can be sent from a single
	 * datagram socket, since UDP is connectionless.
	 */
	private DatagramSocket sendDataSocket=null;
	/**
	 * UDP socket to listen for data sent from the robot.
	 */
	private DatagramSocket receiveFromRobotSocket;
	/**
	 * UDP socket to listen for data sent from the driver station.
	 */
	private DatagramSocket receiveFromDSSocket;
	/**
	 * @see ReceiveFromRobotWorker
	 */
	private Thread receiveFromRobotThread;
	/**
	 * @see ReceiveFromDSWorker
	 */
	private Thread receiveFromDSThread;
	/**
	 * @see SendToRobotWorker
	 */
	private Thread sendToRobotThread;
	/**
	 * @see SendToDSWorker
	 */
	private Thread sendToDSThread;
	/**
	 * Unbounded queue for storing data that needs to be sent to the robot.
	 */
	private BlockingQueue<byte[]> sendToRobotQueue;
	/**
	 * Unbounded queue for storing data that needs to be sent to the driver
	 * station.
	 */
	private BlockingQueue<byte[]> sendToDSQueue;

	/**
	 * Convenience constructor to initialize the object and start listening for
	 * data both from the robot as well as the drier station.
	 */
	public FRCCommunication() {
		this(true,false);
	}

	/**
	 * Initializes a new FRCCommunication object to start listening for data
	 * from the robot, from the driver station, both or neither. Will close if
	 * cannot open the send socket.
	 * 
	 * @param rRobot
	 *            True to listen for data sent from robot, false if not.
	 * @param rDS
	 *            True to listen for data sent from driver station, false if
	 *            not.
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
		sendToRobotThread=new Thread(new SendToRobotWorker(),
				"FRCComm Send to Robot");
		sendToDSThread=new Thread(new SendToDSWorker(),"FRCComm Send to DS");
	}

	/**
	 * Send the given data object to the robot. This method will serialize the
	 * data and put it into a queue, so the object can be reused. In order to
	 * use this method, the IP address of the robot must have been set. This is
	 * automatically calculated when the team ID is specified.
	 * 
	 * @param dsData
	 *            The data object to send.
	 */
	public void sendToRobot(FRCCommonControl dsData) {
		checkClosed();
		if(robotAddress==null) {
			throw new IllegalStateException(
					"No robot address; invaid or unset team ID");
		}
		byte[] dsDataBytes=dsData.serialize();
		sendToRobotQueue.add(dsDataBytes);
	}

	/**
	 * Send the given data object to the driver station. This method will
	 * serialize the data and put it into a queue, so the object can be reused.
	 * In order to use this method, the IP address of the driver station must
	 * have been set. This is automatically done when a data packet is received
	 * from the driver station, or it can be explicitly set with
	 * {@link #setDsAddress(InetAddress)}.
	 * 
	 * @param robotData
	 *            The data object to send.
	 */
	public void sendToDS(FRCRobotControl robotData) {
		checkClosed();
		if(dsAddress==null) {
			throw new IllegalStateException(
					"No DS address set; wait for DS or manually set.");
		}
		byte[] robotDataBytes=robotData.serialize();
		sendToDSQueue.add(robotDataBytes);
	}

	/**
	 * Registers a listener that will be notified when data sent by the robot is
	 * received.
	 * 
	 * @param listener
	 *            Listener to register.
	 */
	public void addRobotDataListener(FRCCommunicationListener listener) {
		checkClosed();
		robotListeners.add(listener);
	}

	/**
	 * Registers a listener that will be notified when data sent by the robot is
	 * received.
	 * 
	 * @param listener
	 *            Listener to register.
	 */
	public void addDSDataListener(FRCCommunicationListener listener) {
		checkClosed();
		dsListeners.add(listener);
	}

	/**
	 * Get the currently set team ID.
	 * 
	 * @return Team ID.
	 */
	public int getTeamID() {
		checkClosed();
		return teamID;
	}

	/**
	 * Change the team ID associated with this object. Doing this will
	 * recalculate the robot's IP address and overwrite the previously set robot
	 * IP address. If given a value <= 0, the address will be set to null.
	 * <p>
	 * Given a team ID of xxyy (eg. 0612), the IP address is 10.xx.yy.2 (eg.
	 * 10.6.12.2)
	 * <p>
	 * <b>Note:</b> The team ID set here has no effect on the team ID that is
	 * sent with the data packets.
	 * 
	 * @param teamID
	 *            Team ID of robot.
	 */
	public void setTeamID(int teamID) {
		checkClosed();
		if(teamID<=0) {
			robotAddress=null;
			return;
		}
		byte[] addrBytes=Utils.teamIDToAddress(teamID);
		addrBytes[3]=2;
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

	/**
	 * Get the currently set driver station IP address.
	 * 
	 * @return Driver station IP address.
	 */
	public InetAddress getDsAddress() {
		checkClosed();
		return dsAddress;
	}

	/**
	 * Explicitly set the driver station IP address. Once set here, it will not
	 * be overwritten in the future.
	 * 
	 * @param dsAddress
	 *            Driver station IP address.
	 */
	public void setDsAddress(InetAddress dsAddress) {
		checkClosed();
		this.dsAddress=dsAddress;
		if(!sendToDSThread.isAlive()) {
			sendToDSThread.start();
		}
	}

	/**
	 * Indicates whether this object is currently listening for data sent from
	 * the robot.
	 * 
	 * @return True if listening for robot data, false if not.
	 */
	public boolean isReceivingFromRobot() {
		checkClosed();
		return receivingFromRobot;
	}

	/**
	 * Set whether this object should be listening for robot data. Setting to
	 * true will create the listening network socket and start the thread.
	 * Setting to false will close the socket and stop the thread.
	 * 
	 * @param receivingFromRobot
	 *            True to start listening, false to stop.
	 * @return False only if a problem occurred trying to start listning, true
	 *         in all other cases.
	 */
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
			receiveFromRobotThread=new Thread(new ReceiveFromRobotWorker(),
					"FRCComm Receive from Robot");
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

	/**
	 * Indicates whether this object is currently listening for data sent from
	 * the driver station.
	 * 
	 * @return True if listening for driver station data, false if not.
	 */
	public boolean isReceivingFromDS() {
		checkClosed();
		return receivingFromDS;
	}

	/**
	 * 
	 * Set whether this object should be listening for driver station data.
	 * Setting to true will create the listening network socket and start the
	 * thread. Setting to false will close the socket and stop the thread.
	 * 
	 * @param receivingFromDS
	 *            True to start listening, false to stop.
	 * @return False only if a problem occurred trying to start listning, true
	 *         in all other cases.
	 */
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
			receiveFromDSThread=new Thread(new ReceiveFromDSWorker(),
					"FRCComm Receive from DS");
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

	/**
	 * Close all network communucation sockets and stop all related threads.
	 * Once called, this object cannot be used again, and attempts to call other
	 * methods will throw an exception.
	 */
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

	/**
	 * Check to see if this object was closed, and throw an
	 * IllegalStateException if it was.
	 */
	private void checkClosed() {
		if(closed) {
			throw new IllegalStateException("Communication has been closed");
		}
	}

	/**
	 * Worker thread that listens for data sent from the robot, deserializes it,
	 * and notifies all registered listeners. The thread will exit if the
	 * network socket is null or closed, or if the thread is interrupted.
	 * 
	 * @author Anirudh Bagde
	 */
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

	/**
	 * Worker thread that listens for data sent from the driver station,
	 * deserializes it, and notifies all registered listeners. The thread will
	 * exit if the network socket is null or closed, or if the thread is
	 * interrupted. It will also set the value of
	 * {@link FRCCommunication#dsAddress} if it is not already set.
	 * 
	 * @author Anirudh Bagde
	 */
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

	/**
	 * Worker thread that reads data packets from a queue and sends them to the
	 * robot. It will automatically set the correct IP address to
	 * {@link FRCCommunication#robotAddress} and the port to
	 * {@link FRCCommunication#TOROBOT_PORT}.
	 * 
	 * @author Anirudh Bagde
	 */
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

	/**
	 * Worker thread that reads data packets from a queue and sends them to the
	 * driver station. It will automatically set the correct IP address to
	 * {@link FRCCommunication#dsAddress} and the port to
	 * {@link FRCCommunication#TODS_PORT}.
	 * 
	 * @author Anirudh Bagde
	 */
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
