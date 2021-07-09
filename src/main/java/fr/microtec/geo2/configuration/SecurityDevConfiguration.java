package fr.microtec.geo2.configuration;

import fr.microtec.geo2.configuration.authentication.ApiAuthenticationEntryPoint;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationFailureHandler;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationSuccessHandler;
import fr.microtec.geo2.persistance.security.Geo2UserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Configure project security for dev.
 */
@Profile("dev")
@Configuration
@EnableWebSecurity
public class SecurityDevConfiguration extends SecurityConfiguration {

	public SecurityDevConfiguration(
			Geo2UserDetailsService userDetailsService,
			ApiAuthenticationEntryPoint authenticationEntryPoint,
			ApiAuthenticationSuccessHandler authSuccessHandler,
			ApiAuthenticationFailureHandler authFailureHandler) {
		super(userDetailsService, authenticationEntryPoint, authSuccessHandler, authFailureHandler);
	}

	/**
	 * Configure http authentication.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		http.headers().frameOptions().disable();
	}
}
