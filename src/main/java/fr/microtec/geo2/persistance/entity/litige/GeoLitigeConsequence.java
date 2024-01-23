package fr.microtec.geo2.persistance.entity.litige;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_litcon")
@Entity
public class GeoLitigeConsequence extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "lcq_code")
    private String id;

    @Column(name = "lcq_desc")
    private String description;

}
