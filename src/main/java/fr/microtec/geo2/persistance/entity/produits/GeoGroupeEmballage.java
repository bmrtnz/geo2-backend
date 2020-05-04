package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_grpemb")
public class GeoGroupeEmballage extends ValidateAndModifiedEntity {

	@EmbeddedId
	private GeoGroupeEmballageId groupeEmballageId;

	@Column(name = "gem_desc")
	private String description;

}
