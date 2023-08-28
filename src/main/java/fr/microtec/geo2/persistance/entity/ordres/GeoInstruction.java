package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_instruc")
@Entity
public class GeoInstruction extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "ins_code")
    private String id;

    @Column(name = "ins_desc")
    private String description;

}
