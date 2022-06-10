package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.repository.event.document.GeoAsEtiquette;
import fr.microtec.geo2.persistance.repository.event.document.GeoDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_etifru")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoStickeur extends ValidateAndModifiedEntity implements GeoAsEtiquette {

	@Id
	@Column(name = "etf_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "etf_desc")
	private String description;

	@Column(name = "etf_libvte")
	private String descriptionClient;

	@Transient
	private GeoDocument document;

	@Override
	public String getEtiquettePrefix() {
		return GeoAsEtiquette.ETIQUETTE_STICKER;
	}

	// TODO field : clf_code

}
