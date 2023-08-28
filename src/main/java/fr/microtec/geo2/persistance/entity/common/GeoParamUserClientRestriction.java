package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_param_user_client_restr")
@Entity
public class GeoParamUserClientRestriction {

	@Id
	@Column(name = "rownum")
	private Integer id;

	@Column(name = "nom_utilisateur", nullable = false)
	private String nomUtilisateur;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cli_ref", nullable = false)
	private GeoClient client;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cen_ref")
	private GeoEntrepot entrepot;

}
