package fr.microtec.geo2.persistance.entity.traductions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo_pays_desc_trad")
@IdClass(GeoPaysTraductionId.class)
public class GeoPaysTraduction extends ValidateAndModifiedEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_code")
    private GeoPays pays;

    @Id
    @Column(name = "lan_code")
    private String langue;

    @Column(name = "pays_desc")
    private String description;

}
