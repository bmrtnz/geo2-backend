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
@Table(name = "geo_palett")
@Entity
public class GeoTypePalette extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "pal_code")
	private String id;

	@Column(name = "pal_desc")
	private String description;

	@Column(name = "pal_cons")
	private Boolean consigne;

	@Column(name = "gest_code")
	private String gestionnaireChep;

	@Column(name = "gest_ref")
	private String referenceChep;

	@Column(name = "ean_code")
	private String codeEan;

	@Column(name = "poids")
	private Integer poids;

	@Column(name = "dim_code")
	private Character dimensions;

	@Column(name = "satar")
	private Character dimensionsSatar;

}
