package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_variet")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoVariete extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "var_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "var_desc")
	private String description;

	@Column(name = "var_ctifl")
	private Boolean soumisCtifl;

	@Column(name = "var_ristourne")
	private Boolean soumisRistourne;

	@Column(name = "var_douane")
	private String codeProduitDouane;

	@Column(name = "var_plu")
	private String plu;

	@Column(name = "frais_pu")
	private Float fraisPu;

	@Column(name = "pub_web")
	private Boolean publicationWeb;

	@Column(name = "stock_preca")
	private Boolean stockPreca;

	@Column(name = "perequ")
	private Boolean prerequation;

	@Column(name = "ach_pu_mini")
	private Float prixUnitaireMin;

	@Column(name = "pu_max")
	private Float prixUnitaireMax;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "esp_code"),
			@JoinColumn(name = "grv_code")
	})
	private GeoGroupeVariete groupe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "frais_unite")
	private GeoBaseTarif baseTarif;

}
