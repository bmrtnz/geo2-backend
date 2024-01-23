package fr.microtec.geo2.persistance.entity.produits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import fr.microtec.geo2.persistance.repository.produits.matcher.GeoArticlePartMatch;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "avi_art_normalisation")
public class GeoArticleNormalisation extends ValidateCreatedAndModifiedEntity
        implements Duplicable<GeoArticleNormalisation> {

    @Id
    @Column(name = "ref_normalisation")
    @GeneratedValue(generator = "GeoArticleNormalisationGenerator")
    @GenericGenerator(name = "GeoArticleNormalisationGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "F_SEQ_AVI_ART_NORMALISATION"),
            @Parameter(name = "isSequence", value = "false")
    })
    private String id;

    @GeoArticlePartMatch
    @Column(name = "gtin_colis")
    private String gtinColis;

    @GeoArticlePartMatch
    @Column(name = "gtin_palette")
    private String gtinPalette;

    @GeoArticlePartMatch
    @Column(name = "gtin_uc")
    private String gtinUc;

    @GeoArticlePartMatch
    @Column(name = "com_client")
    private String descriptionCalibreClient;

    @GeoArticlePartMatch
    @Column(name = "mdd")
    private Boolean produitMdd;

    @GeoArticlePartMatch
    @Column(name = "pde_cliart")
    private String articleClient;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp_code")
    private GeoEspece espece;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "cam_code"))
    private GeoCalibreMarquage calibreMarquage;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "etf_code"))
    private GeoStickeur stickeur;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "etc_code"))
    private GeoEtiquetteColis etiquetteColis;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "etp_code"))
    private GeoEtiquetteUc etiquetteUc;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "etv_code"))
    private GeoEtiquetteEvenementielle etiquetteEvenementielle;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "ids_code"))
    private GeoIdentificationSymbolique identificationSymbolique;

    @GeoArticlePartMatch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(formula = @JoinFormula("esp_code"))
    @JoinColumnOrFormula(column = @JoinColumn(name = "maq_code"))
    private GeoMarque marque;

    public GeoArticleNormalisation duplicate() {
        GeoArticleNormalisation clone = new GeoArticleNormalisation();
        clone.espece = this.espece;
        clone.calibreMarquage = this.calibreMarquage;
        clone.stickeur = this.stickeur;
        clone.etiquetteColis = this.etiquetteColis;
        clone.etiquetteUc = this.etiquetteUc;
        clone.etiquetteEvenementielle = this.etiquetteEvenementielle;
        clone.identificationSymbolique = this.identificationSymbolique;
        clone.marque = this.marque;
        clone.gtinColis = this.gtinColis;
        clone.gtinUc = this.gtinUc;
        clone.descriptionCalibreClient = this.descriptionCalibreClient;
        clone.produitMdd = this.produitMdd;
        clone.articleClient = this.articleClient;
        clone.gtinPalette = this.gtinPalette;

        return clone;
    }

}
