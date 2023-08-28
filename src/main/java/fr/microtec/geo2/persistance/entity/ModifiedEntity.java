package fr.microtec.geo2.persistance.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class ModifiedEntity {

	@LastModifiedBy
	@Column(name = "mod_user")
	private String userModification;

	@LastModifiedDate
	@Column(name = "mod_date")
	private LocalDateTime dateModification;

}
