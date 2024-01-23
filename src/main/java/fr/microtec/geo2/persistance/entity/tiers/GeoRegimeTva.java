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
@Table(name = "geo_tvareg")
@Entity
public class GeoRegimeTva extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "tvr_code")
    private String id;

    @Column(name = "tvr_desc")
    private String description;

}
