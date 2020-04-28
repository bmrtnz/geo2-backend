package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_etievt")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoEtiquetteEvenementielle extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "etv_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "etv_code")
	private GeoEspece espece;

	@Column(name = "etv_desc")
	private String description;

	@Column(name = "etv_libvte")
	private String descriptionClient;

	// TODO field : clf_code

}
