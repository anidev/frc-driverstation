package org.anidev.frcds.pc.nc;

import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import org.anidev.frcds.proto.nc.Netconsole;
import org.anidev.utils.Utils;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

public class ControlBar extends JToolBar {
	private NetconsolePanel ncPanel;
	private Netconsole nc;
	private JToggleButton listButton;
	private JToggleButton textButton;
	private JToggleButton pauseButton;

	public ControlBar() {
		setBorder(null);
		setRollover(true);
		setSize(new Dimension(22,200));
		setOrientation(SwingConstants.VERTICAL);

		JButton clearButton=new JButton(Utils.getIcon("delete.png"));
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nc.clearMessages();
			}
		});
		add(clearButton);

		pauseButton=new JToggleButton(Utils.getIcon("pause.png"));
		pauseButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				ncPanel.setPaused(pauseButton.isSelected());
			}
		});
		add(pauseButton);

		addSeparator();

		listButton=new JToggleButton(Utils.getIcon("list.png"));
		listButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				ncPanel.setListMode(listButton.isSelected());
			}
		});
		add(listButton);

		textButton=new JToggleButton(Utils.getIcon("text.png"));
		add(textButton);
		
		ButtonGroup modeGroup=new ButtonGroup();
		modeGroup.add(listButton);
		modeGroup.add(textButton);
	}

	protected void setPanel(NetconsolePanel ncPanel) {
		this.ncPanel=ncPanel;
	}

	protected void setNetconsole(Netconsole nc) {
		this.nc=nc;
	}

	protected JToggleButton getListButton() {
		return listButton;
	}

	protected JToggleButton getTextButton() {
		return textButton;
	}
	protected JToggleButton getPauseButton() {
		return pauseButton;
	}
}
