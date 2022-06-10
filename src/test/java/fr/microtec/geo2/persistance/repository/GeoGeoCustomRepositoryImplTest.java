package fr.microtec.geo2.persistance.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.JoinType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import lombok.val;

@DataJpaTest
@ContextConfiguration(classes = Geo2Application.class)
@EnableJpaRepositories(repositoryBaseClass = GeoCustomRepositoryImpl.class)
class GeoGeoCustomRepositoryImplTest
{
    @Autowired
    private GeoClientRepository geoClientRepository;

    private static Specification<GeoClient> specSA() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("societe").get("id"), "SA");
    }

    private static Specification<GeoClient> specBWS() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("societe").get("id"), "BWS");
    }

    private final static PageRequest PAGEABLE = PageRequest.of(0, 10);

    @Test
    public void testWithSA()
    {
        val allWithPaginations = this.geoClientRepository.findAllWithPaginations(specSA(), PAGEABLE, GeoClient.class, Set.of("id", "adresse1", "adresse2"));

        assertFalse(allWithPaginations.isEmpty());
        assertEquals(7, allWithPaginations.size());
    }

    @Test
    public void testWithBWS()
    {
        val allWithPaginations = this.geoClientRepository.findAllWithPaginations(specBWS(), PAGEABLE, GeoClient.class, Set.of("id", "adresse1", "adresse2", "dateModification"));

        assertFalse(allWithPaginations.isEmpty());
        assertEquals(1, allWithPaginations.size());

        GeoClient client = allWithPaginations.get(0);
        assertEquals("000000", client.getId());
        assertEquals("adresse1", client.getAdresse1());
        assertEquals("adresse2", client.getAdresse2());
        assertEquals(LocalDateTime.of(2021, Month.FEBRUARY, 1, 16, 27, 37), client.getDateModification());
    }

    @Test
    public void testWithBWSAndSocieteDependency()
    {
        List<GeoClient> clients = this.geoClientRepository.findAllWithPaginations(specBWS(), PAGEABLE, GeoClient.class, Set.of("id", "adresse1", "societe.id"), JoinType.INNER);

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        GeoClient client = clients.get(0);

        assertNotNull(client.getSociete());
        assertNotNull(client.getSociete().getId());
        assertEquals("BWS", client.getSociete().getId());
    }

    @Test
    public void testWithBWSAndBadFields()
    {
        List<GeoClient> clients = this.geoClientRepository.findAllWithPaginations(specBWS(), PAGEABLE, GeoClient.class, Set.of("id", "adresse12", "societe.ids"));

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        GeoClient client = clients.get(0);
        assertEquals("000000", client.getId());
        assertNull(client.getAdresse1());
        assertNull(client.getSociete());
    }

    @Test
    public void testWithBWSAndSocieteAndPaysDependency()
    {
        List<GeoClient> clients = this.geoClientRepository.findAllWithPaginations(specBWS(), PAGEABLE, GeoClient.class, Set.of("id", "adresse1", "dateDebutIfco", "dateModification", "blocageAvoirEdi", "nbJourLimiteLitige", "societe.id", "societe.pays.description"), JoinType.INNER);

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        GeoClient client = clients.get(0);
        assertEquals(LocalDate.of(2021, Month.SEPTEMBER, 30), client.getDateDebutIfco());
        assertEquals(LocalDateTime.of(2021, Month.FEBRUARY, 1, 16, 27, 37), client.getDateModification());
        assertTrue(client.getBlocageAvoirEdi());
        assertEquals(45, client.getNbJourLimiteLitige());

        assertNotNull(client.getSociete());
        assertNotNull(client.getSociete().getId());
        assertEquals("BWS", client.getSociete().getId());

        assertNotNull(client.getSociete().getPays());
        assertNotNull(client.getSociete().getPays().getDescription());
        assertEquals("France", client.getSociete().getPays().getDescription());
    }

    @Test
    public void testPage()
    {
        val page = this.geoClientRepository.findAllWithPagination(specSA(), PageRequest.of(0, 5), GeoClient.class, Set.of("id", "adresse1", "adresse2"));

        assertFalse(page.isEmpty());
        assertEquals(7, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(5, page.getNumberOfElements());
    }

    /**
     * Ce test permet de vérifier que lorsque l’on demande le champ "societe" il ne charge pas l’entité en entière.
     */
    @Test
    public void testWithBWSWithoutSociete()
    {
        List<GeoClient> clients = this.geoClientRepository.findAllWithPaginations(specBWS(), PAGEABLE, GeoClient.class, Set.of("id", "societe"));

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        GeoClient client = clients.get(0);

        assertNull(client.getSociete());
    }

    @Test
    public void testWithBWSAndSocieteAndPaysAndNotClients()
    {
        List<GeoClient> clients = this.geoClientRepository.findAllWithPaginations(specBWS(), PAGEABLE, GeoClient.class, Set.of("id", "societe.id", "societe.pays.description", "societe.pays.clients"), JoinType.INNER);

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        GeoClient client = clients.get(0);

        assertNotNull(client.getSociete());
        assertNotNull(client.getSociete().getId());
        assertNotNull(client.getSociete().getPays());
        assertEquals("France", client.getSociete().getPays().getDescription());
        assertNull(client.getSociete().getPays().getClients());
    }
}
