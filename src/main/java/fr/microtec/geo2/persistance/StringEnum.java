package fr.microtec.geo2.persistance;

import java.util.ArrayList;
import java.util.List;

/**
 * StringEnum interface for generic JPA converter.
 */
public interface StringEnum {

    /**
     * Get enum key value.
     *
     * @return Enum key value
     */
    String getKey();

    /**
     * Find enum const from Enum class and key.
     *
     * @param clazz Enum class
     * @param key   Value to search into enum const key
     * @return Enum const if founded
     */
    static <T extends Enum<T> & StringEnum> T getValueOf(Class<T> clazz, String key) {
        final T[] values = clazz.getEnumConstants();

        for (T v : values) {
            if (v.getKey().equals(key)) {
                return v;
            }
        }

        throw new IllegalArgumentException(String.format("No enum value (%s) found in %s", key, clazz.getSimpleName()));
    }

    /**
     * Get enum keys as `List`
     */
    static <T extends Enum<T> & StringEnum> List<String> getKeys(Class<T> clazz) {
        List<String> keys = new ArrayList<>();
        for (T v : clazz.getEnumConstants())
            keys.add(v.getKey());
        return keys;
    }

}
