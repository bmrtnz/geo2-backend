package fr.microtec.geo2.persistance.repository.traductions;

import fr.microtec.geo2.persistance.entity.traductions.GeoVarieteTraduction;
import fr.microtec.geo2.persistance.entity.traductions.GeoVarieteTraductionId;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoVarieteTraductionRepository extends GeoRepository<GeoVarieteTraduction, GeoVarieteTraductionId> {
}
