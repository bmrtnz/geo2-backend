package fr.microtec.geo2.persistance.entity.ordres;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.document.GeoAsCQTechnique;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_cqligne")
@Entity
public class GeoCQLigne extends ModifiedEntity implements GeoAsCQTechnique {

    @Id
    @Column(name = "cql_ref")
    private String id;

    @Column(name = "cqc_ref")
    private String referenceCQC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref")
    private GeoOrdreLigne ordreLigne;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "ligne")
    private GeoCQExpedition expedition;

    @Column(name = "col_nbf")
    private Float nombreFruitsTheoriqueParColis;

    @Column(name = "col_nb_ligne")
    private Float nombreColis;

    @Column(name = "col_pdsnet")
    private Float poidsNetTheoriqueParColis;

    @Column(name = "art_desc")
    private String articleDescription;

    @Column(name = "art_desc_abrege")
    private String articleDescriptionAbrege;

    @Column(name = "ok_lot")
    private Boolean lotConforme;

    @Column(name = "ok_colis")
    private Boolean colisConforme;

    @Column(name = "ok_corniere")
    private Boolean corniereConforme;

    @Column(name = "ok_feuillard")
    private Boolean feuillardConforme;

    @Column(name = "mar_ean128")
    private Boolean normalisationEAN128;

    @Column(name = "mar_trace")
    private Boolean codeTracabilite;

    @Column(name = "mar_barcode")
    private Boolean codeBarreClient;

    @Column(name = "nok_tri")
    private Boolean nonConformeTri;

    @Column(name = "nok_autrecli")
    private Boolean nonConformeAutreClient;

    @Column(name = "nok_derog_interne")
    private Character nonConformeDerogationInterne;

    @Column(name = "nok_derog_interne_nom")
    private String nomResponsableDerogationInterne;

    @Column(name = "nok_cause")
    private String causeNonConformite;

    @Column(name = "nok_tri_pal_nb")
    private Float nombrePalettesTri;

    @Column(name = "pal_nb_ligne")
    private Float nombrePalettes;

    @Column(name = "pal_nb_fou")
    private Float nombrePalettesFournisseur;

    @Column(name = "pal_code_reel")
    private String codePaletteEffectif;

    @Column(name = "cq_commentaires")
    private String commentairesControleur;

    @Column(name = "nok_client_ok")
    private Boolean accepteApresAccordClient;

    @Column(name = "evalue")
    private Boolean evalue;

    @Column(name = "fin_date")
    private LocalDateTime dateFinControle;

    @Column(name = "off_date")
    private LocalDateTime dateDerniereSaisieOffline;

    @Column(name = "off_user")
    private String utilisateurDerniereSaisieOffline;

    @Column(name = "off_computer")
    private String machineDerniereSaisieOffline;

    @Column(name = "cql_user")
    private String utilisateurEnCharge;

    @Column(name = "ccw_code")
    private GeoCahierDesCharges cahierDesCharges;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @Column(name = "ordre_desc")
    private String ordreDescription;

    @Column(name = "art_ref")
    private String articleReference;

    @Column(name = "nb_colis_controle")
    private Float nombreColisControles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pal_code")
    private GeoTypePalette typePalette;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_ref")
    private GeoOrdre ordre;

    @Transient
    private String description;

    @Transient
    private String isExp;

    @Formula("case when ok_lot is not null then (case when ok_lot = 'O' then 'OK' else 'NON' end) else 'N/A' end")
    private String cq;

    @PostLoad
    public void postLoad() {

        // description
        if (this.articleDescriptionAbrege != null) {
            this.description = this.articleDescriptionAbrege;
            if (this.utilisateurEnCharge != null)
                this.description += " par " + this.utilisateurEnCharge;
            if (this.dateDerniereSaisieOffline != null)
                this.description += " "
                        + this.dateDerniereSaisieOffline
                                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                        + " ";
            if (Optional.ofNullable(this.nonConformeTri).orElse(false))
                this.description += "tri ";
            if (Optional.ofNullable(this.nonConformeAutreClient).orElse(false))
                this.description += "bloqué ";
            if (Optional.ofNullable(this.accepteApresAccordClient).orElse(false))
                this.description += "bloqué ";

            if (this.accepteApresAccordClient != null)
                if (this.nonConformeDerogationInterne.equals('O') || this.nonConformeDerogationInterne.equals('I'))
                    this.description += "derog. interne ";
                else if (this.nonConformeDerogationInterne.equals('B'))
                    this.description += "derog. Blue Whale ";

            this.description += this.nomResponsableDerogationInterne == null ? ""
                    : this.nomResponsableDerogationInterne + " ";
            this.description += this.commentairesControleur == null ? "" : this.commentairesControleur + " ";
        }

        // isExp
        Integer exp = -2;
        if (this.id == null)
            exp = expedition.getTypePaletteOK() && expedition.getEtatPaletteOK() && expedition.getPCFOK()
                    && expedition.getTypeColisOK() && expedition.getNombreColisOK() && expedition.getFichePaletteOK()
                    && expedition.getEtiquetteColisOK() && expedition.getLisibiliteEtiquetteColisOK()
                    && expedition.getTypeBoxEndLabelOK() && expedition.getLisibiliteBoxEndLabelOK()
                    && expedition.getTypeSacOK()
                    && expedition.getVarieteOK() && expedition.getNombreFruitsOK() && expedition.getTypeEtiquetteSacOK()
                    && expedition.getLisibiliteEtiquetteOK() && expedition.getNombreUCColisOK()
                    && expedition.getHomogeneiteColisOK() ? 1 : 0;
        this.isExp = exp == -2
                ? "N/A"
                : exp == 0 ? "OK" : "NON";

    }

    @Transient
    private GeoDocument cqTechnique;

    @Override
    public String getCqTechniqueName() {
        // ‘\\maddog2\geo_cq\' + control_cq_' + f_nvl_string(ls_CQL_REF) + '.pdf
        return String.format("control_cq_%s.pdf", this.getId());
    }

}
