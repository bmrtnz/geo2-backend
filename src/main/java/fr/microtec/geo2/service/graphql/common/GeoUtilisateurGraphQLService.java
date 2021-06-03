package fr.microtec.geo2.service.graphql.common;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.repository.common.GeoUtilisateurRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import io.leangen.graphql.spqr.spring.autoconfigure.DefaultGlobalContext;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@GraphQLApi
public class GeoUtilisateurGraphQLService extends GeoAbstractGraphQLService<GeoUtilisateur, String> {

	private final AuthenticationManager authManager;

	public GeoUtilisateurGraphQLService(
		GeoUtilisateurRepository utilisateurRepository,
		AuthenticationManager authManager
	) {
		super(utilisateurRepository);
		this.authManager = authManager;
	}

	@GraphQLQuery
	public GeoUtilisateur getUtilisateur(
			@GraphQLArgument(name = "nomUtilisateur") String nomUtilisateur,
			@GraphQLArgument(name = "motDePasse") String motDePasse,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		// Authenticate user
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(nomUtilisateur, motDePasse);
		Authentication auth = authManager.authenticate(authReq);

		// Set in context
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);

		// Set context in session
		RequestContextHolder.currentRequestAttributes().getSessionId();
		HttpServletRequest request = ((DefaultGlobalContext<ServletWebRequest>) env.rootContext).getNativeRequest().getRequest();
		HttpSession session = request.getSession(true);
		session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

		return (GeoUtilisateur) auth.getPrincipal();
	}

	@GraphQLMutation
	@Secured("ROLE_USER")
	public GeoUtilisateur saveUtilisateur(GeoUtilisateur utilisateur) {
		SecurityContext sc = SecurityContextHolder.getContext();
		GeoUtilisateur currentUser = (GeoUtilisateur)sc.getAuthentication().getPrincipal();
		if(utilisateur.getNomUtilisateur() == null)
			throw new SecurityException("User creation is not permitted, entity key needed");
		if(!currentUser.getNomUtilisateur().equals(utilisateur.getNomUtilisateur()))
			throw new SecurityException("Can't mutate another user");
		return this.save(utilisateur);
	}
}
