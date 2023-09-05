package fr.microtec.geo2.persistance.entity.ordres;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_attrib_frais")
@Entity
public class GeoAttribFrais extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "k_frais")
    private BigDecimal id;

    @Column(name = "frais_pu")
    private Float fraisPU;

    @Column(name = "frais_unite")
    private GeoBaseTarif fraisUnite;

    @Column(name = "accompte")
    private Float accompte;

    @Column(name = "perequation")
    private Boolean perequation;

}
