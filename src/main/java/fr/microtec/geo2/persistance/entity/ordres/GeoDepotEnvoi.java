package fr.microtec.geo2.persistance.entity.ordres;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import lombok.Data;

@Data
@Entity
@Table(name = "geo_depot_envoi")
public class GeoDepotEnvoi {

    @Id
    @Column(name = "ref_dep_env")
    @GeneratedValue(generator = "GeoDepotEnvoiGenerator")
    @GenericGenerator(name = "GeoDepotEnvoiGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_ref_dep_env")
    })
    private BigDecimal id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Column(name = "dat_depot")
    private LocalDateTime dateDepot;

    @Column(name = "flu_code")
    private String fluxID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nom_utilisateur")
    private GeoUtilisateur utilisateur;

}
