package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_alveol")
public class GeoAlveole extends ValidateAndModifiedEntity {

	@EmbeddedId
	private GeoAlveoleId alveoleId;

	@Column(name = "alv_desc")
	private String description;

	@Column(name = "alv_libvte")
	private String descriptionClient;

}
