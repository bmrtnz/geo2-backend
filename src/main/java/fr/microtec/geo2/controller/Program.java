package fr.microtec.geo2.controller;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

@Getter
public enum Program implements StringEnum {
    TESCO("tesco"),
    ORCHARD("orchard"),
    PREORDRES("preordres");

    private final String key;

    Program(String key) {
        this.key = key;
    }

}
