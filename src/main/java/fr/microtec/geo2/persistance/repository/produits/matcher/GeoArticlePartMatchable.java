package fr.microtec.geo2.persistance.repository.produits.matcher;

import fr.microtec.geo2.persistance.entity.produits.GeoProduitWithEspeceId;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.hibernate.metamodel.model.domain.internal.SingularAttributeImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Add logic for select unique element in avi_art_* tables in databases.
 * Property took for this selection is defined in Entity with @ArticlePartMatch.
 */
public interface GeoArticlePartMatchable<T> {

    /**
     * List property annotated by ArticlePartMatch.
     */
    default List<String> getArticleMatchPropertyList(Class<?> fromClass) {
        List<String> fieldsDoMatch = new ArrayList<>();

        ReflectionUtils.doWithFields(
            fromClass,
            field -> fieldsDoMatch.add(field.getName()),
            field -> field.isAnnotationPresent(GeoArticlePartMatch.class)
        );

        return fieldsDoMatch;
    }

    /**
     * Get specification (where) for matching article part.
     */
    default Specification<T> getArticleMatchSpecification(T from) {
        final List<String> properties = this.getArticleMatchPropertyList(from.getClass());
        BeanWrapper entityWrapper = new BeanWrapperImpl(from);

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = properties.stream()
                .map(property -> {
                    Type<?> fieldType = root.getModel().getSingularAttribute(property).getType();
                    Path<?> expression = root.get(property);
                    String localProperty = property;

                    // Check target entity property is sub object than has GeoProduitWithEspeceId IdClass.
                    if (fieldType instanceof EntityTypeImpl<?>) {
                        EntityTypeImpl<?> fieldEntityType = (EntityTypeImpl<?>) fieldType;

                        if (fieldEntityType.hasIdClass()) {
                            Class<?> idClassClass = ((SingularAttributeImpl<?, ?>) fieldEntityType.getIdClassAttributes().toArray()[0]).getJavaMember().getDeclaringClass();

                            if (GeoProduitWithEspeceId.class.isAssignableFrom(idClassClass)) {
                                expression = expression.get("id");
                                localProperty = property + ".id";
                            }
                        }
                    }

                    // If is null bind isNull operator and join if needed
                    try {
                        Object value = entityWrapper.getPropertyValue(localProperty);

                        return criteriaBuilder.equal(expression, value);
                    } catch (NullValueInNestedPathException ex) {
                        // Join only object entity
                        if (!property.equals(localProperty)) {
                            root.join(property, JoinType.LEFT);
                        }

                        return criteriaBuilder.isNull(expression);
                    }
                })
                .collect(Collectors.toList());

            // Convert to and conditions list
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

}
