package org.anidev.frcds.pc.nc;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import org.anidev.frcds.proto.DataDir;
import org.anidev.frcds.proto.nc.Netconsole;
import org.anidev.frcds.proto.nc.NetconsoleListener;
import org.anidev.frcds.proto.nc.NetconsoleMessage;
import org.anidev.utils.Utils;
import java.awt.CardLayout;
import javax.swing.JTextArea;

public class NetconsolePanel extends JPanel {
	private int lastCount=0;
	private boolean listMode=false;
	private volatile int autoScrolling=0;
	private CardLayout messagesLayout;
	private ControlBar controlBar;
	private JPanel messagesPanel;
	private JTextArea consoleText;
	private JTable consoleTable;
	private AbstractTableModel tableModel;
	private JTextField consoleSendText;
	private JScrollPane tableScrollPane;
	private Netconsole nc;
	private NetconsoleListener ncListener=null;
	private static final int ICON_COL_WIDTH=22;
	private static final int TIME_COL_WIDTH=90;
	private JScrollPane textScrollPane;

	public NetconsolePanel(Netconsole _nc) {
		this.nc=_nc;
		if(nc!=null) {
			ncListener=new NetconsoleListener() {
				@Override
				public void receivedData(NetconsoleMessage msg) {
					fireMessagesAdded();
				}

				@Override
				public void dataSent(NetconsoleMessage msg) {
					fireMessagesAdded();
				}

				@Override
				public void messagesCleared() {
					tableModel.fireTableDataChanged();
				}

				@Override
				public void pauseChanged(boolean paused) {
					controlBar.getPauseButton().setSelected(paused);
				}
			};
			nc.addNetconsoleListener(ncListener);
		}

		setPreferredSize(new Dimension(600,240));
		setSize(new Dimension(600,240));
		setLayout(new BorderLayout(2,0));

		messagesPanel=new JPanel();
		add(messagesPanel,BorderLayout.CENTER);
		messagesLayout=new CardLayout(0,0);
		messagesPanel.setLayout(messagesLayout);

		consoleTable=new JTable();
		consoleTable.setFillsViewportHeight(true);
		consoleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableModel=new NetconsoleTableModel();
		consoleTable.setModel(tableModel);
		consoleTable.setDefaultRenderer(String.class,
				new NetconsoleCellRenderer());
		TableColumn iconColumn=consoleTable.getColumnModel().getColumn(0);
		TableColumn timeColumn=consoleTable.getColumnModel().getColumn(2);
		iconColumn.setMinWidth(ICON_COL_WIDTH);
		iconColumn.setMaxWidth(ICON_COL_WIDTH);
		timeColumn.setMinWidth(TIME_COL_WIDTH);
		timeColumn.setMaxWidth(TIME_COL_WIDTH);
		consoleTable.setFont(Utils.makeMonoFont(consoleTable.getFont()));

		tableScrollPane=new JScrollPane(consoleTable);
		messagesPanel.add(tableScrollPane,"list");

		consoleText=new JTextArea();
		consoleText.setEditable(false);
		consoleText.setFont(Utils.makeMonoFont(consoleText.getFont()));
		textScrollPane=new JScrollPane(consoleText);
		messagesPanel.add(textScrollPane,"text");

		JPanel consoleSendPanel=new JPanel();
		consoleSendPanel.setBorder(new EmptyBorder(2,2,2,2));
		add(consoleSendPanel,BorderLayout.SOUTH);
		consoleSendPanel.setLayout(new BorderLayout(2,0));

		consoleSendText=new JTextField();
		consoleSendPanel.add(consoleSendText,BorderLayout.CENTER);
		consoleSendText.setColumns(10);

		consoleSendText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()!=KeyEvent.VK_ENTER) {
					return;
				}
				sendMessage();
			}
		});

		JButton consoleSendButton=new JButton("Send");
		consoleSendPanel.add(consoleSendButton,BorderLayout.EAST);

		controlBar=new ControlBar();
		controlBar.setPanel(this);
		controlBar.setNetconsole(nc);
		add(controlBar,BorderLayout.WEST);
		consoleSendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		setListMode(true);
	}

	public void fireMessagesAdded() {
		int count=nc.getNetconsoleMessages().size();
		int diff=count-lastCount;
		if(diff==0) {
			return;
		}
		boolean autoScroll=false;
		JScrollBar scrollBar=getScrollBar(listMode);
		if(isScrollBarMaximum(scrollBar)) {
			autoScroll=true;
		}
		tableModel.fireTableRowsInserted(lastCount,lastCount+diff);
		List<NetconsoleMessage> list=nc.getNetconsoleMessages();
		StringBuilder text=new StringBuilder();
		synchronized(list) {
			for(int i=lastCount;i<lastCount+diff;i++) {
				text.append(list.get(i).getMessage());
			}
		}
		consoleText.append(text.toString());
		consoleTable.invalidate();
		if(autoScroll||autoScrolling>0) {
			if(autoScrolling<0) {
				autoScrolling=0;
			}
			autoScrolling++;
			final boolean cachedListMode=listMode;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JScrollBar scrollBar=getScrollBar(cachedListMode);
					scrollBar.setValue(scrollBar.getMaximum());
					autoScrolling--;
				}
			});
		}
		lastCount=count;
	}

	public void firePanelDestroyed() {
		if(nc!=null) {
			nc.removeNetconsoleListener(ncListener);
		}
	}

	private void sendMessage() {
		String text=consoleSendText.getText();
		nc.sendData(text);
		consoleSendText.setText("");
	}

	public boolean isListMode() {
		return listMode;
	}

	public void setListMode(boolean listMode) {
		if(this.listMode==listMode) {
			return;
		}
		this.listMode=listMode;
		if(listMode) {
			messagesLayout.show(messagesPanel,"list");
		} else {
			messagesLayout.show(messagesPanel,"text");
		}
		controlBar.getListButton().setSelected(listMode);
		controlBar.getTextButton().setSelected(!listMode);
		JScrollBar scrollBar=getScrollBar(listMode);
		scrollBar.setValue(scrollBar.getMaximum());
	}

	protected void setPaused(boolean paused) {
		nc.setPaused(paused);
	}

	private JScrollBar getScrollBar(boolean mode) {
		return (mode?tableScrollPane:textScrollPane).getVerticalScrollBar();
	}

	private static boolean isScrollBarMaximum(JScrollBar scrollBar) {
		return scrollBar.getValue()+scrollBar.getVisibleAmount()>=scrollBar
				.getMaximum();
	}

	private class NetconsoleTableModel extends AbstractTableModel {
		private Icon sentIcon;
		private Icon receivedIcon;
		private DateFormat dateFormat;

		public NetconsoleTableModel() {
			sentIcon=Utils.getIcon("arrow-up.png");
			receivedIcon=Utils.getIcon("arrow-down.png");
			dateFormat=DateFormat.getTimeInstance();
		}

		@Override
		public int getRowCount() {
			if(nc==null) {
				return 0;
			}
			return nc.getNetconsoleMessages().size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0:
				return "";
			case 1:
				return "Message";
			case 2:
				return "Time";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
			case 0:
				return Icon.class;
			case 1:
			case 2:
				return String.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex,int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex,int columnIndex) {
			if(nc==null) {
				return null;
			}
			NetconsoleMessage msg=nc.getNetconsoleMessage(rowIndex);
			switch(columnIndex) {
			case 0:
				if(msg.getDirection()==DataDir.TODS) {
					return receivedIcon;
				} else {
					return sentIcon;
				}
			case 1:
				return msg.getMessage();
			case 2:
				return dateFormat.format(msg.getDate().getTime());
			}
			return null;
		}
	}

	private class NetconsoleCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value,boolean isSelected,boolean hasFocus,int row,
				int column) {
			JLabel label=(JLabel)super.getTableCellRendererComponent(table,
					value,isSelected,hasFocus,row,column);
			String tooltip=label.getText();
			label.setToolTipText(tooltip);
			return label;
		}
	}
}
