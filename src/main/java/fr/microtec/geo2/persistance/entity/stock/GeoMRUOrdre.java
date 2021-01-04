package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_mru_ordre")
@IdClass(GeoMRUOrdreKey.class)
@Entity
public class GeoMRUOrdre extends ModifiedEntity {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
  private GeoOrdre ordre;
  
  @Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nom_utilisateur")
  private GeoUtilisateur utilisateur;
  
  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cen_code")
	private GeoMRUEntrepot entrepot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
  private GeoSociete societe;

  @NotNull
	@Column(name = "nordre", nullable = false, unique = true)
  private String numero;
  
}