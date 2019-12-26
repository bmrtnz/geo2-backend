package fr.microtec.geo2.persistance.repository;

import fr.microtec.geo2.persistance.entity.GeoUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoUserRepository extends PagingAndSortingRepository<GeoUser, String> {

	Optional<GeoUser> findByNomUtilisateur(String username);

}
