package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_ardesc")
@Entity
public class GeoRaisonAnnuleRemplace {

    @Id
    @Column(name = "ard_desc")
    private String description;

}
