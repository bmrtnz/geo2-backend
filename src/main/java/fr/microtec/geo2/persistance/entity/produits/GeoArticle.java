package fr.microtec.geo2.persistance.entity.produits;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import fr.microtec.geo2.persistance.repository.event.document.GeoAsDocument;
import fr.microtec.geo2.persistance.repository.event.document.GeoDocument;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateModifiedPrewrittedEntity;
import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueArticle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avi_art_gestion")
public class GeoArticle extends ValidateModifiedPrewrittedEntity implements Duplicable<GeoArticle>, GeoAsDocument {

	@Id
	@Column(name = "art_ref")
	@GeneratedValue(generator = "GeoArticleGenerator")
	@GenericGenerator(
			name = "GeoArticleGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "seq_art_num"),
					@Parameter(name = "mask", value = "FM000000")
			}
	)
	private String id;

	@Column(name = "art_alpha")
	private String description;

	@Deprecated
	@Column(name = "art_ref_ass")
	private String articleAssocie;

	@Column(name = "bwstock")
	private Boolean blueWhaleStock;

	@Column(name = "ger_code")
	private Character gerePar;

	@Column(name = "gtin_colis_bw")
	private String gtinColisBlueWhale;

	@Deprecated
	@Column(name = "gtin_palette_bw")
	private String gtinPaletteBlueWhale;

	@Column(name = "gtin_uc_bw")
	private String gtinUcBlueWhale;

	@Column(name = "maj_wms")
	private Integer majWms;

	@Column(name = "ins_station")
	private String instructionStation;

	@Deprecated
	@Column(name = "lf_ean_acheteur")
	private String lieuFonctionEanAcheteur;

	@Deprecated
	@Column(name = "pca_ref")
	private String procat;

	@Deprecated
	@Column(name = "pde_ref")
	private String prodet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_mat_prem")
	private GeoArticleMatierePremiere matierePremiere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_emballage")
	private GeoArticleEmballage emballage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_cdc")
	private GeoArticleCahierDesCharge cahierDesCharge;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_normalisation")
	private GeoArticleNormalisation normalisation;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "article")
	private List<GeoHistoriqueArticle> historique;

	public GeoArticle duplicate() {
		GeoArticle clone = new GeoArticle();
		clone.description = this.description;
		clone.articleAssocie = this.articleAssocie;
		clone.blueWhaleStock= this.blueWhaleStock;
		clone.gerePar = this.gerePar;
		clone.gtinPaletteBlueWhale = this.gtinPaletteBlueWhale;
		clone.majWms = this.majWms;
		clone.instructionStation = this.instructionStation;
		clone.procat = this.procat;
		clone.prodet = this.prodet;
		clone.lieuFonctionEanAcheteur = this.lieuFonctionEanAcheteur;
		clone.matierePremiere= this.matierePremiere;
		clone.cahierDesCharge = this.cahierDesCharge;
		clone.emballage = this.emballage;
		clone.normalisation = this.normalisation;
		if(this.historique != null)
			clone.historique = new ArrayList<GeoHistoriqueArticle>(this.historique);

		return clone;
	}

    @Transient
    private GeoDocument document;

    @Override
    public String getDocumentName() {
        return this.getId().concat(".pdf");
    }

    @Override
    public Maddog2FileSystemService.PATH_KEY getDocumentPathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_IMG;
    }
}
