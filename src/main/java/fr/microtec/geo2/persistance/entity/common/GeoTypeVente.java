package fr.microtec.geo2.persistance.entity.common;

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
@Table(name = "geo_typvte")
@Entity
public class GeoTypeVente extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "tvt_code")
	private String id;

	@Column(name = "tvt_desc")
	private String description;

	@Column(name = "normal_retrait")
	private Character typeRetrait;

	public static GeoTypeVente getDefault() {
		GeoTypeVente tv = new GeoTypeVente();
		tv.setId("F");
		return tv;
	}

}
