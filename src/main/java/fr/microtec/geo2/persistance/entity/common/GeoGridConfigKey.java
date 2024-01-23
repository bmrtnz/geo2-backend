package fr.microtec.geo2.persistance.entity.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoGridConfigKey implements Serializable {

    protected String utilisateur;
    protected String grid;
    protected String societe;

}
