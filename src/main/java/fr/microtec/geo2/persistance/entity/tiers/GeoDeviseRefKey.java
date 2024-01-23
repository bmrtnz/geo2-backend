package fr.microtec.geo2.persistance.entity.tiers;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoDeviseRefKey implements Serializable {

    protected String id;
    protected String devise;

}
