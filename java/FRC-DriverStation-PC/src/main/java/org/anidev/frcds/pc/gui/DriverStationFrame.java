package org.anidev.frcds.pc.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.frcds.pc.PCDriverStation;
import org.anidev.frcds.pc.nc.NetconsoleFrame;
import org.anidev.frcds.pc.nc.NetconsolePanel;
import org.anidev.frcds.proto.tods.FRCRobotControl;
import org.anidev.utils.Utils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.BackingStoreException;
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
	private final PCDriverStation ds=DriverStationMain.getDS();
	private static final String OPERATION_TAB="Operation";
	private static final String NETCONSOLE_TAB="Netconsole";
	private static final String OPERATION_TIP="Robot Operation";
	private static final String NETCONSOLE_TIP="Netconsole";
	private static final String TAB_ORDER_PREF="tab_order";
	private static final String SELECTED_TAB_PREF="selected_tab";
	private static final String MAIN_NC_LIST="main_nc_list";
	private static final String TEAMID_PREF="teamid";
	private static final String DEF_TAB_LIST;
	private static final String DEF_SELECTED_TAB=OPERATION_TAB;
	static {
		DEF_TAB_LIST=initDefTabList();
	}

	public DriverStationFrame() {
		super("FRC Driver Station");
		setSize(new Dimension(870,300));
		setMinimumSize(getSize());
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
				getPrefs().put(TAB_ORDER_PREF,tabList);
			}

			@Override
			public void tabDetached(int index,MouseEvent e) {
				if(tabbedPane.getTitleAt(index).equals(NETCONSOLE_TAB)) {
					JFrame frame=new NetconsoleFrame(DriverStationMain
							.getNetconsole());
					frame.setLocation(e.getLocationOnScreen());
					frame.setVisible(true);
				}
			}
		});

		operationPanel=new OperationPanel();

		netconsolePanel=new NetconsolePanel(DriverStationMain.getNetconsole());
		netconsolePanel.setListMode(getPrefs().getBoolean(MAIN_NC_LIST,true));

		restoreTabOrder(operationPanel,netconsolePanel);
		int operationTabIndex=tabbedPane.indexOfTab(OPERATION_TAB);
		tabbedPane.setTabDetachable(operationTabIndex,false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int selectedTabIndex=tabbedPane.getSelectedIndex();
				String selectedTab=tabbedPane.getTitleAt(selectedTabIndex);
				Preferences prefs=getPrefs();
				prefs.put(SELECTED_TAB_PREF,selectedTab);
				prefs.putInt(TEAMID_PREF,ds.getTeamID());
				prefs.putBoolean(MAIN_NC_LIST,netconsolePanel.isListMode());
				try {
					prefs.flush();
				} catch(BackingStoreException ex) {
					ex.printStackTrace();
				}
			}
		});

		ds.setTeamID(getPrefs().getInt(TEAMID_PREF,0));
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
		teamIDPanel.setTeamID(teamID);
		operationPanel.setTeamID(teamID);
	}

	public void setBatteryPercent(double percent) {
		operationPanel.setBatteryPercent(percent);
	}

	public void displayControlData(FRCRobotControl control) {
		statusPanel.setBatteryVolts(control.getBatteryVolts());
	}

	private void setEnableAllowed(boolean allowed) {
		enableDisablePanel.setEnableAllowed(allowed);
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
		String selectedTab=getPrefs()
				.get(SELECTED_TAB_PREF,DEF_SELECTED_TAB);
		int selectedTabIndex=tabbedPane.indexOfTab(selectedTab);
		if(selectedTabIndex>=0) {
			tabbedPane.setSelectedIndex(selectedTabIndex);
		}
	}
	
	private static Preferences getPrefs() {
		return Utils.getPrefs(DriverStationFrame.class);
	}

	private static String initDefTabList() {
		StringBuilder defTabListBuilder=new StringBuilder();
		defTabListBuilder.append(OPERATION_TAB);
		defTabListBuilder.append(",");
		defTabListBuilder.append(NETCONSOLE_TAB);
		return defTabListBuilder.toString();
	}

	private static String[] getTabOrderPref() {
		Preferences prefs=getPrefs();
		String tabOrderList=prefs.get(TAB_ORDER_PREF,DEF_TAB_LIST);
		return tabOrderList.split(",");
	}
}
