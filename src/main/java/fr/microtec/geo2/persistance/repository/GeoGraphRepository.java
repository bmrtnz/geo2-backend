package fr.microtec.geo2.persistance.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface GeoGraphRepository<T, ID extends Serializable> extends
		EntityGraphJpaRepository<T, ID>,
		EntityGraphJpaSpecificationExecutor<T> {
}
