package fr.microtec.geo2.persistance.entity.tiers;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoMouvementEntrepot extends GeoSupervisionPalox {

    @Column(name = "nordre")
    String numeroOrdre;

    @Column(name = "depdatp")
    LocalDateTime dateDepartOrdre;

    @Column(name = "bon_retour")
    String bonRetour;

    @Column(name = "cmr")
    String cmr;

    @Column(name = "ref_cli")
    String referenceClient;

    @Column(name = "cli_ref")
    String codeClient;

    @Column(name = "palox_ko_nbr")
    Double nombrePaloxKO;

    @Column(name = "palox_ko_cause")
    Double nombrePaloxCause;

    @Column(name = "station")
    String station;

}
