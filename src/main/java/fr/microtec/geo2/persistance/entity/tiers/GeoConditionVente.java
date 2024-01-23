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
@Table(name = "geo_conven")
@Entity
public class GeoConditionVente extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "cov_code")
    private String id;

    @Column(name = "cov_desc")
    private String description;

}
