package fr.microtec.geo2.persistance.entity.etiquette;

import fr.microtec.geo2.persistance.entity.produits.*;

/**
 * Interface a rajouter sur les entités qui peuvent avoir une étiquette de rattacher.
 */
public interface GeoAsEtiquette {

	String ETIQUETTE_STICKER    = "ETIFRU";
	String ETIQUETTE_CLIENT     = "ETICOL";
	String ETIQUETTE_EVENEMENT  = "ETIEVT";
	String ETIQUETTE_UC         = "ETIMB";

	// Getter utilisé par cette interface (besoin d'un id et d'une espesce)
	String getId();
	GeoEspece getEspece();

	// Getter / Setter pour ajouter l'objet etiquette sur les entités.
	void setEtiquette(GeoEtiquette etiquette);
	GeoEtiquette getEtiquette();

	// Permet de récupérer le prefix d'étiquette
	String getEtiquettePrefix();

	// Construit le nom de l'étiquette
	default String getEtiquetteName() {
		return String.format("%s_%s_%s", this.getEtiquettePrefix(), this.getEspece().getId(), this.getId());
	};

}
