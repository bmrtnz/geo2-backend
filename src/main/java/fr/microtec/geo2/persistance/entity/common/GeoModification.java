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
import javax.validation.constraints.NotNull;

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

	@NotNull
	@Column(name = "entite", nullable = false)
	private String entite;

	@NotNull
	@Column(name = "entite_key", nullable = false)
	private String entiteID;

	@NotNull
	@Column(name = "statut", nullable = false)
	private Boolean statut = false;

	@NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "utilisateur", nullable = false)
  private GeoUtilisateur initiateur;

  @LastModifiedDate
	@Column(name = "mod_date")
	private LocalDateTime dateModification;

}
