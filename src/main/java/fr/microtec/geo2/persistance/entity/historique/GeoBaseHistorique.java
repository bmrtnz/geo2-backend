package fr.microtec.geo2.persistance.entity.historique;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@MappedSuperclass
public class GeoBaseHistorique {

	@Column(name = "comm_histo")
	private String commentaire;

	@Column(name = "mod_user")
	private String userModification;

	@Column(name = "mod_date")
	private String dateModification;

	@NotNull
	@Column(name = "valide", nullable = false)
	private Boolean valide;

}
