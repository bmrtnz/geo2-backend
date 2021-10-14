package fr.microtec.geo2.persistance.entity.tiers;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
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

}
