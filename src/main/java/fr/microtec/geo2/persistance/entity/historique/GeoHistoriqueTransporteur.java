package fr.microtec.geo2.persistance.entity.historique;

import java.math.BigDecimal;

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

import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "geo_histo_transp")
@Entity
public class GeoHistoriqueTransporteur extends GeoBaseHistorique {

    @Id
    @Column(name = "histo_trp_ref")
    @GeneratedValue(generator = "GeoHistoriqueTransporteurGenerator")
    @GenericGenerator(name = "GeoHistoriqueTransporteurGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_histo_trp"),
    })
    private BigDecimal id;

    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_code", nullable = false)
    private GeoTransporteur transporteur;

}
