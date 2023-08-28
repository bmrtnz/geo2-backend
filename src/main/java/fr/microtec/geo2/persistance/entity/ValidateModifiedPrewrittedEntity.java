package fr.microtec.geo2.persistance.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class ValidateModifiedPrewrittedEntity extends ValidateAndModifiedEntity {

    @Column(name = "pre_saisie")
    private Boolean preSaisie;

}
