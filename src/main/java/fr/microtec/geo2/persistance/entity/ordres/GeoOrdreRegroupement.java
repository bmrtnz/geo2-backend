package fr.microtec.geo2.persistance.entity.ordres;

import fr.microtec.geo2.persistance.converter.BooleanCharacterConverter;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class GeoOrdreRegroupement implements Serializable {
    @Column(name = "code_chargement")
    private String codeChargement;

    @Column(name = "ordregrp")
    private String ordreRegroupement;

    @Id
    @Column(name = "ordorig")
    private String ordreOrigine;

    @Column(name = "raisoc")
    private String raisonSociale;

    @Column(name = "ville")
    private String ville;

    @Column(name = "art_ref_orig")
    private String refArticleOrigine;

    @Column(name = "var_desc")
    private String descriptionVariete;

    @Column(name = "CAM_DESC")
    private String description;

    @Column(name = "cde_nb_pal")
    private String nombrePalettesCommandees;

    @Column(name = "NB_COL")
    private Integer nombreColis;

    @Column(name = "CDE_PDS_NET")
    private Float poidsNetCommande;

    @Column(name = "depdatp")
    private LocalDateTime dateDepartPrevue;

    @Column(name = "livdatp")
    private LocalDateTime dateLivraisonPrevue;

    @Column(name = "trp_code")
    private String transporteurCode;

    @Column(name = "fou_code_orig")
    private String station;

    @Id
    @Column(name = "ORL_LIG")
    private String numero;
}
