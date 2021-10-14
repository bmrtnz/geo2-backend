package fr.microtec.geo2.persistance.entity.historique;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class GeoBaseHistorique extends ValidateAndModifiedEntity {

	@Column(name = "comm_histo")
	private String commentaire;

}
