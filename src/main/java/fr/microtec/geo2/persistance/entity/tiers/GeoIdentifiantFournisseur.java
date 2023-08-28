package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ident_fou")
@Entity
public class GeoIdentifiantFournisseur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "ref")
	private Integer id;

	@Column(name = "libelle")
	private String libelle;

}
