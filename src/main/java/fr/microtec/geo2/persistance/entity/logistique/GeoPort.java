package fr.microtec.geo2.persistance.entity.logistique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_port")
public class GeoPort extends ModifiedEntity {
    @Id
    @Column(name = "por_id")
    private String id;

    @Column(name = "por_name")
    private String name;

    @Column(name = "por_type")
    private GeoPortType type;

    @Column(name = "tyt_code")
    private Character typeTiers;

    @Column(name = "por_valide")
    private Boolean valide;

    @Column(name = "unlocode")
    private String universalLocationCode;
}
