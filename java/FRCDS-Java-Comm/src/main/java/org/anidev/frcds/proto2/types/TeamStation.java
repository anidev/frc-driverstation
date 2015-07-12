package org.anidev.frcds.proto2.types;

import org.anidev.frcds.protoold.torobot.Alliance;

/**
 * Where the driver station is (i.e. Red 3)
 */
public enum TeamStation {
	RED1(Alliance.RED,1),
	RED2(Alliance.RED,2),
	RED3(Alliance.RED,3),
	BLUE1(Alliance.BLUE,1),
	BLUE2(Alliance.BLUE,2),
	BLUE3(Alliance.BLUE,3);

	private Alliance alliance;
	private int position;

	/**
	 * @param alliance the team's alliance
	 * @param position the teams position
	 */
	private TeamStation(Alliance alliance,int position) {
		this.alliance=alliance;
		this.position=position;
	}

	/**
	 * @return which alliance the driver station is on
	 */
	public Alliance getAlliance() {
		return alliance;
	}

	/**
	 * @return the team position
	 */
	public int getPosition() {
		return position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return alliance.getName()+" "+position;
	}
}
