package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoDefCodePromoId implements Serializable {
    protected String codePromo;
    protected String espece;
    protected String variete;
}
