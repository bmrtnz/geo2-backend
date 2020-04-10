package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidableAndModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_person")
@Entity
public class GeoPersonne extends ValidableAndModifiableEntity {

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

}
