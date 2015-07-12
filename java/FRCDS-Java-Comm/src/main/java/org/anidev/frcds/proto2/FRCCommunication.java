package org.anidev.frcds.proto2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.anidev.frcds.proto2.DSDataListener;
import org.anidev.frcds.proto2.RobotDataListener;
import com.rits.cloning.Cloner;

/**
 * Base class for all DS<->robot communication. This class is the main interface
 * of the communication library. It takes care of the managing the control data,
 * addresses, threading, and whatever else is necessary. Subclasses will
 * implement the protocol-specific methods that determine how the data is
 * serialized/deserialized and sent/received.
 * 
 * @author Anirudh Bagde
 */
public abstract class FRCCommunication {
	/**
	 * Robot data that will be serialized and sent to the driver station by the
	 * protocol.
	 */
	protected volatile RobotData robotSendData;
	/**
	 * Driver station data that will be serialized and sent to the robot by the
	 * protocol.
	 */
	protected volatile DSData dsSendData;

	/**
	 * Indicates whether this object has closed network communication.
	 */
	private volatile boolean closed=false;
	/**
	 * Indicates if currently receiving data from the robot.
	 */
	private volatile boolean receivingFromRobot;
	/**
	 * Indicates if currently receiving data from the driver station.
	 */
	private volatile boolean receivingFromDS;
	/**
	 * List of registered listeners for data being sent from the robot.
	 */
	private List<RobotDataListener> robotListeners=new CopyOnWriteArrayList<>();
	/**
	 * List of registered listeners for data being sent from the driver station.
	 */
	private List<DSDataListener> dsListeners=new CopyOnWriteArrayList<>();
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
	 * Used to clone the RobotData and DSData objects.
	 */
	private Cloner cloner=new Cloner();

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
	 *            True to listen for data sent from driver staUnable to
	 *            connecttion, false if not.
	 */
	public FRCCommunication(boolean rRobot,boolean rDS) {
		initProtocol();
		setReceivingFromRobot(rRobot);
		setReceivingFromDS(rDS);
		sendToRobotThread=new Thread(new SendToRobotWorker(),
				"FRCComm Send to Robot");
		sendToDSThread=new Thread(new SendToDSWorker(),"FRCComm Send to DS");
	}

	/**
	 * Set the robot data object that will be sent to the driver station by the
	 * protocol. The exact method of serialization, frequency of transmission,
	 * etc are determined by the specific protocol.
	 * <p>
	 * This method will create a clone of the given data parameter, so it can be
	 * easily reused.
	 * 
	 * @param data
	 *            The object that represents the robot data to be sent.
	 */
	public void setRobotSendData(RobotData data) {
		RobotData copy=cloner.deepClone(data);
		robotSendData=copy;
	}

	/**
	 * Returns the currently set robot data object that is to be sent to the
	 * driver station. The object will be cloned so modifications to it do not
	 * interfere with the sending thread.
	 * 
	 * @return A clone of the currently set robot data object.
	 */
	public RobotData getRobotSendData() {
		RobotData data=robotSendData;
		return cloner.deepClone(data);
	}

	/**
	 * Set the driver station data object that will be sent to the robot by the
	 * protocol. The exact method of serialization, frequency of transmission,
	 * etc are determined by the specific protocol.
	 * <p>
	 * This method will create a clone of the given data parameter, so it can be
	 * easily reused.
	 * 
	 * @param data
	 *            The object that represents the driver station data to be sent.
	 */
	public void setDSSendData(DSData data) {
		DSData copy=cloner.deepClone(data);
		dsSendData=copy;
	}

	/**
	 * Returns the currently set driver station data object that is to be sent
	 * to the robot. The object will be cloned so modifications to it do not
	 * interfere with the sending thread.
	 * 
	 * @return A clone of the currently set driver station data object.
	 */
	public DSData getDSSendData() {
		DSData data=dsSendData;
		return cloner.deepClone(data);
	}

