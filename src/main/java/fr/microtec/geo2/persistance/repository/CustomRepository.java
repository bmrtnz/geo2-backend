package fr.microtec.geo2.persistance.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomRepository<T> {
    List<T> findAllWithPaginations(final Specification<T> specs, final Pageable pageable, final Class<T> clazz, final Set<String> fields);
    Page<T> findAllWithPagination(final Specification<T> specs, final Pageable pageable, final Class<T> clazz, final Set<String> fields);
}
