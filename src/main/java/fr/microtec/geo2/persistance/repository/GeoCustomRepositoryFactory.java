package fr.microtec.geo2.persistance.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;

import javax.persistence.EntityManager;

public class GeoCustomRepositoryFactory extends JpaRepositoryFactory {

    private GeoRepositoryEvent repositoryEvent;

    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public GeoCustomRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    public void setRepositoryEvent(GeoRepositoryEvent repositoryEvent) {
        this.repositoryEvent = repositoryEvent;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaRepositoryImplementation<?, ?> repository = super.getTargetRepository(information, entityManager);

        if (repository instanceof GeoCustomRepository) {
            ((GeoCustomRepository<?>) repository).setRepositoryEvent(this.repositoryEvent);
        }

        return repository;
    }

}
