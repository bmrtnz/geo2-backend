package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_sucre")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoSucre extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "suc_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "suc_desc")
	private String description;

	@Column(name = "suc_libvte")
	private String descriptionClient;

}
