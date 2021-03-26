package fr.microtec.geo2.persistance.entity.common;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_campag")
public class GeoCampagne extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "cam_code")
	private String id;

	@Column(name = "cam_desc")
	private String description;

	@Column(name = "date_debut")
	private LocalDate dateDebut;

	@Column(name = "date_fin")
	private LocalDate dateFin;

}
