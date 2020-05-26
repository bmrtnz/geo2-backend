package fr.microtec.geo2.configuration;

import fr.microtec.geo2.configuration.graphql.LocalDateMapper;
import io.leangen.graphql.ExtensionProvider;
import io.leangen.graphql.GeneratorConfiguration;
import io.leangen.graphql.generator.mapping.SchemaTransformer;
import io.leangen.graphql.generator.mapping.TypeMapper;
import io.leangen.graphql.generator.mapping.common.NonNullMapper;
import io.leangen.graphql.generator.mapping.common.ScalarMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * GraphQL configuration.
 */
@Configuration
public class GraphQLConfiguration {

	/**
	 * GraphQL type mapper.
	 */
	@Bean
	public ExtensionProvider<GeneratorConfiguration, TypeMapper> typeMapperExtensionProvider() {
		return (config, defaults) -> {
			defaults.removeIf(mapper -> mapper instanceof NonNullMapper);
			defaults.insertBefore(ScalarMapper.class, new LocalDateMapper());

			return defaults;
		};
	}

	@Bean
	public ExtensionProvider<GeneratorConfiguration, SchemaTransformer> schemaTransformerExtensionProvider() {
		return (config, defaults) -> {
			defaults.removeIf(mapper -> mapper instanceof NonNullMapper);

			return defaults;
		};
	}

}
