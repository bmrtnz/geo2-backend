package fr.microtec.geo2.persistance.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class ValidateCreatedAndModifiedEntity extends ValidateAndModifiedEntity {

	@CreatedBy
	@Column(name = "cre_user")
	private String userCreation;

	@CreatedDate
	@Column(name = "cre_date")
	private LocalDateTime dateCreation;

}
