package org.anidev.frcds.proto.torobot;


public enum TeamStation {
	RED1(Alliance.RED,1),
	RED2(Alliance.RED,2),
	RED3(Alliance.RED,3),
	BLUE1(Alliance.BLUE,1),
	BLUE2(Alliance.BLUE,2),
	BLUE3(Alliance.BLUE,3);

	private Alliance alliance;
	private int position;

	private TeamStation(Alliance alliance,int position) {
		this.alliance=alliance;
		this.position=position;
	}

	public Alliance getAlliance() {
		return alliance;
	}

	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return alliance.getName()+" "+position;
	}
}
