package fr.microtec.geo2.persistance.repository;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PostLoad;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import fr.microtec.geo2.common.CustomUtils;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRepository<T>
{
    private final EntityManager entityManager;

    public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager)
    {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * Permet de récupérer les données en fonction des champs souhaités.
     *      * Cela est l’équivalent des projections.
     *      * La requête SQL sera construite de façon à ne récupérer que les champs fournis dans la liste fields.
     *      * Si l’on souhaite avoir le champ adresse1 de l’objet GEO_SOCIETE depuis un l’objet GEO_CLIENT, alors il faut ajouter dans la liste
     *      * la valeur {@literal "societe.adresse1"}
     *      * Si un champ de la liste fields n’est pas trouvé dans l’objet T ou ses descendants, alors il sera ignoré.
     *      * Tout autres champs non spécifiés dans la liste fields aura pour valeur null, ou sa valeur par défaut pour valeur dans l’objet T ou ses descendances.
     * @param specs Une specification de l’objet T. La spécification peut-être null.
     * @param pageable L’objet pageable.
     * @param clazz La class qui est la représentation de T.
     * @param fields Une liste des champs que l’on souhaite dans le select.
     * @return Une liste d’object contenant les données spécifiées dans la liste fields.
     */
    public List<T> findAllWithPaginations(final Specification<T> specs, final Pageable pageable, final Class<T> clazz, final Set<String> fields, JoinType joinType)
    {
        // Il semble que l’entity graph soit déjà utilisé par défaut.
//        EntityGraph<T> entityGraph = this.entityManager.createEntityGraph(clazz);

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();

        Root<T> root = this.applySpecToCriteria(query, builder, specs);

        List<Selection<?>> selections = CustomUtils.getSelections(fields, root, joinType);

//        CustomUtils.buildGraph(entityGraph, selections);

        query.multiselect(selections);

        this.applySorting(builder, query, root, pageable);

        TypedQuery<Tuple> typedQuery = this.entityManager.createQuery(query);

//        typedQuery.setHint("javax.persistence.loadgraph", entityGraph);


        if(pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }

        List<T> result = new ArrayList<>();

        typedQuery.getResultList().forEach(tuple -> {
            try {
                T newClass = clazz.getDeclaredConstructor().newInstance();

                selections.forEach(selection -> {
                    val alias = selection.getAlias();

                    this.setData(newClass, alias, tuple);
                });
                result.add(newClass);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        });

        return result;
    }

    public List<T> findAllWithPaginations(final Specification<T> specs, final Pageable pageable, final Class<T> clazz, final Set<String> fields)
    {
        return this.findAllWithPaginations(specs, pageable, clazz, fields, JoinType.LEFT);
    }

    /**
     * Permet de récupérer les données en fonction des champs souhaités.
     * Cela est l’équivalent des projections.
     * La requête SQL sera construite de façon à ne récupérer que les champs fournis dans la liste fields.
     * Si l’on souhaite avoir le champ adresse1 de l’objet GEO_SOCIETE depuis un l’objet GEO_CLIENT, alors il faut ajouter dans la liste
     * la valeur {@literal "societe.adresse1"}
     * Si un champ de la liste fields n’est pas trouvé dans l’objet T ou ses descendants, alors il sera ignoré.
     * Tout autres champs non spécifiés dans la liste fields aura pour valeur null, ou sa valeur par défaut pour valeur dans l’objet T ou ses descendances.
     * @param specs Une specification de l’objet T. La spécification peut-être null.
     * @param pageable L’objet pageable.
     * @param clazz La class qui est la représentation de T.
     * @param fields Une liste des champs que l’on souhaite dans le select.
     * @return Un objet Page contenant les données spécifiées dans la liste fields.
     */
    public Page<T> findAllWithPagination(final Specification<T> specs, final Pageable pageable, final Class<T> clazz, final Set<String> fields, JoinType joinType)
    {
        val list = this.findAllWithPaginations(specs, pageable, clazz, fields, joinType);
        val total = this.count(specs);
        return new PageImpl<>(list, pageable, total);
    }
    public Page<T> findAllWithPagination(final Specification<T> specs, final Pageable pageable, final Class<T> clazz, final Set<String> fields)
    {
        return this.findAllWithPagination(specs, pageable, clazz, fields, JoinType.LEFT);
    }

    /**
     * Met les valeurs de l’objet Tuple dans l’instance de l’objet newClass en utilisant le paramètre alias.
     * @param newClass Une instance de l’objet qui sera utilisé pour binder les résultats du Tuple hibernate.
     * @param alias Correspond à l’alias qui est utilisé dans la query
     * @param tuple L’objet Tuple hibernate contenant les résultats.
     */
    private void setData(final Object newClass, final String alias, final Tuple tuple)
    {
        this.setData(newClass, alias, tuple, "");
    }

    /**
     * Met les valeurs de l’objet Tuple dans l’instance de l’objet newClass en utilisant le paramètre alias.
     * @param newClass Une instance de l’objet qui sera utilisé pour binder les résultats du Tuple hibernate.
     * @param fullAlias Correspond à l’alias qui est utilisé dans la query
     * @param tuple L’objet Tuple hibernate contenant les résultats.
     * @param alias Correspond à l’alias qu’il reste à traiter dans le cas d’un alias composé d’un point. Cela permet de mettre la valeur du Tuple dans l’objet enfant.
     */
    private void setData(final Object newClass, final String fullAlias, final Tuple tuple, final String alias)
    {
        val clazz = newClass.getClass();

        try {
            if((fullAlias.contains(".") && alias.isEmpty()) || (alias.contains("."))) {
                val toSplit = (alias.contains(".")) ? alias : fullAlias;
                val split = toSplit.split("\\.", 2);
                val currentAlias = split[0];
                val restAlias = split[1];

                // On vérifie que c'est bien une entité
                val subClass = clazz.getDeclaredField(currentAlias).getType();
                if(subClass.isAnnotationPresent(Entity.class)) {
                    // On recupère la propriété voir si elle est null. Si oui, on instancie la subClass
                    val propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, currentAlias);
                    if(propertyDescriptor != null) {
                        val readMethod = propertyDescriptor.getReadMethod();
                        if(readMethod != null) {
                            val invoke = readMethod.invoke(newClass);
                            if(invoke == null) {
                                val writeMethod = propertyDescriptor.getWriteMethod();
                                if(writeMethod != null) {
                                    val obj = propertyDescriptor.getPropertyType().getDeclaredConstructor().newInstance();
                                    writeMethod.invoke(newClass, obj);
                                    this.setData(obj, fullAlias, tuple, restAlias);
                                }
                            }
                            else {
                                this.setData(invoke, fullAlias, tuple, restAlias);
                            }
                        }
                    }
                }
            }
            else {
                val currentAlias = alias.isEmpty() ? fullAlias : alias;
                val propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, currentAlias);
                if(propertyDescriptor != null) {
                    val method = propertyDescriptor.getWriteMethod();
                    if(method != null) {
                        method.invoke(newClass, tuple.get(fullAlias));
                    }
                }
            }
            this.handlePostLoadEvent(newClass);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException | InstantiationException | NoSuchMethodException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @param builder l’objet criteria builder.
     * @param query l’objet criteria query.
     * @param root Le type racine dans la clause from de l’objet T.
     * @param pageable L’objet représentant une page.
     */
    private void applySorting(final CriteriaBuilder builder, final CriteriaQuery<Tuple> query, final Root<T> root, final Pageable pageable)
    {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        if(sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }
    }

    /**
     * Applique les spécifications à la query.
     * @param query L’objet query.
     * @param builder L’objet criteria builder.
     * @param specs L’objet représentant les spécifications que l’on souhaite appliquer.
     * @return Le type racine dans la clause from de l’objet T.
     */
    private Root<T> applySpecToCriteria(final CriteriaQuery<?> query, final CriteriaBuilder builder, final Specification<T> specs)
    {
        Root<T> root = query.from(getDomainClass());

        if(specs == null) {
            return root;
        }

        Predicate predicate = specs.toPredicate(root, query, builder);

        if(predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    /**
     * Execute les methodes associées à l'annotation @PostLoad
     * @param newClass Une instance de l’objet après remplissage des données
     */
    private void handlePostLoadEvent(final Object newClass) throws IllegalAccessException, IllegalArgumentException,
    InvocationTargetException {
        Method[] methods = newClass.getClass().getDeclaredMethods();
        for(Method method : methods)
            if(method.isAnnotationPresent(PostLoad.class))
                method.invoke(newClass);
    }
}
