package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_campag")
public class GeoTypeTiers extends ValidateAndModifiedEntity {

  @Id
  @Column(name = "tyt_code")
  private Character id;

  @Column(name = "tyt_desc")
  private String description;

  @Column(name = "resp_litige")
  private Boolean responsableLitige;

}
