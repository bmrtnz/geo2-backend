package fr.microtec.geo2.persistance.entity.ordres;

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
public class GeoDefCodePromoId implements Serializable {
    protected String codePromo;
    protected String espece;
    protected String variete;
}
