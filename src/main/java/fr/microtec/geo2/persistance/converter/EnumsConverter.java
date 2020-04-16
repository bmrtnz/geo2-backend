package fr.microtec.geo2.persistance.converter;

import fr.microtec.geo2.persistance.entity.tiers.GeoModeLivraison;
import fr.microtec.geo2.persistance.entity.tiers.GeoNatureStation;

import javax.persistence.Converter;

public class EnumsConverter {
}

@Converter(autoApply = true)
class GeoModeLivraisonConverter extends AbstractStringEnumConverter<GeoModeLivraison> {
}

@Converter(autoApply = true)
class GeoNatureStationConverter extends AbstractStringEnumConverter<GeoNatureStation> {
}
