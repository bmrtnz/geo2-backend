package fr.microtec.geo2.persistance.repository;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

public class GeoCustomRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    private ObjectProvider<EntityPathResolver> resolver;
    private GeoRepositoryEvent repositoryEvent;

    public GeoCustomRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Autowired
    public void setRepositoryEvent(GeoRepositoryEvent repositoryEvent) {
        this.repositoryEvent = repositoryEvent;
    }

    @Autowired
    @Override
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        super.setEntityPathResolver(resolver);
        this.resolver = resolver;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        GeoCustomRepositoryFactory geoCustomRepositoryFactory = new GeoCustomRepositoryFactory(entityManager);

        geoCustomRepositoryFactory.setEntityPathResolver(this.resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE));
        geoCustomRepositoryFactory.setEscapeCharacter(EscapeCharacter.DEFAULT);
        geoCustomRepositoryFactory.setRepositoryEvent(this.repositoryEvent);

        return geoCustomRepositoryFactory;
    }
}
