package fr.microtec.geo2.persistance.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ValidateAndModifiedEntity extends ValidateEntity {

    @LastModifiedBy
    @Column(name = "mod_user")
    private String userModification;

    @LastModifiedDate
    @Column(name = "mod_date")
    private LocalDateTime dateModification;

}
