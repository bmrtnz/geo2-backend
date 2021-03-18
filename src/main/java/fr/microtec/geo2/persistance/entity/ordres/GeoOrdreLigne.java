package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordlig")
@Entity
public class GeoOrdreLigne extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "orl_ref")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "art_ref")
	private GeoArticle article;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref")
	private GeoOrdre ordre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
	private GeoFournisseur fournisseur;

}