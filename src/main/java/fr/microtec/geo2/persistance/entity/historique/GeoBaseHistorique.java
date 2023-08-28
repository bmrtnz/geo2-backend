package fr.microtec.geo2.persistance.entity.historique;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class GeoBaseHistorique extends ValidateAndModifiedEntity {

	@Column(name = "comm_histo")
	private String commentaire;

}
