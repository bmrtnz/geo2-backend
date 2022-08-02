package fr.microtec.geo2.persistance;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.microtec.geo2.persistance.entity.ordres.GeoFactureAvoir;
import lombok.SneakyThrows;

public class TestEnum {

    @Test
    public void testGetValueOf() {
        Assertions.assertEquals(GeoFactureAvoir.AVOIR, StringEnum.getValueOf(GeoFactureAvoir.class, "A"));
    }

    @Test
    public void testGetValueOfWithBadData() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> StringEnum.getValueOf(GeoFactureAvoir.class, "!"));
    }

    @Test
    @SneakyThrows
    public void testGetValueOfByReflection() {
        Class<?> clazz = GeoFactureAvoir.class;
        Class<?>[] interfaces = clazz.getInterfaces();
        GeoFactureAvoir actual = null;

        for (Class<?> inter : interfaces) {
            if (StringEnum.class.equals(inter)) {
                actual = (GeoFactureAvoir) inter.getMethod("getValueOf", Class.class, String.class).invoke(null, clazz,
                        "A");
            }
        }

        Assertions.assertEquals(GeoFactureAvoir.AVOIR, actual);
    }

    @Test
    public void testGetKeys() {
        Assertions.assertEquals(ArrayList.class, StringEnum.getKeys(GeoFactureAvoir.class).getClass());
    }

}
