package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_colora")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoColoration extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "clr_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "clr_desc")
	private String description;

	@Column(name = "clr_libvte")
	private String descriptionClient;

	// OK_V2 -> Not used

}
