package org.anidev.frcds.pc.gui;

import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.JFormattedTextField;
import org.anidev.frcds.pc.DriverStationMain;
import org.anidev.utils.Utils;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * JPanel for the team number
 */
public class TeamIDPanel extends JPanel {
	private JFormattedTextField teamIDField;
	/**
	 * Create a new panel with a text field for the team number
	 */
	public TeamIDPanel() {
		setSize(new Dimension(170,40));
		GridBagLayout gridBagLayout=new GridBagLayout();
		gridBagLayout.columnWidths=new int[] {0,0};
		gridBagLayout.rowHeights=new int[] {0};
		gridBagLayout.columnWeights=new double[] {0.0,0.0};
		gridBagLayout.rowWeights=new double[] {0.0};
		setLayout(gridBagLayout);

		JLabel teamIDLabel=new JLabel("Team ID");
		teamIDLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints gbc_teamIDLabel=new GridBagConstraints();
		gbc_teamIDLabel.gridx=0;
		gbc_teamIDLabel.insets=new Insets(0,0,0,5);
		gbc_teamIDLabel.anchor=GridBagConstraints.WEST;
		gbc_teamIDLabel.gridy=0;
		add(teamIDLabel,gbc_teamIDLabel);

		teamIDField=new JFormattedTextField(Utils.getTeamIDFormat());
		teamIDField.setColumns(4);
		GridBagConstraints gbc_teamIDField=new GridBagConstraints();
		gbc_teamIDField.weightx=1.0;
		gbc_teamIDField.fill=GridBagConstraints.HORIZONTAL;
		gbc_teamIDField.gridx=1;
		gbc_teamIDField.gridy=0;
		
		teamIDField.addPropertyChangeListener("value",new PropertyChangeListener() {
			/* (non-Javadoc)
			 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
			 */
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				DriverStationMain.getDS().setTeamID(getTeamID());
			}
		});

		add(teamIDField,gbc_teamIDField);
	}
	
	/**
	 * @param teamid the team number which must be greater than 0
	 */
	public void setTeamID(int teamid) {
		teamIDField.setText((teamid<=0?"":Integer.toString(teamid)));
	}
	
	/**
	 * @return the team number or 0 if value of the team ID field is null
	 */
	public int getTeamID() {
		Object value=teamIDField.getValue();
		if(value!=null) {
			int teamIDRaw;
			if(value instanceof Long) {
				teamIDRaw=((Long)value).intValue();
			} else {
				teamIDRaw=((Double)value).intValue();
			}
			int teamID=teamIDRaw%10000;
			if(teamID!=teamIDRaw) {
				teamIDField.setText(Integer.toString(teamID));
			}
			return teamID;
		} else {
			return 0;
		}
	}
}
