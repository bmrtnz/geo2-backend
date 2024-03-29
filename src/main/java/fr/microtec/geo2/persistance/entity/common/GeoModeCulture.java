package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_mode_culture")
public class GeoModeCulture extends ValidateEntity {

    @Id
    @Column(name = "ref")
    private Integer id;

    @Column(name = "libelle")
    private String description;

    @Column(name = "libelle_client")
    private String descriptionClient;

}
