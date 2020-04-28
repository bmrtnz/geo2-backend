package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_calfou")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoCalibreFournisseur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "caf_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "caf_desc", nullable = false)
	private String description;


}
