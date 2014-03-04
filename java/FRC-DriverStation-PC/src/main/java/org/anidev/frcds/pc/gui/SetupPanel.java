package org.anidev.frcds.pc.gui;

import java.awt.Dimension;
import javax.activation.ActivationDataFlavor;
import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JScrollPane;
import org.anidev.frcds.pc.input.InputDevice;
import org.anidev.frcds.pc.input.InputEnvironment;
import org.anidev.frcds.pc.input.InputListener;
import org.anidev.frcds.pc.input.Type;
import javax.swing.DropMode;

public class SetupPanel extends JPanel {
	private static final DataFlavor INPUT_FLAVOR=new ActivationDataFlavor(
			Integer.class,"Device Index");
	private InputEnvironment env;
	private JTable inputTable;
	private AbstractTableModel inputTableModel;

	public SetupPanel(InputEnvironment _env) {
		this.env=_env;

		setPreferredSize(new Dimension(600,240));
		setSize(new Dimension(600,240));
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("min:grow"),
				ColumnSpec.decode("default:grow"),},new RowSpec[] {RowSpec
				.decode("default:grow"),}));

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
		// inputTable.setModel(new DefaultTableModel(new Object[][] { {"1","2"},
		// {"3","4"},},new String[] {"New column","New column"}));
		inputTable.setModel(inputTableModel);
		inputTable.setTransferHandler(new InputTransferHandler());

		JScrollPane inputScrollPane=new JScrollPane(inputTable);
		inputPanel.add(inputScrollPane);

		env.addInputListener(new InputListener() {
			@Override
			public void deviceAdded(InputDevice dev) {
				inputTableModel.fireTableDataChanged();
			}

			@Override
			public void deviceRemoved(InputDevice dev) {
				inputTableModel.fireTableDataChanged();
			}
		});
	}

	private class InputTableModel extends AbstractTableModel {
		@Override
		public int getRowCount() {
			return 4;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

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

		@Override
		public boolean isCellEditable(int rowIndex,int columnIndex) {
			return false;
		}

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

	private class InputTransferHandler extends TransferHandler {
		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			if(c!=inputTable) {
				return null;
			}
			return new InputTransferable(inputTable.getSelectedRow());
		}

		@Override
		public boolean canImport(TransferSupport support) {
			boolean b=support.getComponent()==inputTable;
			b&=support.isDrop();
			b&=support.isDataFlavorSupported(INPUT_FLAVOR);
			return b;
		}

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

	private class InputTransferable implements Transferable {
		public int index;

		public InputTransferable(int index) {
			this.index=index;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {INPUT_FLAVOR};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return false;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) {
			if(INPUT_FLAVOR.equals(flavor)) {
				return index;
			}
			return null;
		}
	}
}
