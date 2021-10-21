package fr.microtec.geo2.persistance.entity.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
@Entity
@Table(name = "geo_modif_corps")
public class GeoModificationCorps {

	@Id
	@Column(name = "k_modif_corps")
  @GeneratedValue(generator = "GeoModifCorpsGenerator")
	@GenericGenerator(
			name = "GeoModifCorpsGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_key_modif_corps"),
					@org.hibernate.annotations.Parameter(name = "isSequence", value = "true")
			}
	)
	private BigDecimal id;

	@NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modif", nullable = false)
  private GeoModification modification;

	@NotNull
	@Column(name = "chemin", nullable = false)
	private String chemin;

	@Column(name = "traduction_key")
	private String traductionKey;

	@NotNull
	@Column(name = "valeur_actuelle", nullable = false)
	private String valeurActuelle;

	@NotNull
	@Column(name = "valeur_demandee", nullable = false)
	private String valeurDemandee;

	@Column(name = "affichage_actuel")
	private String affichageActuel;

	@Column(name = "affichage_demande")
	private String affichageDemande;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "validateur")
  private GeoUtilisateur validateur;

  @LastModifiedDate
	@Column(name = "mod_date")
	private LocalDateTime dateModification;

}
