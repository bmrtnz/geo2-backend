package fr.microtec.geo2.persistance.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class ValidateCreatedAndModifiedEntity extends ValidateAndModifiedEntity {

	@CreatedBy
	@Column(name = "cre_user")
	private String createdBy;

	@CreatedDate
	@Column(name = "cre_date")
	private LocalDateTime createdAt;

}
