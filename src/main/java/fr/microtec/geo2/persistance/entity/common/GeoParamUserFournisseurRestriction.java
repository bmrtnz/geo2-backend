package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Data;

@Data
@Table(name = "geo_param_user_fourni_restr")
@Entity
public class GeoParamUserFournisseurRestriction {

	@Id
	@Column(name = "rownum")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "nom_utilisateur", nullable = false)
	private GeoUtilisateur utilisateur;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code", nullable = false)
	private GeoFournisseur fournisseur;

}
