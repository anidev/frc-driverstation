package org.anidev.frcds.analyze.gui;

import java.io.File;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import org.anidev.frcds.analyze.AnalyzerProviderSelector;
import org.anidev.frcds.analyze.FileProvider;
import org.anidev.frcds.proto.ControlFlags;
import org.anidev.frcds.proto.torobot.*;

public class AnalyzerFrame extends JFrame {
	private AnalyzerProviderSelector selector;
	private JPanel contentPane;
	private JTextField fileNameField;
	private JLabel teamText;
	private final JPanel pktnumPanel=new JPanel();
	private final JPanel teamPanel=new JPanel();
	private final JPanel alliancePanel=new JPanel();
	private final JPanel positionPanel=new JPanel();
	private JCheckBox[] digFields=new JCheckBox[8];
	private JTextField[] algFields=new JTextField[8];
	private JProgressBar[][] joySticks=new JProgressBar[4][6];
	private JToggleButton[][] joyButtons=new JToggleButton[4][12];
	private JLabel pktnumText;
	private JLabel allianceText;
	private JLabel positionText;
	private JCheckBox resetBox;
	private JCheckBox notEStopBox;
	private JCheckBox enabledBox;
	private JCheckBox autoBox;
	private JCheckBox fmsAttachedBox;
	private JCheckBox resyncBox;
	private JCheckBox testBox;
	private JCheckBox versionsBox;
	private JButton streamStartButton;
	private JButton streamStopButton;
	private JButton fileSelectionButton;
	private JButton fileActivateButton;

