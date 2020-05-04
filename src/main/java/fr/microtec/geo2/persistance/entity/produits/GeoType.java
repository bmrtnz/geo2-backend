package fr.microtec.geo2.persistance.entity.produits;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "geo_typ")
public class GeoType {

	@Id
	@Column(name = "typ_ref")
	private String id;

	@Column(name = "typ_code")
	private String code;

	@Column(name = "typ_desc")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

}
