package org.anidev.frcds.receiveds;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import org.anidev.frcds.proto.CommData;
import org.anidev.frcds.proto.FRCCommunication;
import org.anidev.frcds.proto.FRCCommunicationListener;
import org.anidev.frcds.proto.tods.FRCRobotControl;
import org.anidev.frcds.proto.torobot.AnalogInputs;
import org.anidev.frcds.proto.torobot.DigitalInputs;
import org.anidev.frcds.proto.torobot.FRCCommonControl;
import org.anidev.frcds.proto.torobot.Joystick;

public class Main {
	private static JProgressBar[] joyBars=new JProgressBar[6];
	private static JToggleButton[] joyButtons=new JToggleButton[12];
	public static void main(String[] args) {
		JFrame frame=new JFrame("Joysticks");
		frame.setSize(240,480);
		frame.getContentPane().setLayout(new BorderLayout());
		JPanel barsPanel=new JPanel(new GridLayout(1,6));
		frame.add(barsPanel,BorderLayout.CENTER);
		for(int i=0;i<joyBars.length;i++) {
			JProgressBar bar=new JProgressBar(SwingConstants.VERTICAL,-127,127);
			joyBars[i]=bar;
			barsPanel.add(bar);
		}
		JPanel buttonsPanel=new JPanel(new GridLayout(2,6));
		frame.add(buttonsPanel,BorderLayout.SOUTH);
		for(int i=0;i<joyButtons.length;i++) {
			JToggleButton button=new JToggleButton(new Integer(i+1).toString());
			joyButtons[i]=button;
			button.setSize(20,20);
			buttonsPanel.add(button);
		}
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		final FRCCommunication frcComm=new FRCCommunication(false,true);
		frcComm.addDSDataListener(new FRCCommunicationListener() {
			@Override
			public void receivedData(CommData data) {
				if(!(data instanceof FRCCommonControl)) {
					return;
				}
				FRCCommonControl controlData=(FRCCommonControl)data;
				/*				System.out.print("Digital inputs: ");
								DigitalInputs dInputs=controlData.getDigitalInputs();
								for(int i=0;i<dInputs.getNumInputs()-1;i++) {
									System.out.print(dInputs.getInput(i));
									System.out.print(",");
								}
								System.out.println(dInputs.getInput(dInputs.getNumInputs()-1));*/
				/*				System.out.println("Analog inputs: ");
								AnalogInputs aInputs=controlData.getAnalogInputs();
								for(int i=0;i<aInputs.getNumInputs()-1;i++) {
									System.out.print(aInputs.getInput(i));
									System.out.print(",");
								}
								System.out.println(aInputs.getInput(aInputs.getNumInputs()-1));*/
				Joystick joystick=controlData.getJoystick(0);
				for(int i=0;i<joystick.getNumAxes();i++) {
					joyBars[i].setValue(-joystick.getAxis(i));
				}
				for(int i=0;i<joystick.getNumButtons();i++) {
					joyButtons[i].setSelected(joystick.getButton(i));
				}
				FRCRobotControl robotData=new FRCRobotControl();
				robotData.setBatteryVolts(12);
				robotData.setBatteryMV(0);
				robotData.setTeamID(612);
				robotData.getControlFlags().setEnabled(false);
				frcComm.sendToDS(robotData);
			}
		});
	}
}
