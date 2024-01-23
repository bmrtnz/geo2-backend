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
@Table(name = "geo_typcli")
@Entity
public class GeoTypeClient extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "tcl_code")
    private String id;

    @Column(name = "tcl_desc")
    private String description;

}
