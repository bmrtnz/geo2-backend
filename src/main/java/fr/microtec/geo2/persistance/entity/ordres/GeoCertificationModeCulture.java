package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_certif_modcult")
public class GeoCertificationModeCulture {

	@Id
	@Column(name = "k_certif")
	private Integer id;

	@Column(name = "cert_ment_leg")
	private String description;

	@Column(name = "type_cert")
	private String type;

}
