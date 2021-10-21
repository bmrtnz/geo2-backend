package fr.microtec.geo2.persistance.entity.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
@Entity
@Table(name = "geo_modif")
public class GeoModification {

	@Id
	@Column(name = "k_modif")
  @GeneratedValue(generator = "GeoModifGenerator")
	@GenericGenerator(
			name = "GeoModifGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_key_modif"),
					@org.hibernate.annotations.Parameter(name = "isSequence", value = "true")
			}
	)
	private BigDecimal id;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modification")
	private List<GeoModificationCorps> corps;

	@Column(name = "entite")
	private String entite;

	@Column(name = "entite_key")
	private String entiteID;

	@Column(name = "statut")
	private Boolean statut;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "utilisateur")
  private GeoUtilisateur initiateur;

  @LastModifiedDate
	@Column(name = "mod_date")
	private LocalDateTime dateModification;

}
