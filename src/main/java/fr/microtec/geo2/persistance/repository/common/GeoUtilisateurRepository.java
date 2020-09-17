package fr.microtec.geo2.persistance.repository.common;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoUtilisateurRepository extends EntityGraphJpaRepository<GeoUtilisateur, String> {

	Optional<GeoUtilisateur> findByNomUtilisateur(String nomUtilisateur);
	Optional<GeoUtilisateur> findByNomUtilisateurAndMotDePasseAndValideIsTrue(String nomUtilisateur, String motDePasse);

}
