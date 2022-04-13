package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "geo_typord")
@Entity
public class GeoTypeOrdre {
	@Id
	@Column(name = "typ_ord")
	private String id;

	@Column(name = "typ_desc")
	private String description;
}
