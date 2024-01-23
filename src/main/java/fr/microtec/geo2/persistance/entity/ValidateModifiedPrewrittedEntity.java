package fr.microtec.geo2.persistance.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class ValidateModifiedPrewrittedEntity extends ValidateAndModifiedEntity {

    @Column(name = "pre_saisie")
    private Boolean preSaisie;

}
