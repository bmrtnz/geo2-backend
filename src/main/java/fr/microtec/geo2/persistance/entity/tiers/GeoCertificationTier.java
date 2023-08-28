package fr.microtec.geo2.persistance.entity.tiers;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "typ_tiers")
@Table(name = "GEO_CERTIFS_TIERS")
public class GeoCertificationTier {

	@Id
	@Column(name = "k_certifs_tiers")
	@GeneratedValue(generator = "GeoCertificationTierGenerator")
	@GenericGenerator(name = "GeoCertificationTierGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
			@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_k_certifs_tiers"),
			@org.hibernate.annotations.Parameter(name = "isSequence", value = "true")
	})
	private BigDecimal id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "certif")
	private GeoCertification certification;

}
