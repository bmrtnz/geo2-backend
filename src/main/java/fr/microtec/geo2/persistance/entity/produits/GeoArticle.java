package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueArticle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avi_art_gestion")
public class GeoArticle extends ValidateCreatedAndModifiedEntity {

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

}
