package fr.microtec.geo2.persistance.entity.common;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_typtie")
public class GeoTypeTiers extends ValidateAndModifiedEntity {

  @Id
  @Column(name = "tyt_code")
  private Character id;

  @Column(name = "tyt_desc")
  private String description;

  @Column(name = "resp_litige")
  private Boolean responsableLitige;

  @OneToMany(mappedBy = "typeTiers")
  @Where(clause = "typ_tiers = '" + GeoClient.TYPE_TIERS + "'")
	private List<GeoClient> clients;

}
