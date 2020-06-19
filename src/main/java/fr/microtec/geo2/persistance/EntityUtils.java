package fr.microtec.geo2.persistance;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EntityUtils {

	/**
	 * Get geo entity class by this name.
	 *
	 * @param className Simple class name.
	 * @return Founded class.
	 */
	public static Class<?> getEntityClassFromName(String className) {
		String entityPackage = "fr.microtec.geo2.persistance.entity";
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false, new StandardServletEnvironment()
		);

		provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		for (BeanDefinition beanDefinition : provider.findCandidateComponents(entityPackage)) {
			if (beanDefinition.getBeanClassName() != null && beanDefinition.getBeanClassName().endsWith(className)) {
				try {
					return Class.forName(beanDefinition.getBeanClassName());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(String.format("Unable to load class '%s'", beanDefinition.getBeanClassName()));
				}
			}
		}

		throw new RuntimeException(String.format("Unable to find entity with name '%s'", className));
	}

	public static boolean isEntity(Class<?> clazz) {
		return clazz.getAnnotation(Entity.class) != null;
	}

	/**
	 * Get entity field database column name.
	 *
	 * @param entityClass Entity class.
	 * @param field Searched field.
	 * @return Database column name.
	 */
	public static String getEntityPropertyColumnName(Class<?> entityClass, String field) {
		Optional<Field> optionalField = Arrays.stream(entityClass.getDeclaredFields())
				.filter(f -> f.getName().equals(field))
				.findFirst();

		String columnName = optionalField.orElseThrow().getAnnotation(Column.class).name();

		if (columnName.isBlank()) {
			columnName = field;
		}

		return columnName;
	}

	/**
	 * Parse property to expression, support deep parsing (ex: "property.sub-property").
	 *
	 * @param root The root type.
	 * @param property Property string chain to parse.
	 * @return Parsed expression.
	 */
	public static <Y> Expression<Y> parseExpression(Root<?> root, String property) {
		Path<Y> path = null;

		for (String part : property.split("\\.")) {
			path = (path != null) ? path.get(part) : root.get(part);
		}

		return path;
	}

}
