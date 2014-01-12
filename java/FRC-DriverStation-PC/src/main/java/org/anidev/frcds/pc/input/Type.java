package org.anidev.frcds.pc.input;

public enum Type {
	// Genearlly, joysticks have 3 axes (x, y, z,
	// where z is rotation or throttle)
	// Gamepads have 4 axes (two sticks x and y),
	// or 6 axes (two sticks x and y, l and r triggers)
	// 6-axis gamepads are generally XBOX controllers
	JOYSTICK,GAMEPAD4,GAMEPAD6,UNKNOWN;
}
