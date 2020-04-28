package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_cirage")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoCirage extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "cir_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "cir_desc")
	private String description;

	@Column(name = "cir_libvte")
	private String descriptionClient;

}
