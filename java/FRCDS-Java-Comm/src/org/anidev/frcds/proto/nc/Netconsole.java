package org.anidev.frcds.proto.nc;

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
import org.anidev.frcds.proto.DataDir;

public class Netconsole {
	public static final int RECV_PORT=6666;
	public static final int SEND_PORT=6668;
	private static final InetAddress broadcastAddress;
	private volatile boolean closed=false;
	private int queueTimeout=40;
	private List<NetconsoleListener> listeners=Collections
			.synchronizedList(new ArrayList<NetconsoleListener>());
	private List<NetconsoleMessage> netconsoleMessages=Collections
			.synchronizedList(new ArrayList<NetconsoleMessage>());
	private DatagramSocket sendDataSocket;
	private DatagramSocket receiveDataSocket;
	private Thread sendDataThread;
	private Thread receiveDataThread;
	private BlockingQueue<String> sendDataQueue;

	static {
		InetAddress dummy=null;
		try {
			dummy=InetAddress.getByName("255.255.255.255");
		} catch(UnknownHostException e) {
			dummy=null;
		}
		broadcastAddress=dummy;
	}

	public Netconsole() {
		sendDataQueue=new LinkedBlockingQueue<String>();
		initSockets();
		initThreads();
	}

	public List<NetconsoleMessage> getNetconsoleMessages() {
		return netconsoleMessages;
	}

	public NetconsoleMessage getNetconsoleMessage(int index) {
		return netconsoleMessages.get(index);
	}
	
	public void clearMessages() {
		netconsoleMessages.clear();
		synchronized(listeners) {
			for(NetconsoleListener listener:listeners) {
				listener.messagesCleared();
			}
		}
	}

	public void sendData(String data) {
		checkClosed();
		try {
			sendDataQueue.offer(data,queueTimeout,TimeUnit.MILLISECONDS);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public int getQueueTimeout() {
		return queueTimeout;
	}

	public void setQueueTimeout(int queueTimeout) {
		checkClosed();
		this.queueTimeout=queueTimeout;
	}

	public void addNetconsoleListener(NetconsoleListener listener) {
		listeners.add(listener);
	}

	public void removeNetconsoleListener(NetconsoleListener listener) {
		listeners.remove(listener);
	}

	public void close() {
		checkClosed();
		if(sendDataSocket!=null) {
			sendDataSocket.close();
			sendDataSocket=null;
		}
		if(sendDataThread!=null) {
			sendDataThread.interrupt();
			sendDataThread=null;
		}
		if(receiveDataSocket!=null) {
			receiveDataSocket.close();
			receiveDataSocket=null;
		}
		if(receiveDataThread!=null) {
			receiveDataThread.interrupt();
			receiveDataThread=null;
		}
		listeners.clear();
		listeners=null;
		netconsoleMessages.clear();
		netconsoleMessages=null;
		sendDataQueue.clear();
		sendDataQueue=null;
		closed=true;
	}

	private void checkClosed() {
		if(closed) {
			throw new IllegalStateException("Communication has been closed");
		}
	}

	/**
	 * If fail to open send socket, send operations will be silently consumed
	 * rather than closing the whole thing down.
	 */
	private void initSockets() {
		try {
			sendDataSocket=new DatagramSocket();
			sendDataSocket.setReuseAddress(true);
			sendDataSocket.setBroadcast(true);
		} catch(SocketException e) {
			e.printStackTrace();
			System.err.println("Failed to create netconsole send socket.");
			sendDataSocket=null;
		}
		try {
			receiveDataSocket=new DatagramSocket(RECV_PORT);
			receiveDataSocket.setReuseAddress(true);
		} catch(SocketException e) {
			e.printStackTrace();
			System.err.println("Failed to create netconsole receive socket.");
			sendDataSocket=null;
		}
	}

	private void initThreads() {
		sendDataThread=new Thread(new SendDataWorker(),"Netconsole Send");
		sendDataThread.start();
		receiveDataThread=new Thread(new ReceiveDataWorker(),"Netconsole Receive");
		receiveDataThread.start();
	}

	private class SendDataWorker implements Runnable {
		@Override
		public void run() {
			while(sendDataSocket!=null&&!Thread.interrupted()&&!closed) {
				String sendData=null;
				try {
					sendData=sendDataQueue.take();
				} catch(InterruptedException e) {
					break;
				}
				int len=sendData.length();
				if(len==0||sendData.charAt(len-1)!='\n') {
					sendData+='\n';
				}
				byte[] sendBytes=sendData.getBytes();
				DatagramPacket packet=new DatagramPacket(sendBytes,
						sendBytes.length);
				packet.setAddress(broadcastAddress);
				packet.setPort(SEND_PORT);
				try {
					sendDataSocket.send(packet);
				} catch(IOException e) {
					e.printStackTrace();
				}
				NetconsoleMessage msg=new NetconsoleMessage(DataDir.TOROBOT,
						sendData);
				netconsoleMessages.add(msg);
				synchronized(listeners) {
					for(NetconsoleListener listener:listeners) {
						listener.dataSent(msg);
					}
				}
			}
		}
	}

	private class ReceiveDataWorker implements Runnable {
		@Override
		public void run() {
			while(receiveDataSocket!=null&&!Thread.interrupted()&&!closed) {
				int length=1024;
				byte[] buffer=new byte[length];
				DatagramPacket packet=new DatagramPacket(buffer,length);
				try {
					receiveDataSocket.receive(packet);
				} catch(SocketException e) {
					break;
				} catch(IOException e) {
					e.printStackTrace();
				}
				String data=new String(buffer,0,packet.getLength());
				NetconsoleMessage msg=new NetconsoleMessage(DataDir.TODS,data);
				netconsoleMessages.add(msg);
				synchronized(listeners) {
					for(NetconsoleListener listener:listeners) {
						listener.receivedData(msg);
					}
				}
			}
		}
	}
}
