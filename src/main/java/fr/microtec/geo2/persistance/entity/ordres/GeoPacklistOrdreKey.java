package fr.microtec.geo2.persistance.entity.ordres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoPacklistOrdreKey implements Serializable {

    protected BigDecimal id;
    protected String ordre;

}
