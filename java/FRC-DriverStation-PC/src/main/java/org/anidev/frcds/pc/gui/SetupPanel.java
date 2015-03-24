package org.anidev.frcds.pc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.activation.ActivationDataFlavor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import org.anidev.frcds.pc.input.InputDevice;
import org.anidev.frcds.pc.input.InputEnvironment;
import org.anidev.frcds.pc.input.InputListener;
import org.anidev.frcds.pc.input.Type;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * JPanel for the setup tab
 */
public class SetupPanel extends JPanel {
	public static final String ROBORIO_PROTOCOL="2015 roboRIO protocol";
	public static final String CRIO_PROTOCOL="2009-2014 cRIO protocol";
	private static final DataFlavor INPUT_FLAVOR=new ActivationDataFlavor(
			Integer.class,"Device Index");
	private InputEnvironment env;
	private JTable inputTable;
	private AbstractTableModel inputTableModel;
	private JComboBox<String> protocolMenu;

	/**
	 * Setup the setup
	 * 
	 * @param _env
	 *            the InputEnvironment with the input devices
	 */
	public SetupPanel(InputEnvironment _env) {
		this.env=_env;

		setPreferredSize(new Dimension(600,240));
		setSize(new Dimension(600,240));
		setLayout(new FormLayout(new ColumnSpec[] {ColumnSpec
				.decode("min:grow")},new RowSpec[] {
				RowSpec.decode("default:grow"),RowSpec.decode("min:grow")}));

		JPanel inputPanel=new JPanel();
		inputPanel.setBorder(new TitledBorder(null,"Input",
				TitledBorder.LEADING,TitledBorder.TOP,null,null));
		add(inputPanel,"1, 1, fill, fill");
		inputPanel.setLayout(new BorderLayout(0,0));

		inputTable=new JTable();
		inputTable.setDragEnabled(true);
		inputTable.setDropMode(DropMode.ON);
		inputTable.setFillsViewportHeight(true);
		inputTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		inputTableModel=new InputTableModel();
		inputTable.setModel(inputTableModel);
		inputTable.setTransferHandler(new InputTransferHandler());

		JScrollPane inputScrollPane=new JScrollPane(inputTable);
		inputPanel.add(inputScrollPane);

		env.addInputListener(new InputListener() {
			/* (non-Javadoc)
			 * @see org.anidev.frcds.pc.input.InputListener#deviceAdded(org.anidev.frcds.pc.input.InputDevice)
			 */
			@Override
			public void deviceAdded(InputDevice dev) {
				inputTableModel.fireTableDataChanged();
			}

			/* (non-Javadoc)
			 * @see org.anidev.frcds.pc.input.InputListener#deviceRemoved(org.anidev.frcds.pc.input.InputDevice)
			 */
			@Override
			public void deviceRemoved(InputDevice dev) {
				inputTableModel.fireTableDataChanged();
			}
		});

		// drop down menu to switch between protocols
		protocolMenu=new JComboBox<String>();
		protocolMenu.setModel(new DefaultComboBoxModel<String>(new String[] {
				CRIO_PROTOCOL,ROBORIO_PROTOCOL}));
		protocolMenu.addActionListener(new ActionListener() {
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent event) {
				if(protocolMenu.getSelectedItem().equals(CRIO_PROTOCOL)) {
					// TODO put code to switch to the cRIO protocol here
				} else if(protocolMenu.getSelectedItem().equals(
						ROBORIO_PROTOCOL)) {
					// TODO switch to the new roboRIO protocol here
				}
			}
		});
		add(protocolMenu,"1, 2, default, default");
	}

	/**
	 * @return the communications protocol that was selected
	 */
	public String getProtocol() {
		return protocolMenu.getSelectedItem().toString();
	}

	/**
	 * table for the input devices
	 */
	private class InputTableModel extends AbstractTableModel {
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return 4;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
			case 0:
				return "Device";
			case 1:
				return "Type";
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
			case 0:
				return String.class;
			case 1:
				return Type.class;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex,int columnIndex) {
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex,int columnIndex) {
			InputDevice dev=env.getDevice(rowIndex);
			if(dev==null) {
				return null;
			}
			switch(columnIndex) {
			case 0:
				return dev.getController().getName();
			case 1:
				return dev.getType();
			}
			return null;
		}
	}

	/**
	 * TransferHandler for moving the input devices
	 */
	private class InputTransferHandler extends TransferHandler {
		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
		 */
		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
		 */
		@Override
		protected Transferable createTransferable(JComponent c) {
			if(c!=inputTable) {
				return null;
			}
			return new InputTransferable(inputTable.getSelectedRow());
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
		 */
		@Override
		public boolean canImport(TransferSupport support) {
			boolean b=support.getComponent()==inputTable;
			b&=support.isDrop();
			b&=support.isDataFlavorSupported(INPUT_FLAVOR);
			return b;
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
		 */
		@Override
		public boolean importData(TransferSupport support) {
			if(!canImport(support)) {
				return false;
			}
			Transferable trans=support.getTransferable();
			int fromIndex=0;
			try {
				fromIndex=(Integer)trans.getTransferData(INPUT_FLAVOR);
			} catch(IOException|UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			}
			JTable.DropLocation location=(JTable.DropLocation)support
					.getDropLocation();
			int toIndex=location.getRow();
			env.swapDevices(fromIndex,toIndex);
			inputTableModel.fireTableDataChanged();
			return true;
		}
	}

	/**
	 * Transferable for input devices
	 */
	private class InputTransferable implements Transferable {
		public int index;

		/**
		 * @param index
		 *            the input device's index
		 */
		public InputTransferable(int index) {
			this.index=index;
		}

		/* (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
		 */
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {INPUT_FLAVOR};
		}

		/* (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
		 */
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return false;
		}

		/* (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
		 */
		@Override
		public Object getTransferData(DataFlavor flavor) {
			if(INPUT_FLAVOR.equals(flavor)) {
				return index;
			}
			return null;
		}
	}
}
