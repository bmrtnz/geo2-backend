package fr.microtec.geo2.persistance.entity.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GeoPrecalModelVariete {

    @Id
    @Column(name = "var_code")
    private String id;

    @Column(name = "var_desc")
    private String description;

}
