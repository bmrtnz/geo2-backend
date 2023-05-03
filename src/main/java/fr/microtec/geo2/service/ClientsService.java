package fr.microtec.geo2.service;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.GeoSequenceGenerator;
import fr.microtec.geo2.persistance.entity.tiers.GeoCertificationClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoContact;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.tiers.GeoClientRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoContactRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoSocieteRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import lombok.val;

@Service
public class ClientsService extends GeoAbstractGraphQLService<GeoClient, String> {

    @PersistenceContext
    private EntityManager entityManager;

    private final GeoSocieteRepository societeRepository;
    private final GeoEntrepotRepository entrepotRepository;
    private final GeoContactRepository contactRepository;

    public ClientsService(
            GeoClientRepository clientsRepository,
            GeoEntrepotRepository entrepotRepository,
            GeoContactRepository contactRepository,
            GeoSocieteRepository societeRepository) {
        super(clientsRepository, GeoClient.class);
        this.societeRepository = societeRepository;
        this.entrepotRepository = entrepotRepository;
        this.contactRepository = contactRepository;
    }

    public String generateId() {
        Properties params = new Properties();

        params.put("sequenceName", "seq_cli_num");
        params.put("mask", "FM099999");
        params.put("isSequence", true);

        return (String) GeoSequenceGenerator.generate(this.entityManager, params);
    }

    @Transactional
    public GeoClient duplicate(
            String clientID,
            String fromSocieteID,
            String toSocieteID,
            Boolean copyContacts,
            Boolean copyEntrepotsContacts,
            Boolean copyEntrepots) {

        GeoClient clone = this.entityManager.find(GeoClient.class, clientID);

        // target should not already exist
        if (this.repository.findOne((root, cq, cb) -> cb.and(
                cb.equal(root.get("code"), clone.getCode()),
                cb.equal(root.get("societe").get("id"), toSocieteID))).isPresent())
            throw new RuntimeException(
                    "Le client " + clone.getCode() + " existe déjà pour la société " + toSocieteID);

        GeoSociete targetSociete = this.societeRepository.getOne(toSocieteID);
        GeoClient target = new GeoClient();
        BeanUtils.copyProperties(clone, target);
        target.setId(null);
        target.setDateModification(null);
        target.setUserModification(null);
        target.setValide(false);
        target.setSociete(targetSociete);

        // shared references unauthorized
        target.updateCertifications(null);
        target.setHistorique(null);
        target.setModifications(null);
        target.setContacts(null);
        target.setEntrepots(null);
        GeoClient savedTarget = this.repository.save(target);
        this.entityManager.flush();

        Set<GeoCertificationClient> certifications = clone.getCertifications().stream().map(certification -> {
            this.entityManager.detach(certification);
            certification.setId(null);
            this.entityManager.persist(certification);
            return certification;
        }).collect(Collectors.toSet());
        savedTarget.setCertifications(certifications);

        if (copyEntrepots) {
            List<GeoEntrepot> entrepots = this.entrepotRepository
                    .findAll((root, cq, cb) -> cb.and(
                            cb.equal(root.get("client").get("id"), clientID),
                            // cb.equal(root.get("societe").get("id"), fromSocieteID),
                            // cb.equal(root.get("valide"), true),
                            // on exclu l'entrepot qui a le meme code que le client
                            // parce que sa duplication est automatique
                            cb.notEqual(root.get("code"), target.getCode())))
                    .stream()
                    .map(entrepot -> {
                        this.entityManager.detach(entrepot);
                        entrepot.setId(null);
                        entrepot.setClient(target);
                        entrepot.setSociete(targetSociete);
                        entrepot.setContacts(null);
                        if (copyEntrepotsContacts) {
                            List<GeoContact> contactsEntrepot = this.contactRepository
                                    .findAll((root, cq, cb) -> cb.and(
                                            cb.equal(root.get("codeTiers"), entrepot.getCode()),
                                            cb.equal(root.get("societe").get("id"), fromSocieteID),
                                            // cb.equal(root.get("valide"), true),
                                            cb.equal(root.get("typeTiers"), 'E')))
                                    .stream()
                                    .map(contact -> {
                                        this.entityManager.detach(contact);
                                        contact.setId(null);
                                        contact.setSociete(targetSociete);
                                        val cli = new GeoClient();
                                        cli.setId(savedTarget.getId());
                                        contact.setClient(cli);
                                        contact.setEntrepot(entrepot);
                                        return this.contactRepository.save(contact);
                                    }).collect(Collectors.toList());
                            entrepot.setContacts(contactsEntrepot);
                        }
                        return this.entrepotRepository.save(entrepot);
                    }).collect(Collectors.toList());
            savedTarget.setEntrepots(entrepots);
        }

        if (copyContacts) {
            List<GeoContact> contacts = this.contactRepository
                    .findAll((root, cq, cb) -> cb.and(
                            cb.equal(root.get("codeTiers"), target.getCode()),
                            cb.equal(root.get("societe").get("id"), fromSocieteID),
                            // cb.equal(root.get("valide"), true),
                            cb.equal(root.get("typeTiers"), 'C')))
                    .stream()
                    .map(contact -> {
                        this.entityManager.detach(contact);
                        contact.setId(null);
                        contact.setSociete(targetSociete);
                        contact.setClient(savedTarget);
                        this.entityManager.persist(contact);
                        return contact;
                    }).collect(Collectors.toList());
            savedTarget.setContacts(contacts);
        }

        return this.repository.save(savedTarget);

    }
}
