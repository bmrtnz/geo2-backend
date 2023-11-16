package fr.microtec.geo2.persistance.entity.common;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GEO_ALERT")
public class GeoAlerte extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "K_ALERT")
    @GeneratedValue(generator = "GeoAlertGenerator")
    @GenericGenerator(name = "GeoAlertGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequenceName", value = "SEQ_K_ALERT"),
            @org.hibernate.annotations.Parameter(name = "isSequence", value = "true")
    })
    private BigDecimal id;

    @Column(name = "COD_TYP_ALERT")
    private Character type;

    @Column(name = "IND_DEROUL")
    private Boolean deroulant;

    @Column(name = "DAT_DEBUT")
    private LocalDate dateDebut;

    @Column(name = "dat_fin")
    private LocalDate dateFin;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code")
    private GeoSecteur secteur;

    @CreatedBy
    @Column(name = "cre_user")
    private String userCreation;

    @CreatedDate
    @Column(name = "cre_date")
    private LocalDateTime dateCreation;

}
