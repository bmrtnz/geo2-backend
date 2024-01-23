package fr.microtec.geo2.persistance.entity.tiers;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue(GeoFournisseur.TYPE_TIERS)
public class GeoCertificationFournisseur extends GeoCertificationTier {

    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiers", referencedColumnName = "k_fou", insertable = true)
    private GeoFournisseur fournisseur;

}
