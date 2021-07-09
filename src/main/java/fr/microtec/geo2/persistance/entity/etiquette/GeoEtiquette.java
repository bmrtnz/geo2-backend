package fr.microtec.geo2.persistance.entity.etiquette;

import lombok.Data;

/**
 * Pojo pour connaitre la présence d'une étiquette sur une entité.
 */
@Data
public class GeoEtiquette {

	private Boolean isPresent;
	private String filename;

}
