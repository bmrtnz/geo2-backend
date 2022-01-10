package fr.microtec.geo2.service.graphql.common;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.repository.common.GeoUtilisateurRepository;
import fr.microtec.geo2.service.security.SecurityException;
import fr.microtec.geo2.service.security.SecurityService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.leangen.graphql.spqr.spring.autoconfigure.DefaultGlobalContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

@Service
@GraphQLApi
public class GeoUtilisateurGraphQLService extends GeoAbstractGraphQLService<GeoUtilisateur, String> {

	private final SecurityService securityService;

	public GeoUtilisateurGraphQLService(
			GeoUtilisateurRepository utilisateurRepository,
			SecurityService securityService) {
		super(utilisateurRepository, GeoUtilisateur.class);
		this.securityService = securityService;
	}

	@GraphQLQuery
	public GeoUtilisateur getUtilisateur(
			@GraphQLArgument(name = "nomUtilisateur") String nomUtilisateur,
			@GraphQLArgument(name = "motDePasse") String motDePasse,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		// Authenticate user
		return this.securityService.login(nomUtilisateur, motDePasse, ((DefaultGlobalContext<ServletWebRequest>) env.rootContext).getNativeRequest().getRequest());
	}

	@GraphQLMutation
	@Secured("ROLE_USER")
	public GeoUtilisateur saveUtilisateur(GeoUtilisateur utilisateur) {
		GeoUtilisateur currentUser = this.securityService.getUser();

		if(utilisateur.getNomUtilisateur() == null)
			throw new SecurityException("User creation is not permitted, entity key needed");

		if(!currentUser.getNomUtilisateur().equals(utilisateur.getNomUtilisateur()))
			throw new SecurityException("Can't mutate another user");

		return this.save(utilisateur);
	}
}
