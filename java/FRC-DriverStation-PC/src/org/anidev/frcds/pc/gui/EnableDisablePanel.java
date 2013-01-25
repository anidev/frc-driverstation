package org.anidev.frcds.pc.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.frcds.pc.Utils;

public class EnableDisablePanel extends JPanel {
	private static final double SELECTED_ALPHA=0.8;
	private ButtonGroup buttonGroup;
	private ColoredToggleButton enableButton;
	private ColoredToggleButton disableButton;
	public EnableDisablePanel() {
		setSize(new Dimension(70, 250));
		setLayout(new GridLayout(2, 1, 0, 0));
		
		buttonGroup=new ButtonGroup();
		
		enableButton = new ColoredToggleButton("Enable",Color.GREEN.darker());
		enableButton.setActionCommand("enable");
		add(enableButton);
		buttonGroup.add(enableButton);
		
		disableButton = new ColoredToggleButton("Disable",Color.RED);
		disableButton.setSelected(true);
		disableButton.setActionCommand("disable");
		add(disableButton);
		buttonGroup.add(disableButton);
		
		ItemListener listener=new EnableDisableButtonListener();
		enableButton.addItemListener(listener);
		disableButton.addItemListener(listener);
	}
	public boolean isEnabledSelected() {
		JToggleButton button=(JToggleButton)Utils.getSelectedButton(buttonGroup);
		return button.getActionCommand().equals("enable");
	}
	public void setEnableAllowed(boolean allowed) {
		if(!allowed) {
			disableButton.setSelected(true);
		}
		enableButton.setEnabled(allowed);
	}
	private class ColoredToggleButton extends JToggleButton {
		private Color origColor;
		private Color selectedColor;
		public ColoredToggleButton(String text,Color overlayColor) {
			super(text);
			this.origColor=getBackground();
			selectedColor=Utils.calcAlpha(SELECTED_ALPHA,overlayColor,origColor);
			setFont(new Font("Arial",Font.BOLD,12));
			addItemListener(new ColoredToggleButtonListener());
			changeState(isSelected());
		}
		public void setSelected(boolean selected) {
			super.setSelected(selected);
			changeState(selected);
		}
		public void changeState(boolean selected) {
			if(selected) {
				setBackground(selectedColor);
				setForeground(null);
			} else {
				setBackground(null);
				setForeground(selectedColor);
			}
		}
	}
	private class ColoredToggleButtonListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(!(e.getSource() instanceof ColoredToggleButton)) {
				return;
			}
			ColoredToggleButton button=(ColoredToggleButton)e.getSource();
			button.changeState(e.getStateChange()==ItemEvent.SELECTED);
		}
	}
	private class EnableDisableButtonListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			JToggleButton button=(JToggleButton)e.getSource();
			boolean enabled=button.getActionCommand().equals("enable");
			DriverStationMain.getDS().setEnabled(enabled);
		}
	}
}
