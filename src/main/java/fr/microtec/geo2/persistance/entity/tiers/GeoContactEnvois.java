package fr.microtec.geo2.persistance.entity.tiers;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoContactEnvois implements Serializable {

	@Id
	@Column(name = "con_ref")
	private String id;
	@Column(name = "con_tyt")
	private Character typeTiers;
	@Column(name = "con_tiers")
	private String codeTiers;
	@Column(name = "moc_code")
	private String moyenCommunication;
	@Column(name = "con_acces1")
	private String fluxAccess1;
	@Column(name = "con_acces2")
	private String fluxAccess2;
	@Column(name = "con_fluvar")
	private String fluxComplement;
	@Column(name = "con_prenom")
	private String prenom;
	@Column(name = "con_nom")
	private String nom;
	@Column(name = "con_dot")
	private String fichierDot;
	@Column(name = "con_map")
	private String fichierMap;

	@ManyToOne
	@JoinColumn(name = "con_ref", insertable = false, updatable = false)
	private GeoContact contact;

}
