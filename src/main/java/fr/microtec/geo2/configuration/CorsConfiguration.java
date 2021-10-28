package fr.microtec.geo2.configuration;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("dev")
@Configuration
public class CorsConfiguration {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
						.addMapping("/**")
						.allowedOrigins("http://localhost:4200", "https://geo-bw.microtec.fr")
						.allowCredentials(true);
			}
		};
	}

	@Bean
	public TomcatContextCustomizer sameSiteCookiesConfig() {
		return context -> {
				final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
				cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
				context.setCookieProcessor(cookieProcessor);
		};
	}

}
