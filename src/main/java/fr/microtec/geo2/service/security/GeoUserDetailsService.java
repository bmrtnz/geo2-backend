package fr.microtec.geo2.service.security;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.repository.common.GeoUtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Geo2 spring user detail implementation.
 */
@Service
public class GeoUserDetailsService implements UserDetailsService {

	private final GeoUtilisateurRepository utilisateurRepository;

	@Autowired
	public GeoUserDetailsService(GeoUtilisateurRepository userRepository) {
		this.utilisateurRepository = userRepository;
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
		Optional<GeoUtilisateur> geoUserOptional = this.utilisateurRepository.findByNomUtilisateur(username);
		if (geoUserOptional.isEmpty()) {
			throw new UsernameNotFoundException("Username not found");
		}

		return geoUserOptional.get();
	}

}
