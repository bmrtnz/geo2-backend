package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_frais")
@Entity
public class GeoFrais extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "fra_code")
    private String id;

    @NotNull
    @Column(name = "fra_desc", nullable = false)
    private String description;

}
