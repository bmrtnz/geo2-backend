package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_penetro")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoPenetro extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "pen_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "pen_desc")
	private String description;

	@Column(name = "pen_libvte")
	private String descriptionClient;

	@Column(name = "pen_min")
	private Float minimum;

	@Column(name = "pen_moy")
	private Float moyenne;

	@Column(name = "pen_max")
	private Float maximum;

}
