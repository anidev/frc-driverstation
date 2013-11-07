package org.anidev.frcds.pc.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.anidev.frcds.pc.DriverStationMain;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Cursor;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class DriverStationFrame extends JFrame {
	private JPanel contentPane;
	private EnableDisablePanel enableDisablePanel;
	private StatusPanel statusPanel;
	private OperationPanel operationPanel;
	private TeamIDPanel teamIDPanel;
	private NetconsolePanel netconsolePanel;

	public DriverStationFrame() {
		super("FRC Driver Station");
		setResizable(false);
		setSize(new Dimension(870,300));
		contentPane=new JPanel();
		contentPane
				.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("70px"),
				ColumnSpec.decode("170px"),
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("40px"),
				RowSpec.decode("default:grow"),}));

		enableDisablePanel=new EnableDisablePanel();
		contentPane.add(enableDisablePanel,"1, 1, 1, 2, fill, fill");
		
		teamIDPanel = new TeamIDPanel();
		contentPane.add(teamIDPanel, "2, 1, fill, fill");
		
		statusPanel=new StatusPanel();
		contentPane.add(statusPanel,"2, 2, fill, fill");

		JTabbedPane tabbedPane=new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane,"3, 1, 1, 2, fill, fill");

		operationPanel=new OperationPanel();
		tabbedPane.addTab("Operation",null,operationPanel,"Robot operation");
		
		netconsolePanel = new NetconsolePanel();
		tabbedPane.addTab("Netconsole", null, netconsolePanel, "Robot Console");
		tabbedPane.setEnabledAt(1, true);
		DriverStationMain.getDS().addNetconsolePanel(netconsolePanel);
		DetachableTab netconsoleTab=new DetachableTab("Netconsole");
		tabbedPane.setTabComponentAt(1,netconsoleTab);
		
		setTeamID(0);
	}

	public void setElapsedTime(double elapsedTime) {
		operationPanel.setElapsedTime(elapsedTime);
	}
	
	public void setTeamID(int teamID) {
		if(teamID<=0) {
			setEnableAllowed(false);
		} else {
			setEnableAllowed(true);
		}
		operationPanel.setTeamID(teamID);
	}
	
	public void setBatteryPercent(double percent) {
		operationPanel.setBatteryPercent(percent);
	}
	
	private void setEnableAllowed(boolean allowed) {
		enableDisablePanel.setEnableAllowed(allowed);
	}
	
	private class DetachableTab extends JPanel {
		private String title;
		private JButton detachButton;
		
		public DetachableTab(String title) {
			super(new BorderLayout());
			setOpaque(false);
			this.title=title;
			JLabel label=new JLabel(title);
			add(label,BorderLayout.CENTER);
			detachButton=new JButton();
		}
	}
}
