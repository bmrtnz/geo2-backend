package fr.microtec.geo2.persistance.entity.historique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_histo_client")
@Entity
public class GeoHistoriqueClient extends GeoBaseHistorique {

	@Id
	@Column(name = "histo_cli_ref")
	@GeneratedValue(generator = "GeoHistoriqueClientGenerator")
	@GenericGenerator(
			name = "GeoHistoriqueClientGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "seq_histo_cli"),
					@Parameter(name = "mask", value = "FM099999")
			}
	)
	private String id;

	@NotNull
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_ref", nullable = false)
	private GeoClient client;

}
