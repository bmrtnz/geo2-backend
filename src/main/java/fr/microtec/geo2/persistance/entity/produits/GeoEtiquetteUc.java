package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.document.GeoAsEtiquette;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_etipmb")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoEtiquetteUc extends ValidateAndModifiedEntity implements GeoAsEtiquette {

	@Id
	@Column(name = "etp_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "etp_desc")
	private String description;

	@Column(name = "etp_libvte")
	private String descriptionClient;

	@Transient
	private GeoDocument document;

	@Override
	public String getEtiquettePrefix() {
		return GeoAsEtiquette.ETIQUETTE_UC;
	}

	// TODO field : clf_code

}
