package fr.microtec.geo2.configuration;

import fr.microtec.geo2.configuration.authentication.ApiAuthenticationEntryPoint;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationFailureHandler;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationSuccessHandler;
import fr.microtec.geo2.service.security.GeoLdapUserDetailsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Configure project security for dev.
 */
@Profile("dev")
@Configuration
@EnableWebSecurity
public class SecurityDevConfiguration extends SecurityConfiguration {

	@Value("${spring.dev.auth-with-ldap:false}")
	private Boolean authWithLdap;

	public SecurityDevConfiguration(
			GeoLdapUserDetailsMapper geoLDAPUserDetailsMapper,
			ApiAuthenticationEntryPoint authenticationEntryPoint,
			ApiAuthenticationSuccessHandler authSuccessHandler,
			ApiAuthenticationFailureHandler authFailureHandler,
			LdapContextSource ldapContextSource) {
		super(geoLDAPUserDetailsMapper, authenticationEntryPoint, authSuccessHandler, authFailureHandler, ldapContextSource);
	}

	/**
	 * Configure http authentication.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		http.headers().frameOptions().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (this.authWithLdap) {
			super.configure(auth);
			return;
		}

		auth.userDetailsService(this.geoLDAPUserDetailsMapper.getGeo2UserDetailsService());
	}
}
