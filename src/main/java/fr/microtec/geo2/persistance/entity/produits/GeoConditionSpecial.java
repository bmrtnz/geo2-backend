package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_conspe")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoConditionSpecial extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "cos_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "cos_desc")
	private String description;

	@Column(name = "cos_libvte")
	private String descriptionClient;

}
