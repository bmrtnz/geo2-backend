package fr.microtec.geo2.persistance.repository;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface GeoRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	Page<T> findAll(Specification<T> specification, Pageable pageable, EntityGraph entityGraph);

	Page<T> findAll(Pageable pageable, EntityGraph entityGraph);

	Optional<T> findById(ID id, EntityGraph entityGraph);

}
