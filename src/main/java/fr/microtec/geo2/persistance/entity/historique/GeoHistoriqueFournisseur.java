package fr.microtec.geo2.persistance.entity.historique;

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
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_histo_fourni")
@Entity
public class GeoHistoriqueFournisseur extends GeoBaseHistorique {

    @Id
    @Column(name = "histo_fou_code")
    @GeneratedValue(generator = "GeoHistoriqueFournisseurGenerator")
    @GenericGenerator(name = "GeoHistoriqueFournisseurGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_histo_fourni"),
            @Parameter(name = "mask", value = "FM099999")
    })
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "k_fou", nullable = false)
    private GeoFournisseur fournisseur;

}
