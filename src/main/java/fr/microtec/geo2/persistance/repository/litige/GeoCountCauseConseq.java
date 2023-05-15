package fr.microtec.geo2.persistance.repository.litige;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class GeoCountCauseConseq {

    @Id
    @Column(name = "rownum")
    BigDecimal rownum;

    @Column(name = "cause")
    BigDecimal cause;

    @Column(name = "consequence")
    BigDecimal consequence;
}
