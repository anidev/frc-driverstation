package org.anidev.frcds.pc.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import org.anidev.utils.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class StatusPanel extends JPanel {
	private JLabel voltsValueLabel;
	private DecimalFormat voltsFormat;
	private JPanel communicationStatus;

	// private Indicator robotCodeStatus;

	public StatusPanel() {
		setSize(new Dimension(170,240));
		setLayout(new GridLayout(2,1,0,0));

		JPanel robotStatusPanel=new JPanel();
		robotStatusPanel.setBorder(new TitledBorder(null,"Robot Status",
				TitledBorder.LEADING,TitledBorder.TOP,null,null));
		add(robotStatusPanel);
		robotStatusPanel.setLayout(new GridLayout(0,1,0,0));

		JPanel voltsPanel=new JPanel();
		robotStatusPanel.add(voltsPanel);
		voltsPanel.setLayout(new GridLayout(1,2,0,0));

		JLabel voltsLabel=new JLabel("Volts:");
		voltsLabel.setFont(new Font("Arial",Font.BOLD,16));
		voltsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		voltsPanel.add(voltsLabel);

		voltsValueLabel=new JLabel();
		setBatteryVolts(-1.0);
		voltsValueLabel.setFont(new Font("Arial",Font.BOLD,16));
		voltsValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		voltsPanel.add(voltsValueLabel);

		voltsFormat=new DecimalFormat("##.##");

		communicationStatus=new StatusIndicator("Communication");
		robotStatusPanel.add(communicationStatus);

		/*		JPanel robotCodeStatusPanel=new JPanel();
				robotStatusPanel.add(robotCodeStatusPanel);
				robotCodeStatusPanel.setLayout(new FormLayout(new ColumnSpec[] {
						ColumnSpec.decode("10px"),ColumnSpec.decode("16px"),
						ColumnSpec.decode("4px"),ColumnSpec.decode("default:grow"),},
						new RowSpec[] {RowSpec.decode("default:grow"),}));

				robotCodeStatusIcon=new JLabel("");
				robotCodeStatusIcon.setPreferredSize(new Dimension(16,16));
				setRobotCodeState(false);
				robotCodeStatusPanel.add(robotCodeStatusIcon,"2, 1, left, fill");

				JLabel robotCodeStatusLabel=new JLabel("Robot Code");
				robotCodeStatusPanel.add(robotCodeStatusLabel,"4, 1, fill, fill");*/

		JPanel operationStatusPanel=new JPanel();
		operationStatusPanel.setBorder(new TitledBorder(null,
				"Operation Status",TitledBorder.LEADING,TitledBorder.TOP,null,
				null));
		add(operationStatusPanel);
		operationStatusPanel.setLayout(new BorderLayout(0,0));

		JLabel operationStatusLabel=new JLabel(
				"<html><div style=\"text-align:center;\">No Robot Communication</div></html>");
		operationStatusLabel.setFont(new Font("Arial",Font.BOLD,18));
		operationStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		operationStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		operationStatusPanel.add(operationStatusLabel,BorderLayout.CENTER);
	}

	public void setBatteryVolts(double volts) {
		if(volts<0) {
			voltsValueLabel.setText("--.--");
		} else {
			voltsValueLabel.setText(voltsFormat.format(volts));
		}
	}

	public void setCommunicationState(boolean on) {
		// communicationStatus.setOn(on);
	}
}

class StatusIndicator extends JPanel {
	private JLabel icon;
	private JLabel label;
	private boolean on;
	private static final ImageIcon goodIcon;
	private static final ImageIcon badIcon;
	static {
		goodIcon=Utils.getIcon("status-good.png");
		badIcon=Utils.getIcon("status-bad.png");
	}

	public StatusIndicator(String text) {
		System.out.println(badIcon);
		setLayout(new FormLayout(new ColumnSpec[] {ColumnSpec.decode("10px"),
				ColumnSpec.decode("16px"),ColumnSpec.decode("4px"),
				ColumnSpec.decode("default:grow"),},new RowSpec[] {RowSpec
				.decode("default:grow"),}));
		icon=new JLabel("");
		icon.setPreferredSize(new Dimension(16,16));
		add(icon,"2, 1, left, fill");
		label=new JLabel(text);
		add(label,"4, 1, fill, fill");
		setOn(false);
	}

	public String getText() {
		return label.getText();
	}

	public void setText(String text) {
		label.setText(text);
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean newOn) {
		on=newOn;
		icon.setIcon((on?goodIcon:badIcon));
	}
}
