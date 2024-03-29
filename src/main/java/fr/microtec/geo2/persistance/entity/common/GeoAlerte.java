package fr.microtec.geo2.persistance.entity.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import fr.microtec.geo2.persistance.entity.ValidateEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_alert")
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class GeoAlerte extends ValidateEntity {

    @Id
    @Column(name = "k_alert")
    @GeneratedValue(generator = "GeoAlertGenerator")
    @GenericGenerator(name = "GeoAlertGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequenceName", value = "SEQ_K_ALERT"),
            @org.hibernate.annotations.Parameter(name = "isSequence", value = "true")
    })
    private BigDecimal id;

    @Column(name = "cod_typ_alert")
    private Character type;

    @Column(name = "ind_deroul")
    private Boolean deroulant;

    @Column(name = "dat_debut")
    private LocalDateTime dateDebut;

    @Column(name = "dat_fin")
    private LocalDateTime dateFin;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code")
    private GeoSecteur secteur;

    @CreatedBy
    @Column(name = "cre_user")
    private String userCreation;

    @CreatedDate
    @Column(name = "cre_dat")
    private LocalDateTime dateCreation;

}
