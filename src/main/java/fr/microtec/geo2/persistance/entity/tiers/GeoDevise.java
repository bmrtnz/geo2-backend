package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_devise")
@Entity
public class GeoDevise extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "dev_code")
	private String id;

	@Column(name = "dev_desc")
	private String description;

	@Column(name = "dev_tx")
	private Double taux;

	@Column(name = "dev_tx_achat")
	private Double tauxAchat;

	public static GeoDevise getDefault() {
		GeoDevise dev = new GeoDevise();
		dev.setId("EUR");
		return dev;
	}

}
