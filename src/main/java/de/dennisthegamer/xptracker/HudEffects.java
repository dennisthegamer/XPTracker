package de.dennisthegamer.xptracker;

public class HudEffects {

	private static int resetMessageTicks = 0;
	private static final int RESET_MESSAGE_DURATION = 40; // 2 seconds

	public static void triggerResetMessage() {
		resetMessageTicks = RESET_MESSAGE_DURATION;
	}

	public static void tick() {
		if (resetMessageTicks > 0) {
			resetMessageTicks--;
		}
	}

	public static boolean isResetMessageActive() {
		return resetMessageTicks > 0;
	}
}
