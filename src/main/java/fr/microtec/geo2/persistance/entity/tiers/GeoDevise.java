package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
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

}
