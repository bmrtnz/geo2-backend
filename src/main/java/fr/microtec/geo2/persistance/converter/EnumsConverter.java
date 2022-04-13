package fr.microtec.geo2.persistance.converter;

import javax.persistence.Converter;

import org.springframework.stereotype.Component;

import fr.microtec.geo2.controller.FsCommand;
import fr.microtec.geo2.controller.FsDocumentType;
import fr.microtec.geo2.persistance.entity.logistique.GeoPortType;
import fr.microtec.geo2.persistance.entity.ordres.GeoCahierDesCharges;
import fr.microtec.geo2.persistance.entity.ordres.GeoFactureAvoir;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreStatut;
import fr.microtec.geo2.persistance.entity.ordres.GeoStatus;
import fr.microtec.geo2.persistance.entity.ordres.GeoStatusGEO;
import fr.microtec.geo2.persistance.entity.tiers.GeoModeLivraison;
import fr.microtec.geo2.persistance.entity.tiers.GeoNatureStation;
import fr.microtec.geo2.persistance.entity.tiers.GeoRole;

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

@Converter(autoApply = true)
class GeoPortTypeConverter extends AbstractStringEnumConverter<GeoPortType> {
}

@Converter(autoApply = true)
class GeoStatusGEOConverter extends AbstractStringEnumConverter<GeoStatusGEO> {
}

@Converter(autoApply = true)
class GeoStatusConverter extends AbstractStringEnumConverter<GeoStatus> {
}

@Converter(autoApply = true)
class GeoOrdreStatutConverter extends AbstractStringEnumConverter<GeoOrdreStatut> {
}

@Converter(autoApply = true)
class GeoCahierDesChargesConverter extends AbstractStringEnumConverter<GeoCahierDesCharges> {
}

@Component
@Converter(autoApply = true)
class FsCommandConverter extends AbstractStringEnumConverter<FsCommand> {
}

@Component
@Converter(autoApply = true)
class FsDocumentTypeConverter extends AbstractStringEnumConverter<FsDocumentType> {
}