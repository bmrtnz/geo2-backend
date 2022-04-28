package fr.microtec.geo2.persistance.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;

import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class ValidateEntity implements Serializable {

	// @Generated(GenerationTime.INSERT) // Use this for select value after insert into database
	@Column(name = "valide")
	private Boolean valide;

}
