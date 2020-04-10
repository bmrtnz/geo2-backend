package fr.microtec.geo2.persistance;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor implementation.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

	/**
	 * Auditor logic.
	 *
	 * @return Optional user internal name
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return Optional.ofNullable(((GeoUtilisateur) authentication).getNomInterne());
	}

}
