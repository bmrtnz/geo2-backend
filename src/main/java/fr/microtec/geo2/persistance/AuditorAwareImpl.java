package fr.microtec.geo2.persistance;

import fr.microtec.geo2.service.security.SecurityService;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor implementation.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

	private final SecurityService securityService;

	public AuditorAwareImpl(SecurityService securityService) {
		this.securityService = securityService;
	}

	/**
	 * Auditor logic.
	 *
	 * @return Optional user internal name
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.ofNullable(this.securityService.getUser().getNomInterne());
	}

}
