package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_rangem")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoRangement extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "ran_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "ran_desc")
	private String description;

	@Column(name = "ran_libvte")
	private String descriptionClient;

}
