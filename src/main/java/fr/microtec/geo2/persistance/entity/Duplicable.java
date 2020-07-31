package fr.microtec.geo2.persistance.entity;

/**
 * Entity duplication interface
 * @param <T> Entity
 */
public interface Duplicable<T> {
	public T duplicate();
}