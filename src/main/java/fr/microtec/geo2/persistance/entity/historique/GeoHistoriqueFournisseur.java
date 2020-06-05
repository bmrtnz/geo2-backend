package fr.microtec.geo2.persistance.entity.historique;

import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_histo_fourni")
@Entity
public class GeoHistoriqueFournisseur extends GeoBaseHistorique implements GeoHistorique {

	@Id
	@Column(name = "histo_fou_code")
	@GeneratedValue(generator = "GeoHistoriqueFournisseurGenerator")
	@GenericGenerator(
			name = "GeoHistoriqueFournisseurGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "seq_histo_fourni"),
					@Parameter(name = "mask", value = "FM099999")
			}
	)
	private String id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code", nullable = false)
	private GeoFournisseur fournisseur;

}
