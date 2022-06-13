package fr.microtec.geo2.persistance.repository.event;

import org.springframework.core.GenericTypeResolver;

/**
 * Interface to extend for react to data load event.
 * Work for all query execute by GeoCustomRepository.
 *
 * @param <T> Objet you want to
 */
public interface Geo2LoadEventListener<T> {

    /**
     * OnLoad callback.
     */
    void onLoad(T entity);

    /*
     * If subclass accept onLoad callback on given class.
     * Here (By default) it's must be match to T generic param.
     */
    default boolean acceptVisitor(Class<?> clazz) {
        Class<T> acceptClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClass(), Geo2LoadEventListener.class);

        return acceptClass.isAssignableFrom(clazz);
    }

}
