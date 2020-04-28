package fr.microtec.geo2.persistance.entity.produit;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_caluni")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoCalibreUnifie extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "cun_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "cun_desc")
	private String description;

	@Column(name = "cug_code")
	private String groupe;


}
