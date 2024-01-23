package fr.microtec.geo2.persistance.entity.tiers;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "geo_pays")
@Entity
public class GeoPays extends ValidateAndModifiedEntity {

    @Id
    @Column(name = "pay_code")
    private String id;

    @Column(name = "pay_desc")
    private String description;

    @Column(name = "pay_numiso")
    private String numeroIso;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pays")
    private List<GeoClient> clients;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code", nullable = false)
    private GeoSecteur secteur;

}
