package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.etiquette.DocumentAuditingListener;
import fr.microtec.geo2.persistance.entity.etiquette.GeoAsEtiquette;
import fr.microtec.geo2.persistance.entity.etiquette.GeoDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_etievt")
@IdClass(GeoProduitWithEspeceId.class)
@EntityListeners(DocumentAuditingListener.class)
public class GeoEtiquetteEvenementielle extends ValidateAndModifiedEntity implements GeoAsEtiquette {

	@Id
	@Column(name = "etv_code")
	private String id;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
	private GeoEspece espece;

	@Column(name = "etv_desc")
	private String description;

	@Column(name = "etv_libvte")
	private String descriptionClient;

	@Transient
	private GeoDocument document;

	@Override
	public String getEtiquettePrefix() {
		return GeoAsEtiquette.ETIQUETTE_EVENEMENT;
	}

	// TODO field : clf_code

}
