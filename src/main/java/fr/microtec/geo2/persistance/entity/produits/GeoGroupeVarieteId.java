package fr.microtec.geo2.persistance.entity.produits;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class GeoGroupeVarieteId implements Serializable {

	@Column(name = "grv_code")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

}
