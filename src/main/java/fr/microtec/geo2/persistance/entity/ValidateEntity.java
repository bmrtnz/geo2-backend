package fr.microtec.geo2.persistance.entity;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class ValidateEntity {

	// @Generated(GenerationTime.INSERT) // Use this for select value after insert into database
	@Column(name = "valide")
	private Boolean valide;

}
