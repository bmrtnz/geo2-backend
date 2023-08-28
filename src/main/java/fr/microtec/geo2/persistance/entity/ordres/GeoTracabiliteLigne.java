package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_traca_ligne")
@Entity
public class GeoTracabiliteLigne extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "ref_traca_ligne")
	private Integer id;

	@Column(name = "arbo_code")
	private String arboCode;

	@Column(name = "nb_colis")
	private Integer nombreColis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orl_ref")
	private GeoOrdreLigne ordreLigne;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_traca")
	private GeoTracabiliteDetailPalette tracabiliteDetailPalette;

}
