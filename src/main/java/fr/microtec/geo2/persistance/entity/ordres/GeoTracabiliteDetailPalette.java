package fr.microtec.geo2.persistance.entity.ordres;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_traca_detail_pal")
@Entity
public class GeoTracabiliteDetailPalette extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "ref_traca")
    private Integer id;

    @Column(name = "sscc")
    private String SSCC;

    @Column(name = "pds_net")
    private Float poidsNet;

    @Column(name = "pds_brut")
    private Float poidsBrut;

    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name = "pal_sol")
    private Boolean paletteAuSol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pal_code")
    private GeoTypePalette typePalette;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tracabiliteDetailPalette")
    private List<GeoTracabiliteLigne> tracabiliteLignes;

}
