package fr.microtec.geo2.persistance.entity.tiers;

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

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_ent_trp_bassin")
public class GeoEntrepotTransporteurBassin extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "id_ent_trp_bassin")
    @GeneratedValue(generator = "GeoEntrepotTransporteurBassinGenerator")
    @GenericGenerator(name = "GeoEntrepotTransporteurBassinGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "seq_id_ent_trp_bassin"),
    })
    private BigDecimal id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_ref", nullable = false)
    private GeoEntrepot entrepot;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_code", nullable = false)
    private GeoTransporteur transporteur;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_code", nullable = false)
    private GeoBureauAchat bureauAchat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_bta_code")
    private GeoBaseTarif baseTarifTransport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trp_dev_code")
    private GeoDevise deviseTarifTransport;

    @Column(name = "trp_pu")
    private Float prixUnitaireTarifTransport;

}
