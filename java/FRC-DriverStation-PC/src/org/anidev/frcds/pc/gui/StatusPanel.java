package org.anidev.frcds.pc.gui;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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
import java.awt.Insets;
import java.text.DecimalFormat;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

public class StatusPanel extends JPanel {
	private JLabel voltsValueLabel;
	private DecimalFormat voltsFormat;
	private JLabel communicationStatusIcon;
	private ImageIcon goodIcon;
	private ImageIcon badIcon;

	public StatusPanel() {
		goodIcon=Utils.getIcon("status-good.png");
		badIcon=Utils.getIcon("status-bad.png");

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

		JPanel communicationStatusPanel=new JPanel();
		robotStatusPanel.add(communicationStatusPanel);
		communicationStatusPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("16px"),
				ColumnSpec.decode("4px"),
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("default:grow"),
				RowSpec.decode("default:grow"),}));

		communicationStatusIcon=new JLabel("");
		communicationStatusIcon.setPreferredSize(new Dimension(16,16));
		setCommunicationState(false);
		communicationStatusPanel
				.add(communicationStatusIcon,"2, 1, left, fill");

		JLabel communicationStatusLabel=new JLabel("Communication");
		communicationStatusPanel.add(communicationStatusLabel,
				"4, 1, fill, fill");

		JPanel robotCodeStatusPanel=new JPanel();
		robotStatusPanel.add(robotCodeStatusPanel);

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

	public void setCommunicationState(boolean good) {
		communicationStatusIcon.setIcon((good?goodIcon:badIcon));
	}
}
