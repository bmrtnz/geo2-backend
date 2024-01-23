package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_genre")
public class GeoGenre extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "gen_code")
    private String id;

    @Column(name = "gen_desc")
    private String description;

}
