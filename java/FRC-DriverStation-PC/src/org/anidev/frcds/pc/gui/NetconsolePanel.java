package org.anidev.frcds.pc.gui;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.text.DateFormat;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import org.anidev.frcds.common.types.NetconsoleMessage;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.frcds.pc.PCDriverStation;
import org.anidev.frcds.pc.Utils;

public class NetconsolePanel extends JPanel {
	private JTable consoleTable;
	private AbstractTableModel tableModel;
	private JTextField consoleSendText;
	private JScrollPane scrollPane;
	private PCDriverStation ds;
	private static final int ICON_COL_WIDTH=22;
	private static final int TIME_COL_WIDTH=90;

	public NetconsolePanel() {
		this.ds=DriverStationMain.getDS();

		setPreferredSize(new Dimension(600,240));
		setSize(new Dimension(600,240));
		setLayout(new BorderLayout(0,0));

		consoleTable=new JTable();
		consoleTable.setFillsViewportHeight(true);
		consoleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableModel=new NetconsoleTableModel();
		consoleTable.setModel(tableModel);
		TableColumn iconColumn=consoleTable.getColumnModel().getColumn(0);
		iconColumn.setMinWidth(ICON_COL_WIDTH);
		iconColumn.setMaxWidth(ICON_COL_WIDTH);
		TableColumn timeColumn=consoleTable.getColumnModel().getColumn(2);
		timeColumn.setMinWidth(TIME_COL_WIDTH);
		timeColumn.setMaxWidth(TIME_COL_WIDTH);
		Font oldFont=consoleTable.getFont();
		Font monoFont=new Font(Font.MONOSPACED,oldFont.getStyle(),oldFont
				.getSize());
		consoleTable.setFont(monoFont);

		scrollPane=new JScrollPane(consoleTable);
		add(scrollPane,BorderLayout.CENTER);

		JPanel consoleSendPanel=new JPanel();
		consoleSendPanel.setBorder(new EmptyBorder(2,2,2,2));
		add(consoleSendPanel,BorderLayout.SOUTH);
		consoleSendPanel.setLayout(new BorderLayout(2,0));

		consoleSendText=new JTextField();
		consoleSendPanel.add(consoleSendText,BorderLayout.CENTER);
		consoleSendText.setColumns(10);

		JButton consoleSendButton=new JButton("Send");
		consoleSendPanel.add(consoleSendButton,BorderLayout.EAST);
	}

	public void fireMessagesAdded() {
		tableModel.fireTableDataChanged();
		// consoleTable.invalidate();
	}

	private class NetconsoleTableModel extends AbstractTableModel {
		private ImageIcon sentIcon;
		private ImageIcon receivedIcon;
		private DateFormat dateFormat;

		public NetconsoleTableModel() {
			sentIcon=Utils.getIcon("arrow-up.png");
			receivedIcon=Utils.getIcon("arrow-down.png");
			dateFormat=DateFormat.getTimeInstance();
		}

		@Override
		public int getRowCount() {
			if(ds==null) {
				return 0;
			}
			return ds.getNetconsoleMessages().size();
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
				return ImageIcon.class;
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
			if(ds==null) {
				return null;
			}
			NetconsoleMessage msg=ds.getNetconsoleMessages().get(rowIndex);
			switch(columnIndex) {
			case 0:
				if(msg.getType()==NetconsoleMessage.Type.TODS) {
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
}
