package fr.microtec.geo2.persistance.entity.stock;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoEspece;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class GeoBaseStock extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "sto_ref")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fou_code", referencedColumnName = "fou_code")
    private GeoFournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_code")
    private GeoEspece espece;

}
