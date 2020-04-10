package fr.microtec.geo2.persistance;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import graphql.schema.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.util.GraphQLUtils;

import java.util.Arrays;
import java.util.List;

public class GeoEntityGraph {

	/**
	 * Relay-Connection fields check.
	 */
	private static final String CONNECTION_DEEP_TEST = "edges/node/*";
	private static final String[] CONNECTION_DEEP_PROPERTIES = new String[]{ "edges", "node" };

	/**
	 * Get entity graph from graphQL environment.
	 *
	 * @param env GraphQL environment.
	 * @return Finned entity graph.
	 */
	public static EntityGraph getEntityGraph(ResolutionEnvironment env) {
		List<SelectedField> fields = getSelectedFields(env);
		GraphQLType outputType = getOutputType(env);

		String[] selectFields = fields.stream()
				.map(SelectedField::getName)
				.filter(f -> {
					boolean isAssos = false;

					if (outputType instanceof GraphQLObjectType) {
						GraphQLFieldDefinition fieldDef = ((GraphQLObjectType) outputType).getFieldDefinition(f);

						isAssos = fieldDef != null && fieldDef.getType() instanceof GraphQLObjectType;
					}

					return isAssos;
				})
				.toArray(String[]::new);

		System.out.println("Entity require field by graphQl");
		fields.stream().map(SelectedField::getName).forEach(System.out::println);
		System.out.println("Selected entity graph fields :");
		Arrays.stream(selectFields).forEach(System.out::println);

		return selectFields.length > 0 ? EntityGraphUtils.fromAttributePaths(selectFields) : EntityGraphs.empty();
	}

	/**
	 * Get entity selected fields from GraphQL environment.
	 *
	 * @param env GraphQL environment.
	 * @return List of entity selected fields.
	 */
	private static List<SelectedField> getSelectedFields(ResolutionEnvironment env) {
		DataFetchingFieldSelectionSet selectionSet = env.dataFetchingEnvironment.getSelectionSet();

		return selectionSet.contains(CONNECTION_DEEP_TEST) ?
				selectionSet.getFields(CONNECTION_DEEP_TEST) :
				selectionSet.getFields();
	}

	/**
	 * Get output type from graphQL environment.
	 * Support Connection.
	 *
	 * @param env GraphQL environment.
	 * @return GraphQL output type (corresponding to entity type).
	 */
	private static GraphQLType getOutputType(ResolutionEnvironment env) {
		GraphQLType outputType = GraphQLTypeUtil.isWrapped(env.fieldType) ? GraphQLUtils.unwrap(env.fieldType) : env.fieldType;
		boolean isConnection = env.dataFetchingEnvironment.getSelectionSet().contains(CONNECTION_DEEP_TEST);

		// For connection retrieve real type
		if (isConnection && outputType instanceof GraphQLObjectType) {
			GraphQLObjectType objectType = (GraphQLObjectType) outputType;

			for (String prop : CONNECTION_DEEP_PROPERTIES) {
				GraphQLFieldDefinition fieldDefinition = objectType.getFieldDefinition(prop);
				if (fieldDefinition != null) {
					GraphQLType type = fieldDefinition.getType();
					outputType = objectType = (GraphQLObjectType) (GraphQLTypeUtil.isWrapped(type) ? GraphQLUtils.unwrap(type) : type);
				}
			}
		}

		return outputType;
	}

}
