package fr.microtec.geo2.persistance.entity.common;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "geo_grid_config")
@IdClass(GeoGridConfigKey.class)
@Entity
public class GeoGridConfig extends ModifiedEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur")
    private GeoUtilisateur utilisateur;

    @Id
    @Column(name = "grid")
    private String grid;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soc_code")
    private GeoSociete societe;

    @Lob
    @Column(name = "config", columnDefinition = "BLOB")
    private HashMap<String, Object> config;

}
