package fr.microtec.geo2.persistance.repository.common;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoUtilisateurRepository extends GeoRepository<GeoUtilisateur, String> {

	Optional<GeoUtilisateur> findByNomUtilisateur(String nomUtilisateur);
	Optional<GeoUtilisateur> findByNomUtilisateurAndMotDePasseAndValideIsTrue(String nomUtilisateur, String motDePasse);

}
