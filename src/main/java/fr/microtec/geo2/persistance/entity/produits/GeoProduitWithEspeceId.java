package fr.microtec.geo2.persistance.entity.produits;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoProduitWithEspeceId implements Serializable {

    protected String id;
    protected String espece;

}
