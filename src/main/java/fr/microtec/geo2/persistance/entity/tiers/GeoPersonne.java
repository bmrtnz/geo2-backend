package fr.microtec.geo2.persistance.entity.tiers;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_person")
@Entity
public class GeoPersonne extends ValidateAndModifiedEntity implements Serializable {

	@Id
	@Column(name = "per_code")
	private String id;

	@Column(name = "per_prenom")
	private String prenom;

	@Column(name = "per_nom")
	private String nom;

	@Column(name = "per_service")
	private String service;

	@Column(name = "per_username", insertable = false, updatable = false)
	private String nomUtilisateur;

	@Column(name = "per_imprim")
	private String imprimante;

	@Column(name = "per_email")
	private String email;

	@Column(name = "per_role")
	private GeoRole role;

	@Column(name = "ind_pres_spec")
	private String indicateurPresentationSUP;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_username", insertable = false, updatable = false)
	private GeoUtilisateur utilisateur;

}
