package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_mru_entrep")
@Entity
public class GeoMRUEntrepot extends ModifiedEntity {
  
  @Id
	@Column(name = "cen_code")
  private String id;
  
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nom_utilisateur")
  private GeoUtilisateur utilisateur;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cen_ref")
	private GeoEntrepot entrepot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
  private GeoSociete societe;
  
}