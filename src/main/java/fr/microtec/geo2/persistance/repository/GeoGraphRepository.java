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
public interface GeoGraphRepository<T, ID extends Serializable>  extends
		//GeoRepository<T, ID>
		JpaRepository<T, ID>, JpaSpecificationExecutor<T>
		/*EntityGraphJpaRepository<T, ID>,
		EntityGraphJpaSpecificationExecutor<T>*/ {
}
