package fr.microtec.geo2.configuration;

import fr.microtec.geo2.configuration.authentication.ApiAuthenticationEntryPoint;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationFailureHandler;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationSuccessHandler;
import fr.microtec.geo2.service.security.GeoLdapUserDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Configure project security.
 */
@Profile("!(dev | devbw)")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	protected final GeoLdapUserDetailsMapper geoLDAPUserDetailsMapper;
	private final ApiAuthenticationEntryPoint authenticationEntryPoint;
	private final AuthenticationSuccessHandler authSuccessHandler;
	private final AuthenticationFailureHandler authFailureHandler;
	private final LdapContextSource ldapContextSource;

	@Autowired
	public SecurityConfiguration(
			GeoLdapUserDetailsMapper geoLDAPUserDetailsMapper, ApiAuthenticationEntryPoint authenticationEntryPoint,
			ApiAuthenticationSuccessHandler authSuccessHandler,
			ApiAuthenticationFailureHandler authFailureHandler,
			LdapContextSource ldapContextSource) {
		this.geoLDAPUserDetailsMapper = geoLDAPUserDetailsMapper;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.authSuccessHandler = authSuccessHandler;
		this.authFailureHandler = authFailureHandler;
		this.ldapContextSource = ldapContextSource;
	}

	/**
	 * Configure http authentication.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//http.authorizeRequests().antMatchers("/*").permitAll();
		http.authorizeRequests().anyRequest().permitAll();
		/*http.authorizeRequests().antMatchers("/graphql").permitAll();
		http.authorizeRequests().antMatchers("/*").permitAll();
		http.authorizeRequests().anyRequest().authenticated();*/
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

		http.formLogin().successHandler(authSuccessHandler).failureHandler(authFailureHandler);

		http.logout().permitAll();

		http.cors();
		http.csrf().disable();

		http.headers().frameOptions().sameOrigin();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.ldapAuthentication()
				.userSearchFilter("SamAccountName={0}")
				.userDetailsContextMapper(this.geoLDAPUserDetailsMapper)
				.contextSource(this.ldapContextSource);
	}

	@Bean
	public PasswordEncoder encoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
}
