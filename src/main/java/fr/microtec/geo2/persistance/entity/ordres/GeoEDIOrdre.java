package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Table(name = "geo_edi_ordre")
@Entity
public class GeoEDIOrdre {

    @Id
	@Column(name = "ref_edi_ordre")
	private Integer id;

    @NotNull
	@Column(name = "status_geo", nullable = false)
	private GeoStatusGEO statusGEO;

	@Column(name = "status")
	private GeoStatus status;

}
