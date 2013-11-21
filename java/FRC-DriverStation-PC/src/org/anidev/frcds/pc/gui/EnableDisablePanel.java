package org.anidev.frcds.pc.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.utils.Utils;

public class EnableDisablePanel extends JPanel {
	private static final double SELECTED_ALPHA=0.8;
	private ButtonGroup buttonGroup;
	private EnhancedToggleButton enableButton;
	private EnhancedToggleButton disableButton;

	public EnableDisablePanel() {
		setSize(new Dimension(70,250));
		setLayout(new GridLayout(2,1,0,0));

		buttonGroup=new ButtonGroup();

		UIDefaults uiOverride=new UIDefaults();
		uiOverride.put("Button.contentMargins",new Insets(0,0,0,0));
		enableButton=new EnhancedToggleButton("Enable",Color.GREEN.darker(),
				SELECTED_ALPHA,EnhancedToggleButton.BorderCollapse.BOTTOM);
		enableButton.setActionCommand("enable");
		enableButton.putClientProperty("Nimbus.Overrides",uiOverride);
		add(enableButton);
		buttonGroup.add(enableButton);

		disableButton=new EnhancedToggleButton("Disable",Color.RED,
				SELECTED_ALPHA,EnhancedToggleButton.BorderCollapse.TOP);
		disableButton.setSelected(true);
		disableButton.setActionCommand("disable");
		disableButton.putClientProperty("Nimbus.Overrides",uiOverride);
		add(disableButton);

		buttonGroup.add(disableButton);

		ItemListener listener=new EnableDisableListener();
		enableButton.addItemListener(listener);
		disableButton.addItemListener(listener);
	}

	public boolean isEnabledSelected() {
		JToggleButton button=(JToggleButton)Utils
				.getSelectedButton(buttonGroup);
		return button.getActionCommand().equals("enable");
	}

	public void setEnableAllowed(boolean allowed) {
		if(!allowed) {
			disableButton.setSelected(true);
		}
		enableButton.setEnabled(allowed);
	}

	private class EnableDisableListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.DESELECTED) {
				return;
			}
			JToggleButton button=(JToggleButton)e.getSource();
			boolean enabled=button.getActionCommand().equals("enable");
			if(enabled) {
				disableButton.requestFocusInWindow();
			} else {
				enableButton.requestFocusInWindow();
			}
			DriverStationMain.getDS().setEnabled(enabled);
		}
	}
}
