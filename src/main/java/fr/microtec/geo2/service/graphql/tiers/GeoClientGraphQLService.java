package fr.microtec.geo2.service.graphql.tiers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientDepassementEnCours;
import fr.microtec.geo2.persistance.entity.tiers.GeoClientEnCours;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.service.ClientsService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoClientGraphQLService extends GeoAbstractGraphQLService<GeoClient, String> {

    private final ClientsService clientsService;

    public GeoClientGraphQLService(
            GeoClientRepository clientRepository,
            ClientsService clientsService) {
        super(clientRepository, GeoClient.class);
        this.clientsService = clientsService;
    }

    @GraphQLQuery
    public RelayPage<GeoClient> allClient(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoClient> getClient(
            @GraphQLArgument(name = "id") String id) {
        return this.getOne(id);
    }

    @GraphQLQuery
    public Optional<GeoClient> getClientByCode(
            @GraphQLArgument(name = "code") String code) {
        return ((GeoClientRepository) this.repository).getOneByCode(code);
    }

    @GraphQLMutation
    public GeoClient saveClient(GeoClient client, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(client, env);
    }

    @GraphQLMutation
    public void deleteClient(String id) {
        this.delete(id);
    }

    @GraphQLQuery
    public long countClient(
            @GraphQLArgument(name = "search") String search) {
        return this.count(search);
    }

    @GraphQLQuery
    public List<GeoClientEnCours> allClientEnCours(
            @GraphQLArgument(name = "clientRef") String clientRef,
            @GraphQLArgument(name = "deviseCodeRef") String deviseCodeRef) {
        return ((GeoClientRepository) this.repository).allClientEnCours(clientRef,
                deviseCodeRef != null ? deviseCodeRef : "%");
    }

    @GraphQLQuery
    public List<GeoClientDepassementEnCours> allClientDepassementEnCours(
            String secteur,
            String societe,
            @GraphQLArgument(name = "clientsValide", defaultValue = "%") Boolean clientsValide) {
        Character clientsValideChar = clientsValide == null ? '%' : clientsValide ? 'O' : 'N';
        return ((GeoClientRepository) this.repository).allClientDepassementEnCours(secteur, societe, clientsValideChar);
    }

    @GraphQLQuery
    public GeoClient duplicateClient(
            String clientID,
            String fromSocieteID,
            String toSocieteID,
            Boolean copyContacts,
            Boolean copyContactsEntrepots,
            Boolean copyEntrepots) {
        return this.clientsService.duplicate(
                clientID,
                fromSocieteID,
                toSocieteID,
                copyContacts,
                copyContactsEntrepots,
                copyEntrepots);
    }
}
