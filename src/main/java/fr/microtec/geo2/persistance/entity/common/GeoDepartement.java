package fr.microtec.geo2.persistance.entity.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "geo_dept")
public class GeoDepartement extends ModifiedEntity {

	@Id
	@Column(name = "k_dept")
	private Integer id;

	@Column(name = "lib_dept")
	private String libelle;

	@Column(name = "num_dept")
	private String numero;

}
