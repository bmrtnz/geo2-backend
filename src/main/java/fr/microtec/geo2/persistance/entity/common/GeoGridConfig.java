package fr.microtec.geo2.persistance.entity.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyGroup;

import javax.persistence.*;
import java.util.HashMap;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

	/*@ToString.Exclude
	@EqualsAndHashCode.Exclude*/
	// @Fetch(FetchMode.SELECT)
	// @Basic(fetch = FetchType.LAZY)
	/*@LazyGroup("lobs")*/
	@Lob
	@Column(name = "config", columnDefinition = "BLOB")
	private HashMap<String, Object> config;

}
