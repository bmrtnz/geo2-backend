package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_etifru")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoStickeur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "etf_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "etf_desc")
	private String description;

	@Column(name = "etf_libvte")
	private String descriptionClient;

	// TODO field : clf_code

}
