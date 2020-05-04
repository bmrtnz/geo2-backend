package fr.microtec.geo2.persistance.entity.produits;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class GeoCalibreFournisseurId implements Serializable {

	@Column(name = "caf_code")
	private String id;

	/*@Column(name = "esp_code")
	private String especeId;*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

}
