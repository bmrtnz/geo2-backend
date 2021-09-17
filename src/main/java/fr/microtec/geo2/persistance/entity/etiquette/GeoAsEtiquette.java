package fr.microtec.geo2.persistance.entity.etiquette;

import fr.microtec.geo2.persistance.entity.produits.*;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.Path;

/**
 * Interface a rajouter sur les entités qui peuvent avoir une étiquette de rattacher.
 */
public interface GeoAsEtiquette extends GeoAsDocument {

	String ETIQUETTE_STICKER    = "ETIFRU";
	String ETIQUETTE_CLIENT     = "ETICOL";
	String ETIQUETTE_EVENEMENT  = "ETIEVT";
	String ETIQUETTE_UC         = "ETIMB";

	// Getter utilisé par cette interface (besoin d'un id et d'une espesce)
	String getId();
	GeoEspece getEspece();

	// Permet de récupérer le prefix d'étiquette
	String getEtiquettePrefix();

	// Construit le nom de l'étiquette
	default String getDocumentName() {
		// return "ETICOL_POMME_ALDIVL";
		return String.format("%s_%s_%s", this.getEtiquettePrefix(), this.getEspece().getId(), this.getId());
	}

	default Path getDocumentWithMaddogService(Maddog2FileSystemService fileSystemService) {
		return fileSystemService.getEtiquette(this.getDocumentName());
	}

}