package fr.microtec.geo2.persistance.entity.traductions;

import fr.microtec.geo2.persistance.entity.ValidateCreatedAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoPays;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "geo_pays_desc_trad")
@IdClass(GeoPaysTraductionId.class)
public class GeoPaysTraduction extends ValidateCreatedAndModifiedEntity {

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