	/**
	 * Registers a listener that will be notified when data sent by the robot is
	 * received.
	 * 
	 * @param listener
	 *            Listener to register.
	 */
	public void addRobotDataListener(RobotDataListener listener) {
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
	public void addDSDataListener(DSDataListener listener) {
		checkClosed();
		dsListeners.add(listener);
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
	 * Setting to false will close the socket and stop the thread. Specific
	 * implementations should override this.
	 * 
	 * @param receivingFromRobot
	 *            True to start listening, false to stop.
	 * @return False only if a problem occurred trying to start listning, true
	 *         in all other cases.
	 */
	public boolean setReceivingFromRobot(boolean receivingFromRobot) {
		checkClosed();
		if(receivingFromRobot) {
			receiveFromRobotThread=new Thread(new ReceiveFromRobotWorker(),
					"FRCComm Receive from Robot");
			receiveFromRobotThread.start();
		} else {
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
	 * Specific implementations should override this.
	 * 
	 * @param receivingFromDS
	 *            True to start listening, false to stop.
	 * @return False only if a problem occurred trying to start listning, true
	 *         in all other cases.
	 */
	public boolean setReceivingFromDS(boolean receivingFromDS) {
		checkClosed();
		if(receivingFromDS) {
			receiveFromDSThread=new Thread(new ReceiveFromDSWorker(),
					"FRCComm Receive from DS");
			receiveFromDSThread.start();
		} else {
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
	 * methods will throw an exception. Specific implementations should override
	 * this.
	 */
	public synchronized void close() {
		checkClosed();
		setReceivingFromRobot(false);
		setReceivingFromDS(false);
		robotListeners.clear();
		robotListeners=null;
		dsListeners.clear();
		dsListeners=null;
		if(sendToRobotThread!=null) {
			sendToRobotThread.interrupt();
		}
		if(sendToDSThread!=null) {
			sendToDSThread.interrupt();
		}
		closed=true;
	}

	/**
	 * Check to see if this object was closed, and throw an
	 * IllegalStateException if it was.
	 */
	protected void checkClosed() {
		if(closed) {
			throw new IllegalStateException("Communication has been closed");
		}
	}

	/**
	 * Called from the FRCCommunication constructor before the threads are
	 * created. Protocol-specific initialization happens here, for instance
	 * construction of queues.
	 */
	protected abstract void initProtocol();

	/**
	 * Called from a dedicated thread to receive and process data from the
	 * robot.
	 * 
	 * @return Deserialized data object, or null if unsuccessful.
	 */
	protected abstract RobotData receiveFromRobot();

	/**
	 * Called from a dedicated thread to receive and process data from the
	 * driver station.
	 * 
	 * @return Deserialized data object, or null if unsuccessful.
	 */
	protected abstract DSData receiveFromDS();

	/**
	 * Called from a dedicated thread to send data to the robot. This method
	 * must is also responsible for establishing the frequency of transmission
	 * by sleeping. It is important that this method sleep before returning
	 * because the dedicated thread will not do so.
	 * 
	 * @return Success of data sending.
	 */
	protected abstract boolean sendToRobot(DSData data);

	/**
	 * Called from a dedicated thread to send data to the driver station. This
	 * method must is also responsible for establishing the frequency of
	 * transmission by sleeping. It is important that this method sleep before
	 * returning because the dedicated thread will not do so.
	 * 
	 * @return Success of data sending.
	 */
	protected abstract boolean sendToDS(RobotData data);

	/**
	 * Worker thread that listens for data sent from the robot, deserializes it
	 * using the protocol-specific methods, and notifies all registered
	 * listeners. The thread will exit if the thread is interrupted. If the
	 * protocol-specific deserialization fails for a major reason (eg the socket
	 * is closed), it should disable receiving from robot, which will interrupt
	 * the thread.
	 * 
	 * @author Anirudh Bagde
	 */
	private class ReceiveFromRobotWorker implements Runnable {
		@Override
		public void run() {
			RobotData data;
			while(!Thread.interrupted()) {
				data=receiveFromRobot();
				for(RobotDataListener listener:robotListeners) {
					listener.receivedRobotData(data);
				}
			}
		}
	}

	/**
	 * Worker thread that listens for data sent from the driver station,
	 * deserializes it using the protocol-specific methods, and notifies all
	 * registered listeners. The thread will exit if the thread is interrupted.
	 * If the protocol-specific deserialization fails for a major reason (eg the
	 * socket is closed), it should disable receiving from driver station, which
	 * will interrupt the thread.
	 * 
	 * @author Anirudh Bagde
	 */
	private class ReceiveFromDSWorker implements Runnable {
		@Override
		public void run() {
			DSData data;
			while(!Thread.interrupted()) {
				data=receiveFromDS();
				for(DSDataListener listener:dsListeners) {
					listener.receivedDSData(data);
				}
			}
		}
	}

	/**
	 * Worker thread that runs the protocol-specific send method endlessly. The
	 * protocol itself will determine the frequency and method of transmission.
	 * The thread will exit if the thread is interrupted.
	 * 
	 * @author Anirudh Bagde
	 */
	private class SendToRobotWorker implements Runnable {
		@Override
		public void run() {
			while(!Thread.interrupted()&&!closed) {
				DSData data=dsSendData;
				sendToRobot(data);
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
			while(!Thread.interrupted()&&!closed) {
				RobotData data=robotSendData;
				sendToDS(data);
			}
		}
	}
}
