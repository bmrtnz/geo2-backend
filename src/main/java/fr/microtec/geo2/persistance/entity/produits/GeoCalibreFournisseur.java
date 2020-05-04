package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_calfou")
public class GeoCalibreFournisseur extends ValidateAndModifiedEntity {

	@EmbeddedId
	private GeoCalibreFournisseurId calibreFournisseurId;

	@Column(name = "caf_desc", nullable = false)
	private String description;

}
