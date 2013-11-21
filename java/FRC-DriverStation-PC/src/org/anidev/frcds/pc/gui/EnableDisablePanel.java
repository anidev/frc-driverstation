package org.anidev.frcds.pc.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.utils.Utils;

public class EnableDisablePanel extends JPanel {
	private static final double SELECTED_ALPHA=0.8;
	private ButtonGroup buttonGroup;
	private ColoredToggleButton enableButton;
	private ColoredToggleButton disableButton;

	public EnableDisablePanel() {
		setSize(new Dimension(70,250));
		setLayout(new GridLayout(2,1,0,0));

		buttonGroup=new ButtonGroup();

		UIDefaults uiOverride=new UIDefaults();
		uiOverride.put("Button.contentMargins",new Insets(0,0,0,0));
		enableButton=new ColoredToggleButton("Enable",Color.GREEN.darker(),
				ColoredToggleButton.BorderCollapse.BOTTOM);
		enableButton.setActionCommand("enable");
		enableButton.putClientProperty("Nimbus.Overrides",uiOverride);
		add(enableButton);
		buttonGroup.add(enableButton);

		disableButton=new ColoredToggleButton("Disable",Color.RED,
				ColoredToggleButton.BorderCollapse.TOP);
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

	private static class ColoredToggleButton extends JToggleButton implements
			ItemListener {
		private Color origColor;
		private Color selectedColor;
		private BorderCollapse borderCollapse;
		private Paint borderColor=null;
		private static final int BORDER_SIZE=4;
		private static final int BORDER_PADDING=2;
		private static final int BORDER_TRANSLATE=BORDER_SIZE+BORDER_PADDING;

		public ColoredToggleButton(String text,Color overlayColor,
				BorderCollapse border) {
			super(text);
			this.borderCollapse=border;
			this.origColor=getBackground();
			selectedColor=Utils
					.calcAlpha(SELECTED_ALPHA,overlayColor,origColor);
			setFont(new Font("Arial",Font.BOLD,12));
			addItemListener(this);
			changeState(isSelected());
			Object borderPref=Utils.getNimbusPref("nimbusBorder",this);
			if(borderPref!=null&&borderPref instanceof Paint) {
				borderColor=(Paint)borderPref;
			}
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

		@Override
		public void paintComponent(Graphics g) {
			Rectangle bounds=g.getClipBounds();
			switch(borderCollapse) {
			case BOTTOM:
				g.translate(0,BORDER_TRANSLATE);
				break;
			case TOP:
				g.translate(0,-BORDER_TRANSLATE);
				break;
			default:
			}
			super.paintComponent(g);
			g.setClip(bounds);
			if(borderColor!=null&&g instanceof Graphics2D) {
				Graphics2D g2d=(Graphics2D)g;
				g2d.setPaint(borderColor);
				int minX=bounds.x+BORDER_SIZE;
				int maxX=bounds.x+bounds.width-BORDER_SIZE;
				int minY=bounds.y+BORDER_SIZE;
				int maxY=bounds.y+bounds.height-BORDER_SIZE;
				switch(borderCollapse) {
				case BOTTOM:
					g.drawLine(minX,maxY-BORDER_PADDING,maxX,maxY
							-BORDER_PADDING);
					break;
				case TOP:
					g.drawLine(minX,minY+BORDER_PADDING,maxX,minY
							+BORDER_PADDING);
					break;
				case NONE:
				}
			}
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(!(e.getSource() instanceof ColoredToggleButton)) {
				return;
			}
			ColoredToggleButton button=(ColoredToggleButton)e.getSource();
			button.changeState(e.getStateChange()==ItemEvent.SELECTED);
		}

		public static enum BorderCollapse {
			NONE,
			BOTTOM,
			TOP;
		}
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
