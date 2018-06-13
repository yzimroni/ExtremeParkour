package net.yzimroni.extremeparkour.utils;

import org.bukkit.Material;

public class ElytraUtils {

	private static boolean init = false;

	private static Material elytra = null;
	private static boolean rockets = false;

	private ElytraUtils() {

	}

	public static void init() {
		if (!init) {
			try {
				elytra = Material.ELYTRA;
			} catch (Error | Exception ignored) {}
			try {
				Material m = Material.IRON_NUGGET;
				m.name();
				rockets = true;
			} catch (Error | Exception e) {
				rockets = false;
			}
			init = true;
			ExtremeParkourLogger.log("Elytra supported? " + elytraSupported());
			ExtremeParkourLogger.log("Rockets supported? " + rocketsSupported());
		}
	}

	public static boolean elytraSupported() {
		return elytra != null;
	}

	public static Material getElytra() {
		return elytra;
	}

	public static boolean rocketsSupported() {
		return rockets;
	}

}
