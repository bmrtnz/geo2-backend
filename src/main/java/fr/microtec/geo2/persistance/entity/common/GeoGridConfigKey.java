package fr.microtec.geo2.persistance.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoGridConfigKey implements Serializable {

	protected String utilisateur;
	protected String grid;

}
