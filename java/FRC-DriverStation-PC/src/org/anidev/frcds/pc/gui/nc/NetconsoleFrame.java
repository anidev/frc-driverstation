package org.anidev.frcds.pc.gui.nc;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.anidev.frcds.proto.nc.Netconsole;

public class NetconsoleFrame extends JFrame {
	private final Netconsole nc;
	private final NetconsolePanel netconsolePanel;

	public NetconsoleFrame(Netconsole _nc) {
		super("FRC Netconsole");
		this.nc=_nc;
		setSize(new Dimension(630,300));
		netconsolePanel=new NetconsolePanel(nc);
		setContentPane(netconsolePanel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				netconsolePanel.firePanelDestroyed();
			}
		});
	}
}
