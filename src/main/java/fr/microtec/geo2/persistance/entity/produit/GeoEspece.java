package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.common.GeoGenre;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_espece")
public class GeoEspece extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "esp_code")
	private String id;

	@Column(name = "esp_desc")
	private String description;

	@Column(name = "esp_deb_mm")
	private Integer moisDebutVente;

	@Column(name = "esp_fin_mm")
	private Integer moisFinVente;

	@Column(name = "pu_max")
	private Float prixUnitaireMax;

	@Column(name = "pu_min")
	private Float prixUnitaireMin;

	@Column(name = "valid_visu")
	private Boolean stationVisible;

	@Column(name = "var_douane")
	private String codeDouanier;

	@Column(name = "pub_web")
	private Boolean publicationWeb;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gen_code")
	private GeoGenre genre;



}
