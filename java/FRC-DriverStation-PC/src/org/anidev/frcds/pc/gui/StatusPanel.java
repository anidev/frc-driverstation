package org.anidev.frcds.pc.gui;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

public class StatusPanel extends JPanel {
	private JLabel voltsValueLabel;
	private DecimalFormat voltsFormat;
	public StatusPanel() {
		setSize(new Dimension(170, 240));
		setLayout(new GridLayout(2, 1, 0, 0));

		JPanel robotStatusPanel=new JPanel();
		robotStatusPanel.setBorder(new TitledBorder(null,"Robot Status",
				TitledBorder.LEADING,TitledBorder.TOP,null,null));
		add(robotStatusPanel);
		robotStatusPanel.setLayout(new GridLayout(3,1,0,0));

		JPanel voltsPanel=new JPanel();
		robotStatusPanel.add(voltsPanel);
		voltsPanel.setLayout(new GridLayout(1,2,0,0));

		JLabel voltsLabel=new JLabel("Volts:");
		voltsLabel.setFont(new Font("Arial", Font.BOLD, 16));
		voltsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		voltsPanel.add(voltsLabel);

		voltsValueLabel=new JLabel();
		setBatteryVolts(-1.0);
		voltsValueLabel.setFont(new Font("Arial", Font.BOLD, 16));
		voltsValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		voltsPanel.add(voltsValueLabel);
		
		voltsFormat=new DecimalFormat("##.##");

		JPanel communicationStatusPanel=new JPanel();
		robotStatusPanel.add(communicationStatusPanel);
		communicationStatusPanel.setLayout(new BorderLayout(3, 0));

		JCheckBox communicationStatusIcon=new JCheckBox("");
		communicationStatusIcon.setMargin(new Insets(0, 5, 0, 0));
		communicationStatusIcon.setEnabled(false);
		communicationStatusPanel.add(communicationStatusIcon, BorderLayout.WEST);
		
		JLabel communicationStatusLabel = new JLabel("Communication");
		communicationStatusPanel.add(communicationStatusLabel, BorderLayout.CENTER);

		JPanel robotCodeStatusPanel=new JPanel();
		robotStatusPanel.add(robotCodeStatusPanel);

		JPanel operationStatusPanel=new JPanel();
		operationStatusPanel.setBorder(new TitledBorder(null,
				"Operation Status",TitledBorder.LEADING,TitledBorder.TOP,null,
				null));
		add(operationStatusPanel);
		operationStatusPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel operationStatusLabel = new JLabel("<html><div style=\"text-align:center;\">No Robot Communication</div></html>");
		operationStatusLabel.setFont(new Font("Arial", Font.BOLD, 18));
		operationStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		operationStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		operationStatusPanel.add(operationStatusLabel, BorderLayout.CENTER);
	}
	
	public void setBatteryVolts(double volts) {
		if(volts<0) {
			voltsValueLabel.setText("--.--");
		} else {
			voltsValueLabel.setText(voltsFormat.format(volts));
		}
	}
}
