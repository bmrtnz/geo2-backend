package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidableAndModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_transi")
public class GeoTransitaire extends ValidableAndModifiableEntity {

	@Id
	@Column(name = "trs_code")
	private String id;

	@Column(name = "raisoc")
	private String raisonSocial;

	@Column(name = "ads1")
	private String adresse1;

	@Column(name = "ads2")
	private String adresse2;

	@Column(name = "ads3")
	private String adresse3;

	@Column(name = "ville")
	private String ville;

	@Column(name = "zip")
	private String codePostal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code")
	private GeoPays pays;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lan_code")
	private GeoPays langue;

}
