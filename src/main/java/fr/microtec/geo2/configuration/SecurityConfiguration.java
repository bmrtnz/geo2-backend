package fr.microtec.geo2.configuration;

import fr.microtec.geo2.configuration.authentication.ApiAuthenticationEntryPoint;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationFailureHandler;
import fr.microtec.geo2.configuration.authentication.ApiAuthenticationSuccessHandler;
import fr.microtec.geo2.persistance.security.Geo2UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final Geo2UserDetailsService userDetailsService;
	private final ApiAuthenticationEntryPoint authenticationEntryPoint;
	private final AuthenticationSuccessHandler authSuccessHandler;
	private final AuthenticationFailureHandler authFailureHandler;

	@Autowired
	public SecurityConfiguration(
			Geo2UserDetailsService userDetailsService,
			ApiAuthenticationEntryPoint authenticationEntryPoint,
			ApiAuthenticationSuccessHandler authSuccessHandler,
			ApiAuthenticationFailureHandler authFailureHandler
	) {
		this.userDetailsService = userDetailsService;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.authSuccessHandler = authSuccessHandler;
		this.authFailureHandler = authFailureHandler;
	}

	/**
	 * Configure http authentication.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated();
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

		http.formLogin().successHandler(authSuccessHandler).failureHandler(authFailureHandler);

		http.logout().permitAll();

		http.cors();
		http.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService);
	}

	@Bean
	public PasswordEncoder encoder() {
		return NoOpPasswordEncoder.getInstance();
	}
}
