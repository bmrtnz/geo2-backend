package fr.microtec.geo2.persistance.entity.tiers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_incote")
@Entity
public class GeoIncoterm extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "inc_code")
    private String id;

    @Column(name = "inc_desc")
    private String description;

    @Column(name = "inc_rd")
    private Character renduDepart;

    @Column(name = "inc_yalieu")
    private Boolean lieu;

}
