package fr.microtec.geo2.persistance.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
