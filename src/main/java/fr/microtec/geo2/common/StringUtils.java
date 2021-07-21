package fr.microtec.geo2.common;

public class StringUtils {

	/**
	 * Pad string left with given delimiter for given size.
	 */
	public static String padLeft(String s, String delimiter, int size) {
		return String.format("%1$" + size + "s", s).replace(" ", delimiter);
	}

	/**
	 * Pad string right with given delimiter for given size.
	 */
	public static String padRight(String s, String delimiter, int size) {
		return String.format("%1$-" + size + "s", s).replace(" ", delimiter);
	}

}
