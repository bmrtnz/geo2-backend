package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_mru_ordre")
@IdClass(GeoMRUOrdreKey.class)
@Entity
public class GeoMRUOrdre extends ModifiedEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
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
