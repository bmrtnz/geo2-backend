package fr.microtec.geo2.common;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.hibernate.query.criteria.internal.path.AbstractPathImpl;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.StringUtils;

import fr.microtec.geo2.persistance.CriteriaUtils;
import io.leangen.graphql.execution.ResolutionEnvironment;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomUtils {
    /**
     * @param fields La liste des champs souhaités.
     * @param root   Le type racine dans la clause from de l’objet T.
     * @param mapper La fonction de mappage vers R.
     * @return Une liste d’object R correspondant aux attributs de l’entité pour
     *         faire une requête SQL avec hibernate.
     *         On vérifie également que les fields qui sont passés ne font pas
     *         référence à des champs de type entité.
     *         Il n’est pas possible de faire une query GraphQL en demandant depuis
     *         l’entité Geo_Client le champ "societe".
     */
    public static <T, R> List<R> parseFields(Set<String> fields, Root<T> root, Function<String, ? extends R> mapper) {
        val clazz = root.getJavaType();

        return fields
                .stream()
                .filter(field -> {
                    // On se fiche de chercher ou se trouve __typename dans l’entité.
                    // Dans tous les cas, le type de l’entité est retourné par GraphQL
                    // automatiquement.
                    if (field.contains("__typename"))
                        return false;

                    AtomicReference<Boolean> garder = new AtomicReference<>(true);
                    AtomicReference<Class<?>> cls = new AtomicReference<>(clazz);
                    Arrays.stream(field.split("\\."))
                            .forEach(s -> getField(cls.get(), s)
                                    .ifPresentOrElse(declaredField -> {
                                        if (declaredField.isAnnotationPresent(OneToMany.class)
                                                || declaredField.isAnnotationPresent(ManyToMany.class)) {
                                            garder.set(false);
                                        }
                                        cls.set(declaredField.getType());
                                    }, () -> garder.set(false)));

                    if (!garder.get()) {
                        log.info(
                                "Le champ \"{}\" n'a pas été trouvé ou il ne peut pas être gardé car il est de type @OneToMany ou @ManyToMany.",
                                field);
                    }

                    return garder.get();
                })
                .filter(field -> {
                    try {

                        val result = mapper.apply(field);

                        // On check que le type de l’objet demandé n’est pas un objet provenant du
                        // package fr.microtec
                        // Il n’est pas possible de demander directement l’objet GEO_SOCIETE directement
                        // depuis une entité GEO_CLIENT
                        if (result != null) {
                            val attribute = ((AbstractPathImpl<?>) result).getAttribute();
                            // Si l’on a une collection, on récupère la classe déclarée de la collection,
                            // sinon la classe.
                            val aClass = (attribute.isCollection()) ? attribute.getDeclaringType().getJavaType()
                                    : attribute.getJavaType();
                            return !aClass.isAnnotationPresent(Entity.class);
                        }
                        return false;
                    } catch (IllegalArgumentException | IllegalStateException ignore) {
                        // Si root.get(field) n’existe pas, on attrape l’exception pour ne rien en
                        // faire.
                        // Ceci ne devrait jamais arriver si les champs fournis dans la liste
                        // proviennent de GraphQL.
                        log.error("Le champ {} n’existe pas dans l’objet souhaité.", field);
                        return false;
                    }
                })
                .map(field -> mapper.apply(field))
                .collect(Collectors.toList());

    }

    /**
     * @param fields   La liste des champs souhaités.
     * @param root     Le type racine dans la clause from de l’objet T.
     * @param joinType Le type de jointure a utiliser.
     * @return Une liste d’object Selection correspondant aux attributs de l’entité
     *         pour faire une requête SQL avec hibernate.
     */
    public static <T> List<Selection<?>> getSelections(Set<String> fields, Root<T> root, JoinType joinType) {
        return CustomUtils
                .parseFields(fields, root, (String field) -> {

                    return fetchField(root, field, joinType).alias(field);

                });
    }

    public static <T> List<Path<?>> getSelectionPaths(Set<String> fields, Root<T> root, JoinType joinType) {
        return CustomUtils
                .parseFields(fields, root, (String field) -> {

                    return (Path<?>) (fetchField(root, field, joinType).alias(field));

                });
    }

    /**
     * @param fields La liste des champs souhaités.
     * @param root   Le type racine dans la clause from de l’objet T.
     * @return Une liste d’object Selection correspondant aux attributs de l’entité
     *         pour faire une requête SQL avec hibernate.
     */
    public static <T> List<Selection<?>> getSelections(Set<String> fields, Root<T> root) {
        return getSelections(fields, root, JoinType.LEFT);
    }

    public static <T> List<Path<?>> getSelectionPaths(Set<String> fields, Root<T> root) {
        return getSelectionPaths(fields, root, JoinType.LEFT);
    }

    /**
     * @param fields   La liste des champs souhaités.
     * @param root     Le type racine dans la clause from de l’objet T.
     * @param joinType Le type de jointure a utiliser.
     * @return Une liste d’object Expression correspondant aux attributs de l’entité
     *         pour faire une requête SQL avec hibernate.
     */
    public static <T> List<Expression<?>> getSelectionExpressions(Set<String> fields, Root<T> root, JoinType joinType) {
        return CustomUtils
                .parseFields(fields, root, (String field) -> {

                    Expression<?> objectPath = fetchField(root, field, joinType);
                    objectPath.alias(field);
                    return objectPath;

                });
    }

    /**
     * @param fields La liste des champs souhaités.
     * @param root   Le type racine dans la clause from de l’objet T.
     * @return Une liste d’object Expression correspondant aux attributs de l’entité
     *         pour faire une requête SQL avec hibernate.
     */
    public static <T> List<Expression<?>> getSelectionExpressions(Set<String> fields, Root<T> root) {
        return getSelectionExpressions(fields, root, JoinType.LEFT);
    }

    private static Expression<?> fetchField(From<?, ?> from, String field, JoinType joinType) {
        PropertyPath propertyPath = PropertyPath
                .from(field, from.getJavaType());
        return fetchField(from, propertyPath, joinType);
    }

    private static Expression<?> fetchField(From<?, ?> from, PropertyPath propertyPath, JoinType joinType) {

        val chunk = propertyPath.getSegment();

        val path = propertyPath.hasNext()
                ? CriteriaUtils.getOrCreateJoin(from, chunk, joinType)
                : from.get(chunk);

        if (propertyPath.hasNext())
            return fetchField((From<?, ?>) path, propertyPath.next(), joinType);

        return path;
    }

    public static <T> void buildGraph(EntityGraph<T> entityGraph, List<Selection<?>> selections) {
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
                    // Si l’on a une collection, on récupère la classe déclarée de la collection,
                    // sinon la classe.
                    val aClass = (attribute.isCollection()) ? attribute.getDeclaringType().getJavaType()
                            : attribute.getJavaType();
                    if (aClass.isAnnotationPresent(Entity.class)) {

                    }
                });
    }

    public static Map<String, Object> parseArgumentFromEnv(final ResolutionEnvironment env, String argument) {
        return env.dataFetchingEnvironment.getArgument(argument);
    }

    public static <T> String classToArgument(Class<T> clazz) {
        String arg = clazz.getSimpleName();
        arg = arg.substring(3);
        arg = arg.substring(0, 1).toLowerCase() + arg.substring(1);
        return arg;
    }

    public static Set<String> parseSelectFromEnv(final ResolutionEnvironment env) {
        return CustomUtils.parseSelectFromEnv(env, "edges/node/**");
    }

    public static Set<String> parseSelectFromEnv(final ResolutionEnvironment env, final String search) {
        return env.dataFetchingEnvironment
                .getSelectionSet()
                .getFields(search)
                .stream()
                .map(field -> field.getQualifiedName().replace("edges/node/", "").replace("/", "."))
                .collect(Collectors.toSet());
    }

    public static Set<String> parseSelect(final Set<String> fields) {
        return fields
                .stream()
                .map(s -> s.replace("/", "."))
                .collect(Collectors.toSet());
    }

    /**
     * Recherche un champ dans une classe en descendant en profondeur dans les
     * classes enfants.
     * Si l’on ne trouve rien, l’optional sera null, sinon il contiendra le champ
     * trouvé.
     *
     * @param clazz La classe dans laquelle on doit chercher la propriété.
     * @param path  le chemin de la propriété que l’on cherche.
     * @return Un optional de la propriété ou null.
     */
    public static Optional<Field> getField(final Class<?> clazz, final String path) {
        if (clazz == null)
            return Optional.empty();

        List<String> fields = Arrays.asList(path.split("\\."));
        if (fields.size() > 1) {
            try {
                Class<?> nextClass = clazz.getDeclaredField(fields.get(0)).getType();
                String nextPath = String.join(".", fields.subList(1, fields.size()));
                return getField(nextClass, nextPath);
            } catch (NoSuchFieldException ex) {
                return getField(clazz.getSuperclass(), path);
            }
        } else
            try {
                return Optional.of(clazz.getDeclaredField(path));
            } catch (NoSuchFieldException e) {
                return getField(clazz.getSuperclass(), path);
            }
    }

    /**
     * Build paths from GraphQL Field
     *
     * @param field Root field
     * @return Stream of paths (as String)
     */
    public static Stream<String> getPaths(graphql.language.Field field) {
        return CustomUtils
                .getPaths(field, "")
                .filter(f -> !f.contains("__typename"));
    }

    private static Stream<String> getPaths(graphql.language.Field inputField, String prefix) {
        if (!inputField.getChildren().isEmpty()) {
            return inputField
                    .getSelectionSet()
                    .getSelectionsOfType(graphql.language.Field.class)
                    .stream()
                    .flatMap(child -> {
                        val newPrefix = prefix.concat(inputField.getName()).concat("/");
                        return getPaths(child, newPrefix);
                    });
        }

        return Stream.of(prefix.concat(inputField.getName()));
    }

    /**
     * Execute and fetch count query.
     *
     * @param query Query to execute.
     * @return Count result.
     */
    public static long executeCountQuery(TypedQuery<Long> query) {
        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }
}
