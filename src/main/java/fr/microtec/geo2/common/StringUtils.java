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

    /**
     * Change the case of the first character to uppercase
     */
    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

}
