package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidableAndModifiableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_contac")
@Entity
public class GeoContact extends ValidableAndModifiableEntity {

	@Id
	@Column(name = "con_ref")
	@GeneratedValue(generator = "GeoContactsGenerator")
	@GenericGenerator(
			name = "GeoContactsGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@Parameter(name = "sequenceName", value = "seq_con_num"),
					@Parameter(name = "mask", value = "FM099999")
			}
	)
	private String id;

	@Column(name = "con_prenom")
	private String prenom;

	@Column(name = "con_nom")
	private String nom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flu_code")
	private GeoFlux flux;

	@Column(name = "con_fluvar")
	private String fluxComplement;

	@Column(name = "con_acces1")
	private String fluxAccess1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
	private GeoSociete societe;

}
