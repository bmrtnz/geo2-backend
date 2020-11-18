package fr.microtec.geo2.persistance.entity.tiers;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@DiscriminatorValue(GeoFournisseur.TYPE_TIERS)
public class GeoCertificationFournisseur extends GeoCertificationTier {

	@Column(name = "date_validite")
	private LocalDate dateValidite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tiers", referencedColumnName = "k_fou", insertable = true)
	private GeoFournisseur fournisseur;

}