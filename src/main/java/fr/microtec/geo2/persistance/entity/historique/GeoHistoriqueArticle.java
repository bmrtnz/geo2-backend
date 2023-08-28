package fr.microtec.geo2.persistance.entity.historique;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_histo_article")
@Entity
public class GeoHistoriqueArticle extends GeoBaseHistorique {

	@Id
	@Column(name = "histo_art_ref")
	@GeneratedValue(generator = "GeoHistoriqueArticleGenerator")
	@GenericGenerator(name = "GeoHistoriqueArticleGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
			@Parameter(name = "sequenceName", value = "seq_histo_art"),
			@Parameter(name = "mask", value = "FM099999")
	})
	private String id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "art_ref", nullable = false)
	private GeoArticle article;

}
