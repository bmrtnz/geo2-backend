package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_mru_ordre")
@IdClass(GeoMRUOrdreKey.class)
@Entity
public class GeoMRUOrdre extends ModifiedEntity implements Serializable {

    @Id
    @Column(name = "ord_ref")
    private String ordreRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref", insertable = false, updatable = false)
    private GeoOrdre ordre;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nom_utilisateur")
    private GeoUtilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cen_code", referencedColumnName = "cen_ref")
    private GeoEntrepot entrepot;

    @Column(name = "nordre")
    private String numero;

    @Column(name = "nom_utilisateur", insertable = false, updatable = false)
    private String nomUtilisateur;

    @Column(name = "soc_code", insertable = false, updatable = false)
    private String socCode;

    @Column(name = "cen_code", insertable = false, updatable = false)
    private String cenCode;

}
