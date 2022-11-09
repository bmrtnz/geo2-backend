package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

import fr.microtec.geo2.persistance.entity.document.GeoAsCQPhoto;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import lombok.Data;

@Entity
@Table(name = "geo_docnum")
@Data
@IdClass(GeoDocumentNumKey.class)
@DynamicUpdate
public class GeoDocumentNum implements GeoAsCQPhoto {

    @Id
    @Column(name = "ord_num")
    protected String ordreNumero;

    @Id
    @Column(name = "typ_doc")
    protected String typeDocument;

    @Id
    @Column(name = "cre_an")
    protected String anneeCreation;

    @Column(name = "cre_mois")
    protected String moisCreation;

    @Column(name = "nom_partage")
    protected String nomPartage;

    @Column(name = "id")
    protected String id;

    @Column(name = "nordre")
    protected String numeroOrdre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orl_ref")
    protected GeoOrdreLigne ordreLigne;

    @Column(name = "nom_fic")
    protected String nomFichier;

    @Column(name = "nom_fic_complet")
    protected String nomFichierComplet;

    @Column(name = "commentaire")
    protected String commentaire;

    @Column(name = "flpdf")
    protected Boolean flagPDF;

    @Column(name = "status")
    protected Integer statut;

    @Transient
    private GeoDocument cqPhoto;

    @Override
    public String getCqPhotoPath() {
        return String.format("%s/%s/%s", this.getAnneeCreation(), this.getMoisCreation(), this.getNomFichier());
    }
}
