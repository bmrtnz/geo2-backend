package fr.microtec.geo2.persistance.entity.tiers;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "typ_tiers")
@Table(name = "GEO_CERTIFS_TIERS")
public class GeoCertificationTier {

	@Id
	@Column(name = "k_certifs_tiers")
	@GeneratedValue(generator = "GeoCertificationTierGenerator")
	@GenericGenerator(
			name = "GeoCertificationTierGenerator",
			strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator",
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_k_certifs_tiers"),
					@org.hibernate.annotations.Parameter(name = "isSequence", value = "true")
			}
	)
	private BigDecimal id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "certif")
	private GeoCertification certification;

}
