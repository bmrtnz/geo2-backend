package fr.microtec.geo2.persistance.entity.produits;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class GeoIdentificationSymboliqueId implements Serializable {

	@Column(name = "ids_code")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

}
