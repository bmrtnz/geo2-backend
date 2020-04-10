package fr.microtec.geo2.persistance.converter;

import fr.microtec.geo2.persistance.entity.tiers.GeoModeLivraison;

import javax.persistence.Converter;

public class EnumsConverter {
}

@Converter(autoApply = true)
class GeoModeLivraisonConverter extends AbstractStringEnumConverter<GeoModeLivraison> {
	public GeoModeLivraisonConverter() {
		super(GeoModeLivraison.class);
	}
}
