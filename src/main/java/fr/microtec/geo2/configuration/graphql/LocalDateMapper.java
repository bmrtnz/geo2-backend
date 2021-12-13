package fr.microtec.geo2.configuration.graphql;

import graphql.schema.GraphQLScalarType;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.common.CachingMapper;
import io.leangen.graphql.util.Scalars;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.microtec.geo2.common.TemporalUtils;

public class LocalDateMapper extends CachingMapper<GraphQLScalarType, GraphQLScalarType> {

	private static final Map<Type, GraphQLScalarType> MAPPING;

	static {
		Map<Type, GraphQLScalarType> map = new HashMap<>();

		map.put(LocalDate.class, Scalars.temporalScalar(
			LocalDate.class, "LocalDate", "a local date",
			s -> LocalDate.parse(s, DateTimeFormatter.ISO_DATE),
			i -> i.atZone(ZoneOffset.UTC).toLocalDate()
		));
		
		map.put(LocalDateTime.class, Scalars.temporalScalar(
			LocalDateTime.class, "LocalDateTime", "a local date time",
			s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern(TemporalUtils.ISO8601_PATTERN)),
			i -> i.atZone(ZoneOffset.UTC).toLocalDateTime()
		));

		MAPPING = Collections.unmodifiableMap(map);
	}

	@Override
	protected GraphQLScalarType toGraphQLType(String typeName, AnnotatedType javaType, OperationMapper operationMapper, BuildContext buildContext) {
		return MAPPING.get(javaType.getType());
	}

	@Override
	protected GraphQLScalarType toGraphQLInputType(String typeName, AnnotatedType javaType, OperationMapper operationMapper, BuildContext buildContext) {
		return this.toGraphQLType(typeName, javaType, operationMapper, buildContext);
	}

	@Override
	public boolean supports(AnnotatedType type) {
		return MAPPING.containsKey(type.getType());
	}

	protected String getTypeName(AnnotatedType type, BuildContext buildContext) {
		return MAPPING.get(type.getType()).getName();
	}

	protected String getInputTypeName(AnnotatedType type, BuildContext buildContext) {
		return this.getTypeName(type, buildContext);
	}
}
