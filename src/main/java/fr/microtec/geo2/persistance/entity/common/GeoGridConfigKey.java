package fr.microtec.geo2.persistance.entity.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoGridConfigKey implements Serializable {

    protected String utilisateur;
    protected String grid;
    protected String societe;

}
