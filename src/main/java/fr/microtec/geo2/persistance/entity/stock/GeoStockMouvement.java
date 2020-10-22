package fr.microtec.geo2.persistance.entity.stock;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stomvt")
@Entity
public class GeoStockMouvement extends ValidateAndModifiedEntity {

	@Id
	@Column(name = "sto_ref")
	private String id;

	@Column(name = "mod_user")
	private String userModification;

	@Column(name = "mod_date")
	private LocalDateTime dateModification;

	@NotNull
	@Column(name = "valide", nullable = false)
	private Boolean valide;

}
