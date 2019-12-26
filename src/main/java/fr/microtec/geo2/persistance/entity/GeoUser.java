package fr.microtec.geo2.persistance.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "geo_user")
@Entity
public class GeoUser {

	@Id
	@Column(name = "nom_utilistaeur")
	private String nomUtilisateur;

	@Column
	private String email;

	@Column
	private String motDePasse;

	@Column
	private boolean valide;

	@Column
	private boolean geoTiers;

	@Column
	private boolean geoProduit;
}
