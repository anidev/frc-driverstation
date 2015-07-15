package org.anidev.frcds.proto2.types;

import org.anidev.frcds.protoold.torobot.Alliance;

/**
 * Team station, which consists of the alliance and position (1, 2, 3).
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
	 * @param alliance
	 *            Team alliance color
	 * @param position
	 *            Team position (1, 2, 3)
	 */
	private TeamStation(Alliance alliance,int position) {
		this.alliance=alliance;
		this.position=position;
	}

	/**
	 * @return Which alliance the driver station is on.
	 */
	public Alliance getAlliance() {
		return alliance;
	}

	/**
	 * @return Position of team.
	 */
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return alliance.getName()+" "+position;
	}
}
