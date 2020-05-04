package fr.microtec.geo2.persistance.entity.produits;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_grpvar")
public class GeoGroupeVariete extends ValidateAndModifiedEntity {

	@EmbeddedId
	private GeoGroupeVarieteId groupeVarieteId;

	@Column(name = "grv_desc")
	private String description;

	@Column(name = "previ_destockage")
	private Boolean suiviPrevisionDestockage;

	@OneToMany()
	@JoinColumn(name = "esp_code")
	@JoinColumn(name = "grv_code")
	private List<GeoVariete> varietes;

}
