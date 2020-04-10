package fr.microtec.geo2.persistance.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class ValidableEntity {

	@Column(name = "valide")
	private Boolean valide;

}
