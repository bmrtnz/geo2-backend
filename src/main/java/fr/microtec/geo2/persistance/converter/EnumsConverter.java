package fr.microtec.geo2.persistance.converter;

import fr.microtec.geo2.persistance.entity.ordres.GeoFactureAvoir;
import fr.microtec.geo2.persistance.entity.tiers.GeoModeLivraison;
import fr.microtec.geo2.persistance.entity.tiers.GeoNatureStation;
import fr.microtec.geo2.persistance.entity.tiers.GeoRole;

import javax.persistence.Converter;

public class EnumsConverter {
}

@Converter(autoApply = true)
class GeoModeLivraisonConverter extends AbstractStringEnumConverter<GeoModeLivraison> {
}

@Converter(autoApply = true)
class GeoNatureStationConverter extends AbstractStringEnumConverter<GeoNatureStation> {
}

@Converter(autoApply = true)
class GeoFactureAvoirConverter extends AbstractStringEnumConverter<GeoFactureAvoir> {
}

@Converter(autoApply = true)
class GeoRoleConverter extends AbstractStringEnumConverter<GeoRole> {
}
