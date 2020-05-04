package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_idsymb")
public class GeoIdentificationSymbolique extends ValidateAndModifiedEntity {

	@EmbeddedId
	private GeoIdentificationSymboliqueId identificationSymboliqueId;

	@Column(name = "ids_desc")
	private String description;

	@Column(name = "ids_libvte")
	private String descriptionClient;

}
