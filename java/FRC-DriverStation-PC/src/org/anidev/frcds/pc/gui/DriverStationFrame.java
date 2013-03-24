package org.anidev.frcds.pc.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
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
		contentPane.add(tabbedPane,"3, 1, 1, 2, fill, top");

		operationPanel=new OperationPanel();
		tabbedPane.addTab("Operation",null,operationPanel,"Robot operation");
		
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
	}
	
	public void setBatteryPercent(double percent) {
		operationPanel.setBatteryPercent(percent);
	}
	
	private void setEnableAllowed(boolean allowed) {
		enableDisablePanel.setEnableAllowed(allowed);
	}
}
