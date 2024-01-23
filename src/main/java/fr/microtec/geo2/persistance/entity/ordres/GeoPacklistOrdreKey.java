package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoPacklistOrdreKey implements Serializable {

    protected BigDecimal id;
    protected String ordre;

}
