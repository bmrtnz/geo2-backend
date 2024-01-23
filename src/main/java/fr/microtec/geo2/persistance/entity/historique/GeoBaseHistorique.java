package fr.microtec.geo2.persistance.entity.historique;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class GeoBaseHistorique extends ValidateAndModifiedEntity {

    @Column(name = "comm_histo")
    private String commentaire;

}
