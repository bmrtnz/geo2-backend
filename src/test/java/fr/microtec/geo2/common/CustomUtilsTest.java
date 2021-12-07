package fr.microtec.geo2.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.CustomRepositoryImpl;
import lombok.val;

@DataJpaTest
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
class CustomUtilsTest
{
    @Autowired
    private EntityManager entityManager;

    private Root<GeoClient> getRoot()
    {
        val criteriaBuilder = entityManager.getCriteriaBuilder();
        val query = criteriaBuilder.createQuery(GeoClient.class);
        return query.from(GeoClient.class);
    }

    @Test
    public void getSelectionSize()
    {
        List<Selection<?>> selections = CustomUtils.getSelections(Set.of("id", "adresse1"), this.getRoot());

        assertEquals(2, selections.size());
    }

    @Test
    public void getSelectionWithSociete()
    {
        List<Selection<?>> selections = CustomUtils.getSelections(Set.of("id", "adresse1", "societe.id"), this.getRoot());

        assertEquals(3, selections.size());
        assertEquals("id", selections.get(0).getAlias());
        assertEquals("adresse1", selections.get(1).getAlias());
        assertEquals("societe.id", selections.get(2).getAlias());
    }

    @Test
    public void getSelectionWithBadField()
    {
        List<Selection<?>> selections = CustomUtils.getSelections(Set.of("id", "adresse1", "unChampQuiN'existePas"), this.getRoot());

        assertEquals(2, selections.size());
    }

    @Test
    public void getSelectionWithManyToManyRelation()
    {
        // OneToMany or ManyToMany cannot be load.
        List<Selection<?>> selections = CustomUtils.getSelections(Set.of("id", "adresse1", "societe.pays.clients.adresse1"), this.getRoot());

        assertEquals(2, selections.size());
    }
}
