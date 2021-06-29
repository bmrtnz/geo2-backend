package fr.microtec.geo2.persistance.entity.ordres;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoCommentaireOrdreKey implements Serializable {

	protected String id;
	protected String ordre;

}
