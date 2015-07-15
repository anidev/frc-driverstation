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

	private static final TeamStation[] REDS=new TeamStation[] {RED1,RED2,RED3};
	private static final TeamStation[] BLUES=new TeamStation[] {BLUE1,BLUE2,
			BLUE3};

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

	/**
	 * Returns a {@code TeamStation} value that has the same position as this
	 * one, but with the given alliance.
	 * 
	 * @return {@code TeamStation} with same position and given alliance, or
	 *         {@code null} if {@code newAlliance} is null.
	 */
	public TeamStation withAlliance(Alliance newAlliance) {
		if(alliance==newAlliance) {
			return this;
		}
		switch(newAlliance) {
		case RED:
			return REDS[position-1];
		case BLUE:
			return BLUES[position-1];
		}
		return null;
	}

	/**
	 * Returns a {@code TeamStation} value that has the same alliance as this
	 * one, but with the given position.
	 * 
	 * @param newPosition
	 *            Position of returned team station; should be 1, 2, or 3.
	 * @return {@code TeamStation} with same alliance and given position, or
	 *         {@code null} if the new position is not valid.
	 */
	public TeamStation withPosition(int newPosition) {
		if(position==newPosition) {
			return this;
		}
		if(position<1||position>3) {
			return null;
		}
		switch(alliance) {
		case RED:
			return REDS[newPosition-1];
		case BLUE:
			return BLUES[newPosition-1];
		}
		return null;
	}

	@Override
	public String toString() {
		return alliance.getName()+" "+position;
	}
}
