package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_societe")
@Entity
public class GeoSociete extends ValidateEntity {

	@Id
	@Column(name = "soc_code")
	private String id;

	@Column(name = "raisoc")
	private String raisonSocial;

	@Column(name = "ads1")
	private String adresse1;

	@Column(name = "ads2")
	private String adresse2;

	@Column(name = "ads3")
	private String adresse3;

	@Column(name = "zip")
	private String codePostal;

	@Column(name = "ville")
	private String ville;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_code")
	private GeoPays pays;

}
