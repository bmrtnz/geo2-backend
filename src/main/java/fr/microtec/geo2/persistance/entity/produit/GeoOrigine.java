package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_origine")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoOrigine extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "ori_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "ori_desc")
	private String description;

	@Column(name = "ori_libvte")
	private String descriptionClient;

	@Column(name = "cee")
	private Boolean origineCee;



}
