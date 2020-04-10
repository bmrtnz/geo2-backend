package fr.microtec.geo2.configuration;

import cz.jirutka.rsql.parser.RSQLParser;
import fr.microtec.geo2.persistance.rsql.RsqlSearchOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RsqlConfiguration {

	@Bean
	public RSQLParser getParser() {
		return new RSQLParser(RsqlSearchOperation.supportedOperators());
	}

}
