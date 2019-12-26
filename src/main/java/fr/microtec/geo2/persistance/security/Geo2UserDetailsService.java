package fr.microtec.geo2.persistance.security;

import fr.microtec.geo2.persistance.entity.GeoUser;
import fr.microtec.geo2.persistance.repository.GeoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Geo2 spring user detail implementation.
 */
@Component
public class Geo2UserDetailsService implements UserDetailsService {

	private GeoUserRepository userRepository;

	@Autowired
	public Geo2UserDetailsService(GeoUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Get Spring user detail from username.
	 *
	 * @param username Username of user
	 * @return Spring user detail
	 * @throws UsernameNotFoundException If user not found by username
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<GeoUser> geoUserOptional = this.userRepository.findByNomUtilisateur(username);
		if (geoUserOptional.isEmpty()) {
			throw new UsernameNotFoundException("Username not found");
		}

		List<GrantedAuthority> grantedAuthorities = Geo2SecurityRoles.authoritiesFor(geoUserOptional.get());

		return toSpringSecurityUser(geoUserOptional.get(), grantedAuthorities);
	}

	/**
	 * Map GeoUser to UserDetail spring implementation.
	 *
	 * @param geoUser USer in database
	 * @param authorities Authorities of user
	 * @return User detail implementation
	 */
	private User toSpringSecurityUser(GeoUser geoUser, Collection<GrantedAuthority> authorities) {
		return new User(
				geoUser.getNomUtilisateur(),
				geoUser.getMotDePasse(),
				geoUser.isValide(),
				geoUser.isValide(),
				geoUser.isValide(),
				geoUser.isValide(),
				authorities
		);
	}
}
