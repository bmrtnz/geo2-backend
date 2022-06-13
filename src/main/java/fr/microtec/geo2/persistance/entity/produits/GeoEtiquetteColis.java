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
@Table(name = "geo_eticol")
@IdClass(GeoProduitWithEspeceId.class)
public class GeoEtiquetteColis extends ValidateAndModifiedEntity implements GeoAsEtiquette {

	@Id
	@Column(name = "etc_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "etc_desc")
	private String description;

	@Column(name = "etc_libvte")
	private String descriptionClient;

	@Column(name = "clf_code")
	public String codeClient;

	@Transient
	private GeoDocument document;

	@Override
	public String getEtiquettePrefix() {
		return GeoAsEtiquette.ETIQUETTE_CLIENT;
	}

	// TODO field : ref_codesoft

}
