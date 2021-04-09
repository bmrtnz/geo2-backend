package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_person")
@Entity
public class GeoPersonne extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "per_code")
	private String id;

	@Column(name = "per_prenom")
	private String prenom;

	@Column(name = "per_nom")
	private String nom;

	@Column(name = "per_service")
	private String service;

	@Column(name = "per_username")
	private String nomUtilisateur;

	@Column(name = "per_imprim")
	private String imprimante;

	@Column(name = "per_email")
	private String email;

	@Column(name = "per_role")
	private GeoRole role;

}
