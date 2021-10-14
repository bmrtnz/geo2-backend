package fr.microtec.geo2.common;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CustomUtils
{
    /**
     * @param fields La liste des champs souhaités.
     * @param root Le type racine dans la clause from de l’objet T.
     * @return Une liste d’object Selection correspondant aux attributs de l’entité pour faire une requête SQL avec hibernate.
     * On vérifie également que les fields qui sont passés ne font pas référence à des champs de type entité.
     * Il n’est pas possible de faire une query GraphQL en demandant depuis l’entité Geo_Client le champ "societe".
     */
    public static <T> List<Selection<?>> getSelections(List<String> fields, Root<T> root)
    {
        List<Selection<?>> selections = new ArrayList<>();
        val clazz = root.getJavaType();

        fields
            .stream()
            .filter(field -> {
                // On se fiche de chercher ou se trouve __typename dans l’entité.
                // Dans tous les cas, le type de l’entité est retourné par GraphQL automatiquement.
                if(field.contains("__typename")) return false;

                AtomicReference<Boolean> garder = new AtomicReference<>(true);
                AtomicReference<Class<?>> cls = new AtomicReference<>(clazz);
                Arrays.stream(field.split("\\."))
                    .forEach(s ->
                        getField(cls.get(), s)
                            .ifPresentOrElse(declaredField -> {
                                if(declaredField.isAnnotationPresent(OneToMany.class) || declaredField.isAnnotationPresent(ManyToMany.class)) {
                                    garder.set(false);
                                }
                                cls.set(declaredField.getType());
                            }, () -> garder.set(false))
                    );

                if(!garder.get()) {
                    log.info("Le champ \"{}\" n'a pas été trouvé ou il ne peut pas être gardé car il est de type @OneToMany ou @ManyToMany.", field);
                }

                return garder.get();
            })
            .forEach(field -> {
                try {
                    Selection<?> selection;

                    AtomicReference<Path<Object>> objectPath = new AtomicReference<>(null);
                    Arrays.stream(field.split("\\.")).forEach(s -> {
                        if(objectPath.get() == null) {
                            objectPath.set(root.get(s));
                        }
                        else {
                            objectPath.set(objectPath.get().get(s));
                        }
                    });

                    selection = objectPath.get().alias(field);

                    // On check que le type de l’objet demandé n’est pas un objet provenant du package fr.microtec
                    // Il n’est pas possible de demander directement l’objet GEO_SOCIETE directement depuis une entité GEO_CLIENT
                    if(selection != null) {
                        val attribute = ((AbstractPathImpl<?>) selection).getAttribute();
                        // Si l’on a une collection, on récupère la classe déclarée de la collection, sinon la classe.
                        val aClass = (attribute.isCollection()) ? attribute.getDeclaringType().getJavaType() : attribute.getJavaType();
                        if(!aClass.isAnnotationPresent(Entity.class)) {
                            selections.add(selection);
                        }
                    }
                }
                catch (IllegalArgumentException | IllegalStateException ignore)
                {
                    // Si root.get(field) n’existe pas, on attrape l’exception pour ne rien en faire.
                    // Ceci ne devrait jamais arriver si les champs fournis dans la liste proviennent de GraphQL.
                    log.error("Le champ {} n’existe pas dans l’objet souhaité.", field);
                }
            });

        return selections;
    }

    public static <T> void buildGraph(EntityGraph<T> entityGraph, List<Selection<?>> selections)
    {
        Map<String, Subgraph<?>> map = new HashMap<>();

        selections
            .stream()
            .filter(selection -> {
                val alias = selection.getAlias();
                return (StringUtils.hasText(alias) && alias.contains("."));
            })
            .forEach(selection -> {
                val alias = selection.getAlias();

                val attribute = ((AbstractPathImpl<?>) selection).getAttribute();
                // Si l’on a une collection, on récupère la classe déclarée de la collection, sinon la classe.
                val aClass = (attribute.isCollection()) ? attribute.getDeclaringType().getJavaType() : attribute.getJavaType();
                if(aClass.isAnnotationPresent(Entity.class)) {

                }
            });
    }

    /**
     * Recherche un champ dans une classe en descendant en profondeur dans les classes enfants.
     * Si l’on ne trouve rien, l’optional sera null, sinon il contiendra le champ trouvé.
     * @param clazz La classe dans laquelle on doit chercher la propriété.
     * @param fieldName le nom de la propriété que l’on cherche.
     * @return Un optional de la propriété ou null.
     */
    private static Optional<Field> getField(final Class<?> clazz, final String fieldName)
    {
        if(clazz == null) return Optional.empty();

        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return getField(clazz.getSuperclass(), fieldName);
        }
    }
}
