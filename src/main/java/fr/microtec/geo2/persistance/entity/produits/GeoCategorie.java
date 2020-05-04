package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_catego")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoCategorie extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "cat_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "cat_desc")
	private String description;

	@Column(name = "cat_libvte")
	private String descriptionClient;

	@Column(name = "ccw_code")
	private String cahierDesChargesBlueWhale;

}
