package fr.microtec.geo2.persistance.entity.produit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoProduitWithEspeceId implements Serializable {

	protected String id;
	protected String espece;

}
