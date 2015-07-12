package org.anidev.frcds.proto2.types;

public enum Alliance {
	RED("Red",'R'),
	BLUE("Blue",'B');
	private String name;
	private char id;

	private Alliance(String name,char id) {
		this.name=name;
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public char getId() {
		return id;
	}

	public static Alliance find(char id) {
		switch(Character.toUpperCase(id)) {
		case 'R':
			return Alliance.RED;
		case 'B':
			return Alliance.BLUE;
		}
		return null;
	}
}
