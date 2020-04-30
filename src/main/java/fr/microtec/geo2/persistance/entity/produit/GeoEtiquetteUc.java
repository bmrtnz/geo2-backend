package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_etipmb")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoEtiquetteUc extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "etp_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "etp_desc")
	private String description;

	@Column(name = "etp_libvte")
	private String descriptionClient;

	// TODO field : clf_code

}