	public AnalyzerFrame(AnalyzerProviderSelector _selector) {
		super("FRC Communication Analyzer");
		this.selector=_selector;
		selector.registerFrame(this);
		setSize(new Dimension(500,500));
		contentPane=new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane=new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JPanel sourcePanel=new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH,sourcePanel,0,
				SpringLayout.NORTH,contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST,sourcePanel,0,
				SpringLayout.WEST,contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH,sourcePanel,55,
				SpringLayout.NORTH,contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST,sourcePanel,0,
				SpringLayout.EAST,contentPane);
		contentPane.add(sourcePanel);
		FormLayout fl_sourcePanel=new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("left:min"),ColumnSpec.decode("5px"),
				ColumnSpec.decode("default:grow"),},new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,FormFactory.GLUE_ROWSPEC,});
		sourcePanel.setLayout(fl_sourcePanel);

		JRadioButton chooseStreamRadio=new JRadioButton("Network");
		sourcePanel.add(chooseStreamRadio,"1, 1");

		JPanel streamOptionsPanel=new JPanel();
		sourcePanel.add(streamOptionsPanel,"3, 1, fill, fill");
		streamOptionsPanel.setLayout(new GridLayout(1,2,0,0));

		streamStartButton=new JButton("Start");
		streamStartButton.setEnabled(false);
		streamOptionsPanel.add(streamStartButton);

		streamStopButton=new JButton("Stop");
		streamStopButton.setEnabled(false);
		streamOptionsPanel.add(streamStopButton);

		ActionListener streamListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button=(JButton)e.getSource();
				boolean starting="Start".equals(button.getActionCommand());
				streamStartButton.setEnabled(!starting);
				streamStopButton.setEnabled(starting);
				if(starting) {
					selector.startNetworkStream();
				} else {
					selector.stopNetworkStream();
				}
			}
		};
		streamStartButton.addActionListener(streamListener);
		streamStopButton.addActionListener(streamListener);

		JRadioButton chooseFileRadio=new JRadioButton("File");
		sourcePanel.add(chooseFileRadio,"1, 2");

		JPanel fileOptionsPanel=new JPanel();
		sourcePanel.add(fileOptionsPanel,"3, 2, fill, fill");
		fileOptionsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),FormFactory.MIN_COLSPEC,
				FormFactory.MIN_COLSPEC,},
				new RowSpec[] {FormFactory.DEFAULT_ROWSPEC,}));

		fileNameField=new JTextField();
		fileNameField.setEnabled(false);
		fileOptionsPanel.add(fileNameField,"1, 1, fill, fill");
		
		fileSelectionButton=new JButton("Browse");
		fileSelectionButton.setEnabled(false);
		fileOptionsPanel.add(fileSelectionButton,"2, 1, fill, fill");
		fileSelectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				File packetFile=FileProvider.askForFile(AnalyzerFrame.this);
				fileNameField.setText(packetFile.getAbsolutePath());
			}
		});

		fileActivateButton=new JButton("Go");
		fileActivateButton.setEnabled(false);
		fileOptionsPanel.add(fileActivateButton,"3, 1");
		fileActivateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					selector.startFile(new File(fileNameField.getText()));
				} catch(RuntimeException e) {
					e.printStackTrace();
					JOptionPane
							.showMessageDialog(AnalyzerFrame.this,
									"The selected file is invalid or corrupt.",
									"Invalid or Corrupt File",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		ButtonGroup sourceGroup=new ButtonGroup();
		sourceGroup.add(chooseStreamRadio);
		sourceGroup.add(chooseFileRadio);
		ItemListener sourceListener=new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JRadioButton radio=(JRadioButton)e.getSource();
				boolean network="Network".equals(radio.getActionCommand());
				streamStartButton.setEnabled(network);
				fileNameField.setEnabled(!network);
				fileSelectionButton.setEnabled(!network);
				fileActivateButton.setEnabled(!network);
			}
		};
		chooseStreamRadio.addItemListener(sourceListener);
		chooseFileRadio.addItemListener(sourceListener);

		JSeparator separator=new JSeparator();
		sl_contentPane.putConstraint(SpringLayout.NORTH,separator,5,
				SpringLayout.SOUTH,sourcePanel);
		sl_contentPane.putConstraint(SpringLayout.WEST,separator,0,
				SpringLayout.WEST,contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST,separator,0,
				SpringLayout.EAST,contentPane);
		contentPane.add(separator);

		JPanel miscStatusPanel=new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH,miscStatusPanel,0,
				SpringLayout.SOUTH,separator);
		sl_contentPane.putConstraint(SpringLayout.WEST,miscStatusPanel,0,
				SpringLayout.WEST,contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH,miscStatusPanel,25,
				SpringLayout.SOUTH,separator);
		sl_contentPane.putConstraint(SpringLayout.EAST,miscStatusPanel,0,
				SpringLayout.EAST,contentPane);
		contentPane.add(miscStatusPanel);
		miscStatusPanel.setLayout(new GridLayout(1,4,0,0));

		JLabel pktnumLabel=new JLabel("Packet #");
		FlowLayout flowLayout=(FlowLayout)pktnumPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		miscStatusPanel.add(pktnumPanel);
		pktnumPanel.add(pktnumLabel);

		pktnumText=new JLabel("0");
		pktnumPanel.add(pktnumText);
		pktnumText.setBorder(new EtchedBorder(EtchedBorder.LOWERED,null,null));

		JLabel teamLabel=new JLabel("Team");
		FlowLayout flowLayout_1=(FlowLayout)teamPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		miscStatusPanel.add(teamPanel);
		teamPanel.add(teamLabel);

		teamText=new JLabel();
		teamPanel.add(teamText);
		teamText.setText("0");
		teamText.setBorder(new EtchedBorder(EtchedBorder.LOWERED,null,null));

		JLabel allianceLabel=new JLabel("Alliance");
		FlowLayout flowLayout_2=(FlowLayout)alliancePanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		miscStatusPanel.add(alliancePanel);
		alliancePanel.add(allianceLabel);

		allianceText=new JLabel(" ");
		alliancePanel.add(allianceText);
		allianceText
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED,null,null));

		JLabel positionLabel=new JLabel("Position");
		FlowLayout flowLayout_3=(FlowLayout)positionPanel.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		miscStatusPanel.add(positionPanel);
		positionPanel.add(positionLabel);

		positionText=new JLabel(" ");
		positionPanel.add(positionText);
		positionText
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED,null,null));

		JPanel flagsPanel=new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH,flagsPanel,0,
				SpringLayout.SOUTH,miscStatusPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST,flagsPanel,0,
				SpringLayout.WEST,contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH,flagsPanel,60,
				SpringLayout.SOUTH,miscStatusPanel);
		sl_contentPane.putConstraint(SpringLayout.EAST,flagsPanel,0,
				SpringLayout.EAST,contentPane);
		contentPane.add(flagsPanel);

		JPanel digitalPanel=new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH,digitalPanel,5,
				SpringLayout.SOUTH,flagsPanel);
		sl_contentPane.putConstraint(SpringLayout.EAST,digitalPanel,80,
				SpringLayout.WEST,contentPane);
		digitalPanel.setBorder(new TitledBorder(null,"Digital",
				TitledBorder.LEFT,TitledBorder.TOP,null,null));
		sl_contentPane.putConstraint(SpringLayout.WEST,digitalPanel,0,
				SpringLayout.WEST,contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH,digitalPanel,0,
				SpringLayout.SOUTH,contentPane);
		contentPane.add(digitalPanel);

		JPanel analogPanel=new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH,analogPanel,5,
				SpringLayout.SOUTH,flagsPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST,analogPanel,-80,
				SpringLayout.EAST,contentPane);
		analogPanel.setBorder(new TitledBorder(null,"Analog",
				TitledBorder.LEADING,TitledBorder.TOP,null,null));
		sl_contentPane.putConstraint(SpringLayout.SOUTH,analogPanel,0,
				SpringLayout.SOUTH,contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST,analogPanel,0,
				SpringLayout.EAST,contentPane);
		contentPane.add(analogPanel);

		digitalPanel
				.setLayout(new FormLayout(new ColumnSpec[] {
						ColumnSpec.decode("right:min"),
						ColumnSpec.decode("default:grow"),},new RowSpec[] {
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),}));

		for(int i=0;i<8;i++) {
			String istr=new Integer(i+1).toString();
			JLabel digLabel=new JLabel(istr);
			JCheckBox digField=new JCheckBox();
			digitalPanel.add(digLabel,"1, "+istr+", fill, center");
			digitalPanel.add(digField,"2, "+istr+", fill, center");
			digFields[i]=digField;
		}

		analogPanel
				.setLayout(new FormLayout(new ColumnSpec[] {
						ColumnSpec.decode("right:min"),
						ColumnSpec.decode("default:grow"),},new RowSpec[] {
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),
						RowSpec.decode("default:grow"),}));

		for(int i=0;i<8;i++) {
			String istr=new Integer(i+1).toString();
			JLabel algLabel=new JLabel(istr);
			JTextField algField=new JTextField();
			algField.setEditable(false);
			algField.setText("0");
			analogPanel.add(algLabel,"1, "+istr+", fill, center");
			analogPanel.add(algField,"2, "+istr+", fill, center");
			algFields[i]=algField;
		}

		JPanel joyPanelsContainer=new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH,joyPanelsContainer,5,
				SpringLayout.SOUTH,flagsPanel);
		flagsPanel.setLayout(new GridLayout(2,4,0,0));

		resetBox=new JCheckBox("Reset");
		flagsPanel.add(resetBox);

		notEStopBox=new JCheckBox("Not E-Stop");
		flagsPanel.add(notEStopBox);

		enabledBox=new JCheckBox("Enabled");
		flagsPanel.add(enabledBox);

		autoBox=new JCheckBox("Autonomous");
		flagsPanel.add(autoBox);

		fmsAttachedBox=new JCheckBox("FMS Attached");
		flagsPanel.add(fmsAttachedBox);

		resyncBox=new JCheckBox("Resync");
		flagsPanel.add(resyncBox);

		testBox=new JCheckBox("Test Mode");
		flagsPanel.add(testBox);

		versionsBox=new JCheckBox("Check Versions");
		flagsPanel.add(versionsBox);
		sl_contentPane.putConstraint(SpringLayout.WEST,joyPanelsContainer,5,
				SpringLayout.EAST,digitalPanel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH,joyPanelsContainer,0,
				SpringLayout.SOUTH,contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST,joyPanelsContainer,-5,
				SpringLayout.WEST,analogPanel);
		contentPane.add(joyPanelsContainer);

		joyPanelsContainer.setLayout(new GridLayout(2,2,2,2));

		for(int i=0;i<4;i++) {
			JPanel joyPanel=new JPanel();
			joyPanelsContainer.add(joyPanel);
			joyPanel.setLayout(new BorderLayout(0,0));

			JPanel joySticksPanel=new JPanel();
			joyPanel.add(joySticksPanel,BorderLayout.CENTER);
			joySticksPanel.setLayout(new GridLayout(1,6,0,0));

			for(int j=0;j<6;j++) {
				JProgressBar joyStick=new JProgressBar();
				joyStick.setOrientation(SwingConstants.VERTICAL);
				joyStick.setMinimum(-128);
				joyStick.setMaximum(128);
				joySticksPanel.add(joyStick);
				joySticks[i][j]=joyStick;
			}

			JPanel joyButtonsPanel=new JPanel();
			joyPanel.add(joyButtonsPanel,BorderLayout.SOUTH);
			joyButtonsPanel.setLayout(new GridLayout(2,6,0,0));

			UIDefaults joyButtonUI=new UIDefaults();
			joyButtonUI.put("ToggleButton.contentMargins",new Insets(2,2,2,2));
			for(int j=0;j<12;j++) {
				String jstr=new Integer(j).toString();
				JToggleButton joyButton=new JToggleButton(jstr);
				joyButton.putClientProperty("Nimbus.Overrides",joyButtonUI);
				joyButtonsPanel.add(joyButton);
				joyButtons[i][j]=joyButton;
			}
		}
	}

	public void updateValues(FRCCommonControl control) {
		pktnumText.setText(new Integer(control.getPacketIndex()).toString());
		teamText.setText(new Integer(control.getTeamID()).toString());
		allianceText.setText(control.getAlliance().getName());
		if(Alliance.RED.equals(control.getAlliance())) {
			allianceText.setForeground(Color.RED);
		} else {
			allianceText.setForeground(Color.BLUE);
		}
		positionText.setText(new Character(control.getPosition()).toString());
		ControlFlags flags=control.getControlFlags();
		resetBox.setSelected(flags.isReset());
		notEStopBox.setSelected(flags.isNotEStop());
		enabledBox.setSelected(flags.isEnabled());
		autoBox.setSelected(flags.isAutonomous());
		fmsAttachedBox.setSelected(flags.isFmsAttached());
		resyncBox.setSelected(flags.isResync());
		testBox.setSelected(flags.isTest());
		versionsBox.setSelected(flags.isCheckVersions());
		DigitalInputs digitals=control.getDigitalInputs();
		for(int i=0;i<digitals.getNumInputs();i++) {
			digFields[i].setSelected(digitals.getInput(i));
		}
		AnalogInputs analogs=control.getAnalogInputs();
		for(int i=0;i<analogs.getNumInputs();i++) {
			algFields[i].setText(new Integer(analogs.getInput(i)).toString());
		}
		for(int i=0;i<control.getNumJoysticks();i++) {
			Joystick joystick=control.getJoystick(i);
			for(int a=0;a<joystick.getNumAxes();a++) {
				joySticks[i][a].setValue(joystick.getAxis(a));
			}
			for(int b=0;b<joystick.getNumButtons();b++) {
				joyButtons[i][b].setSelected(joystick.getButton(b));
			}
		}
	}
}
