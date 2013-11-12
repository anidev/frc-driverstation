package org.anidev.frcds.pc.gui.nc;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.anidev.frcds.pc.PCDriverStation;

public class NetconsoleFrame extends JFrame {
	private final PCDriverStation ds;
	private final NetconsolePanel netconsolePanel;
	public NetconsoleFrame(PCDriverStation _ds) {
		super("FRC Netconsole");
		this.ds=_ds;
		setResizable(false);
		setSize(new Dimension(630,300));
		netconsolePanel=new NetconsolePanel(ds);
		setContentPane(netconsolePanel);
		if(ds!=null) {
			ds.addNetconsolePanel(netconsolePanel);
		}
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(ds!=null) {
					ds.removeNetconsolePanel(netconsolePanel);
				}
			}
		});
	}
}
