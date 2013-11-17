package org.anidev.frcds.pc.gui.nc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DateFormat;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import org.anidev.frcds.pc.Utils;
import org.anidev.frcds.proto.DataDir;
import org.anidev.frcds.proto.nc.Netconsole;
import org.anidev.frcds.proto.nc.NetconsoleListener;
import org.anidev.frcds.proto.nc.NetconsoleMessage;
import sun.swing.DefaultLookup;

public class NetconsolePanel extends JPanel {
	private JTable consoleTable;
	private AbstractTableModel tableModel;
	private JTextField consoleSendText;
	private JScrollPane scrollPane;
	private Netconsole nc;
	private NetconsoleListener ncListener=null;
	private static final int ICON_COL_WIDTH=22;
	private static final int TIME_COL_WIDTH=90;

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
			};
			nc.addNetconsoleListener(ncListener);
		}

		setPreferredSize(new Dimension(600,240));
		setSize(new Dimension(600,240));
		setLayout(new BorderLayout(0,0));

		consoleTable=new JTable();
		consoleTable.setFillsViewportHeight(true);
		consoleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableModel=new NetconsoleTableModel();
		consoleTable.setModel(tableModel);
		consoleTable.setDefaultRenderer(String.class,
				new NetconsoleCellRenderer2());
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
		consoleSendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
	}

	public void fireMessagesAdded() {
		tableModel.fireTableDataChanged();
		// consoleTable.invalidate();
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

	/*
	 * Still a WIP; may not be used at all in the end.
	 */
	private class NetconsoleCellRenderer extends JTextArea implements
			TableCellRenderer {
		private final Border DEFAULT_NO_FOCUS_BORDER=new EmptyBorder(1,1,1,1);
		private Color unselectedFg;
		private Color unselectedBg;

		public NetconsoleCellRenderer() {
			super();
			setOpaque(true);
			setBorder(DEFAULT_NO_FOCUS_BORDER);
			setLineWrap(true);
			setWrapStyleWord(true);
		}

		public void setForeground(Color c) {
			super.setForeground(c);
			unselectedFg=c;
		}

		public void setBackground(Color c) {
			super.setBackground(c);
			unselectedBg=c;
		}

		private Border getNoFocusBorder() {
			Border border=DefaultLookup.getBorder(this,ui,
					"Table.cellNoFocusBorder");
			if(border!=null) {
				return border;
			} else {
				return DEFAULT_NO_FOCUS_BORDER;
			}
		}

		public void updateUI() {
			super.updateUI();
			setForeground(null);
			setBackground(null);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value,boolean isSelected,boolean hasFocus,int row,
				int column) {
			if(table==null) {
				return this;
			}

			if(isSelected) {
				super.setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				Color bg=unselectedBg;
				Color altColor=DefaultLookup.getColor(this,ui,
						"Table.alternateRowColor");
				if(bg==null) {
					if(altColor!=null&&row%2!=0) {
						bg=altColor;
					} else {
						bg=table.getBackground();
					}
				}
				super.setForeground((unselectedFg==null?table.getForeground()
						:unselectedFg));
				super.setBackground(bg);
			}

			setFont(table.getFont());

			if(hasFocus) {
				Border border=null;
				if(isSelected) {
					border=DefaultLookup.getBorder(this,ui,
							"Table.focusSelectedCellHighlightBorder");
				}
				if(border==null) {
					border=DefaultLookup.getBorder(this,ui,
							"Table.focusCellHighlightBorder");
				}
				setBorder(border);
			} else {
				setBorder(getNoFocusBorder());
			}

			setValue(value);

			int fontHeight=getFontMetrics(getFont()).getHeight();
			int lines=countLines(column);
			int height=fontHeight*lines;
			if(height>table.getRowHeight(row)) {
				table.setRowHeight(row,height);
			}
			System.out.println(height);

			return this;
		}

		private int countLines(int column) {
			AttributedString text=new AttributedString(getText());
			FontRenderContext frc=getFontMetrics(getFont())
					.getFontRenderContext();
			AttributedCharacterIterator charIt=text.getIterator();
			LineBreakMeasurer lineMeasurer=new LineBreakMeasurer(charIt,frc);
			float formatWidth=consoleTable.getColumnModel().getColumn(column)
					.getWidth()-4;
			lineMeasurer.setPosition(charIt.getBeginIndex());
			int lines=0;
			while(lineMeasurer.getPosition()<charIt.getEndIndex()) {
				lineMeasurer.nextLayout(formatWidth);
				lines++;
			}
			return lines;
		}

		/*
		 * The following methods are overridden as a performance measure.
		 */

		public boolean isOpaque() {
			Color back=getBackground();
			Component p=getParent();
			if(p!=null) {
				p=p.getParent();
			}
			boolean colorMatch=(back!=null)&&(p!=null)
					&&back.equals(p.getBackground())&&p.isOpaque();
			return !colorMatch&&super.isOpaque();
		}

		public void invalidate() {
		}

		public void validate() {
		}

		public void revalidate() {
		}

		public void repaint(long tm,int x,int y,int width,int height) {
		}

		public void repaint(Rectangle r) {
		}

		public void repaint() {
		}

		protected void setValue(Object value) {
			setText((value==null)?"":value.toString());
		}
	}

	private class NetconsoleCellRenderer2 extends DefaultTableCellRenderer {
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
