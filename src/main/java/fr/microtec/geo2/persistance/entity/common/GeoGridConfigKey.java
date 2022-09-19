package fr.microtec.geo2.persistance.entity.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoGridConfigKey implements Serializable {

    protected String utilisateur;
    protected String grid;
    protected String societe;

}
