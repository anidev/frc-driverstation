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

	/**
	 * Return the {@code Alliance} value corresponding to the given character
	 * ID. {@code 'R'} corresponds to red, and {@code 'B'} corresponds to blue.
	 * 
	 * @param id
	 *            {@code 'R'} for red or {@code 'B'} for blue.
	 * @return The appropriate {@code Alliance} value, or {@code null} if the
	 *         given ID is invalid.
	 */
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
