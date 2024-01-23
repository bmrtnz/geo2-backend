package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_imprim")
@Entity
public class GeoImprimante extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "imp_ref")
    private String id;

    @Column(name = "imp_desc")
    private String description;

    @Column(name = "imp_id")
    private String reference;

    @Column(name = "imp_locale")
    private String nomLocal;

    @Column(name = "ip_v4")
    private String ipv4;

}
