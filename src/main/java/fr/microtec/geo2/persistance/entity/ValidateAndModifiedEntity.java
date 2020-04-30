package fr.microtec.geo2.persistance.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class ValidateAndModifiedEntity extends ValidateEntity {

	@LastModifiedBy
	@Column(name = "mod_user")
	private String modifiedBy;

	@LastModifiedDate
	@Column(name = "mod_date")
	private LocalDateTime modifiedAt;

}
