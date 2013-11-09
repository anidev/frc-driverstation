package org.anidev.frcds.pc.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.frcds.pc.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class DriverStationFrame extends JFrame {
	private JPanel contentPane;
	private DraggableTabbedPane tabbedPane;
	private EnableDisablePanel enableDisablePanel;
	private StatusPanel statusPanel;
	private TeamIDPanel teamIDPanel;
	private OperationPanel operationPanel;
	private NetconsolePanel netconsolePanel;
	private static final String OPERATION_TAB="Operation";
	private static final String NETCONSOLE_TAB="Netconsole";
	private static final String OPERATION_TIP="Robot Operation";
	private static final String NETCONSOLE_TIP="Netconsole";
	private static final String TAB_ORDER_PREF="tab_order";
	private static final String SELECTED_TAB_PREF="selected_tab";
	private static final String DEF_TAB_LIST;
	private static final String DEF_SELECTED_TAB=OPERATION_TAB;
	static {
		DEF_TAB_LIST=initDefTabList();
	}

	public DriverStationFrame() {
		super("FRC Driver Station");
		setResizable(false);
		setSize(new Dimension(870,300));
		contentPane=new JPanel();
		contentPane
				.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("70px"),ColumnSpec.decode("170px"),
				ColumnSpec.decode("default:grow"),},new RowSpec[] {
				RowSpec.decode("40px"),RowSpec.decode("default:grow"),}));

		enableDisablePanel=new EnableDisablePanel();
		contentPane.add(enableDisablePanel,"1, 1, 1, 2, fill, fill");

		teamIDPanel=new TeamIDPanel();
		contentPane.add(teamIDPanel,"2, 1, fill, fill");

		statusPanel=new StatusPanel();
		contentPane.add(statusPanel,"2, 2, fill, fill");

		tabbedPane=new DraggableTabbedPane();
		contentPane.add(tabbedPane,"3, 1, 1, 2, fill, fill");
		tabbedPane.addTabDragListener(new DraggableTabbedPane.Listener() {
			@Override
			public void tabMoved(int oldIndex,int newIndex,MouseEvent e) {
				StringBuilder tabListBuilder=new StringBuilder();
				int numTabs=tabbedPane.getTabCount();
				for(int i=0;i<numTabs;i++) {
					tabListBuilder.append(tabbedPane.getTitleAt(i));
					if(i<numTabs-1) {
						tabListBuilder.append(",");
					}
				}
				String tabList=tabListBuilder.toString();
				Utils.getPrefs().put(TAB_ORDER_PREF,tabList);
			}

			@Override
			public void tabDetached(int index,MouseEvent e) {
				if(tabbedPane.getTitleAt(index).equals(NETCONSOLE_TAB)) {
					JFrame frame=new JFrame();
					NetconsolePanel panel=new NetconsolePanel();
					frame.setContentPane(panel);
					DriverStationMain.getDS().addNetconsolePanel(panel);
					frame.pack();
					frame.setVisible(true);
				}
			}
		});

		operationPanel=new OperationPanel();

		netconsolePanel=new NetconsolePanel();
		DriverStationMain.getDS().addNetconsolePanel(netconsolePanel);
		DetachableTab netconsoleTab=new DetachableTab(NETCONSOLE_TAB);

		restoreTabOrder(operationPanel,netconsolePanel);
		int operationTabIndex=tabbedPane.indexOfTab(OPERATION_TAB);
		tabbedPane.setTabDetachable(operationTabIndex,false);
		int netconsoleTabIndex=tabbedPane.indexOfTab(NETCONSOLE_TAB);
		tabbedPane.setTabComponentAt(netconsoleTabIndex,netconsoleTab);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int selectedTabIndex=tabbedPane.getSelectedIndex();
				String selectedTab=tabbedPane.getTitleAt(selectedTabIndex);
				Utils.getPrefs().put(SELECTED_TAB_PREF,selectedTab);
			}
		});

		setTeamID(0);
	}

	public void setElapsedTime(double elapsedTime) {
		operationPanel.setElapsedTime(elapsedTime);
	}

	public void setTeamID(int teamID) {
		if(teamID<=0) {
			setEnableAllowed(false);
		} else {
			setEnableAllowed(true);
		}
		operationPanel.setTeamID(teamID);
	}

	public void setBatteryPercent(double percent) {
		operationPanel.setBatteryPercent(percent);
	}

	private void setEnableAllowed(boolean allowed) {
		enableDisablePanel.setEnableAllowed(allowed);
	}

	private static String[] getTabOrderPref() {
		Preferences prefs=Utils.getPrefs();
		String tabOrderList=prefs.get(TAB_ORDER_PREF,DEF_TAB_LIST);
		return tabOrderList.split(",");
	}

	private void restoreTabOrder(Component operationTab,Component netconsoleTab) {
		String[] tabOrder=getTabOrderPref();
		for(String tab:tabOrder) {
			Component toAdd=null;
			String tooltip=tab;
			switch(tab) {
			case OPERATION_TAB:
				toAdd=operationTab;
				tooltip=OPERATION_TIP;
				break;
			case NETCONSOLE_TAB:
				toAdd=netconsoleTab;
				tooltip=NETCONSOLE_TIP;
				break;
			}
			if(toAdd!=null) {
				tabbedPane.addTab(tab,null,toAdd,tooltip);
			}
		}
		String selectedTab=Utils.getPrefs().get(SELECTED_TAB_PREF,
				DEF_SELECTED_TAB);
		int selectedTabIndex=tabbedPane.indexOfTab(selectedTab);
		if(selectedTabIndex>=0) {
			tabbedPane.setSelectedIndex(selectedTabIndex);
		}
	}

	private static String initDefTabList() {
		StringBuilder defTabListBuilder=new StringBuilder();
		defTabListBuilder.append(OPERATION_TAB);
		defTabListBuilder.append(",");
		defTabListBuilder.append(NETCONSOLE_TAB);
		return defTabListBuilder.toString();
	}

	private class DetachableTab extends JPanel {
		private String title;
		private JButton detachButton;

		public DetachableTab(String title) {
			super(new BorderLayout());
			setOpaque(false);
			this.title=title;
			JLabel label=new JLabel(title);
			add(label,BorderLayout.CENTER);
			detachButton=new JButton();
		}
	}
}
