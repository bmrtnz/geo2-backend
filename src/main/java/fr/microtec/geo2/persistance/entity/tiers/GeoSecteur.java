package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_seccom")
@Entity
public class GeoSecteur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "sco_code")
	private String id;

	@Column(name = "sco_desc")
	private String description;

	@Column(name = "soc_liste")
	private String societes;

}
