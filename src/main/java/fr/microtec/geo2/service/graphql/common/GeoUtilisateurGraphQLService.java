package fr.microtec.geo2.service.graphql.common;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.repository.common.GeoUtilisateurRepository;
import fr.microtec.geo2.persistance.security.Geo2UserDetailsService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@GraphQLApi
public class GeoUtilisateurGraphQLService {

  @Autowired private GeoUtilisateurRepository utilisateurRepository;
  // @Autowired Geo2UserDetailsService userDetailsService;

	@GraphQLQuery
	public Optional<GeoUtilisateur> getUtilisateur(
			@GraphQLArgument(name = "nomUtilisateur") String nomUtilisateur,
			@GraphQLArgument(name = "motDePasse") String motDePasse,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
    return this.utilisateurRepository.findByNomUtilisateurAndMotDePasseAndValideIsTrue(nomUtilisateur, motDePasse);
	}
}
