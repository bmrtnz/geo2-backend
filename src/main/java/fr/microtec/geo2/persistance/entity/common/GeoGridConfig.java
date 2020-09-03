package fr.microtec.geo2.persistance.entity.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_grid_config")
@IdClass(GeoGridConfigKey.class)
@Entity
public class GeoGridConfig {
	
	@Id
  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "utilisateur")
	private GeoUtilisateur utilisateur;

	@Id
	@Column(name = "grid")
	private String grid;

  @Lob()
	@Column(name = "config", columnDefinition="BLOB")
	private HashMap<String,Object> config;

}
