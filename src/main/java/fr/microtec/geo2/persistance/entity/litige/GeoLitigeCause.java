package fr.microtec.geo2.persistance.entity.litige;

import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_litcau")
@Entity
public class GeoLitigeCause extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "lca_code")
    private String id;

    @Column(name = "lca_desc")
    private String description;

    @Column(name = "tyt_code")
    private Character typeTier;

    @Column(name = "ind_regul")
    private Boolean indicateurRegularisation;

    @Column(name = "num_tri")
    private BigInteger numeroTri;

    @Column(name = "dat_fin_util")
    private LocalDate dateFin;

}
