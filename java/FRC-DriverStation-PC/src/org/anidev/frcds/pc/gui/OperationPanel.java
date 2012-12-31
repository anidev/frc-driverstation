package org.anidev.frcds.pc.gui;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ButtonGroup;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import org.anidev.frcds.common.types.TeamStation;

public class OperationPanel extends JPanel {
	private final ButtonGroup operationModeGroup=new ButtonGroup();
	private JLabel elapsedTimeValue;
	private JComboBox teamStationBox;
	private JProgressBar batteryBar;

	public OperationPanel() {
		setPreferredSize(new Dimension(600,240));
		setSize(new Dimension(600,240));
		FormLayout formLayout=new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("180px"),ColumnSpec.decode("5px"),
				ColumnSpec.decode("default:grow"),},new RowSpec[] {
				RowSpec.decode("0px:grow(0.45)"),RowSpec.decode("5px"),
				RowSpec.decode("0px:grow(0.55)"),});
		formLayout.setHonorsVisibility(false);
		setLayout(formLayout);

		JPanel topControlPanel=new JPanel();
		add(topControlPanel,"1, 1, fill, fill");
		topControlPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("20px"),ColumnSpec.decode("default:grow"),},
				new RowSpec[] {FormFactory.GLUE_ROWSPEC,
						FormFactory.GLUE_ROWSPEC,FormFactory.GLUE_ROWSPEC,}));

		JRadioButton teleopRadio=new JRadioButton("Teleoperated");
		teleopRadio.setActionCommand("teleop");
		teleopRadio.setSelected(true);
		operationModeGroup.add(teleopRadio);
		teleopRadio.setHorizontalAlignment(SwingConstants.LEFT);
		topControlPanel.add(teleopRadio,"2, 1");

		JRadioButton autonomousRadio=new JRadioButton("Autonomous");
		autonomousRadio.setActionCommand("autonomous");
		operationModeGroup.add(autonomousRadio);
		autonomousRadio.setHorizontalAlignment(SwingConstants.LEFT);
		topControlPanel.add(autonomousRadio,"2, 2");

		JRadioButton practiceRadio=new JRadioButton("Practice");
		practiceRadio.setActionCommand("practice");
		operationModeGroup.add(practiceRadio);
		practiceRadio.setHorizontalAlignment(SwingConstants.LEFT);
		topControlPanel.add(practiceRadio,"2, 3");

		JPanel lcdPanel=new JPanel();
		lcdPanel.setBorder(new TitledBorder(null,"User Messages",
				TitledBorder.LEADING,TitledBorder.TOP,null,null));
		add(lcdPanel,"3, 1, 1, 3, fill, fill");
		lcdPanel.setLayout(new BorderLayout(0,0));

		JTextArea lcdTextArea=new JTextArea();
		lcdTextArea.setEditable(false);
		lcdTextArea.setText("This is a test message.");
		lcdTextArea.setFont(new Font("Monospaced",Font.PLAIN,12));
		lcdPanel.add(lcdTextArea,BorderLayout.CENTER);

		JSeparator controlPanelSeparator=new JSeparator();
		add(controlPanelSeparator,"1, 2, default, center");

		JPanel bottomControlPanel=new JPanel();
		add(bottomControlPanel,"1, 3, fill, fill");
		bottomControlPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GLUE_COLSPEC,ColumnSpec.decode("5px"),
				FormFactory.GLUE_COLSPEC,},new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,FormFactory.GLUE_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));

		JLabel elapsedTimeLabel=new JLabel("Elapsed Time");
		elapsedTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomControlPanel.add(elapsedTimeLabel,"1, 1");

		elapsedTimeValue=new JLabel();
		elapsedTimeValue.setBorder(new EtchedBorder(EtchedBorder.LOWERED,null,
				null));
		elapsedTimeValue.setFont(new Font("Monospaced",Font.PLAIN,16));
		elapsedTimeValue.setHorizontalAlignment(SwingConstants.LEFT);
		setElapsedTime(0);
		bottomControlPanel.add(elapsedTimeValue,"3, 1");

		JLabel teamStationLabel=new JLabel("Team Station");
		teamStationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomControlPanel.add(teamStationLabel,"1, 2");

		teamStationBox=new JComboBox();
		teamStationBox.setModel(new DefaultComboBoxModel(TeamStation.values()));
		bottomControlPanel.add(teamStationBox,"3, 2, fill, default");

		JLabel batteryLabel=new JLabel("PC Battery");
		batteryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomControlPanel.add(batteryLabel,"1, 3");

		batteryBar=new JProgressBar();
		batteryBar.setString("Unavailable");
		batteryBar.setStringPainted(true);
		bottomControlPanel.add(batteryBar,"3, 3");
	}

	public void setElapsedTime(double elapsedTimeMs) {
		double seconds=elapsedTimeMs/1000;
		int minutes=(int)Math.floor(seconds/60);
		int roundSeconds=(int)Math.floor(seconds%60);
		int deciSeconds=(int)Math.floor(seconds*10)%10;
		String roundSecondsText=String.format("%02d",roundSeconds);
		String text=minutes+":"+roundSecondsText+"."+deciSeconds;
		elapsedTimeValue.setText(text);
	}
}
