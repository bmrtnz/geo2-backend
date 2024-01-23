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
@Entity
@Table(name = "geo_typfou")
public class GeoTypeFournisseur extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "type_fournisseur")
    private String id;

    @Column(name = "libelle")
    private String description;

}
