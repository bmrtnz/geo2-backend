package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_marque")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoMarque extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "maq_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "maq_desc")
	private String description;

	@Column(name = "maq_libvte")
	private String descriptionClient;

}
