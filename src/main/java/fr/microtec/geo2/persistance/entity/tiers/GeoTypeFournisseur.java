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
@Entity
@Table(name = "geo_typfou")
public class GeoTypeFournisseur extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "type_fournisseur")
	private String id;

	@Column(name = "libelle")
	private String description;

}
