package fr.microtec.geo2.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.stock.GeoStock;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeKey;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeSpecifications;
import fr.microtec.geo2.persistance.entity.stock.GeoStockMouvement;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoStockArticleAgeRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoStockMouvementRepository;
import fr.microtec.geo2.persistance.repository.stock.GeoStockRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;

@Service()
public class StockService extends GeoAbstractGraphQLService<GeoStockArticleAge, GeoStockArticleAgeKey> {

    private final GeoStockArticleAgeRepository stockArticleAgeRepository;
    private final GeoStockRepository stockRepository;
    private final GeoFunctionOrdreRepository functionRepo;
    private final GeoOrdreRepository ordreRepo;
    private final GeoOrdreLigneRepository ordreLigneRepo;
    private final GeoStockMouvementRepository stockMouvementRepo;
    private final SecurityService securityService;

    public StockService(
            GeoStockArticleAgeRepository stockArticleAgeRepository,
            GeoFunctionOrdreRepository functionsRepo,
            GeoOrdreRepository ordreRepo,
            GeoStockRepository stockRepository,
            GeoOrdreLigneRepository ordreLigneRepo,
            GeoStockMouvementRepository stockMouvementRepo,
            SecurityService securityService) {
        super(stockArticleAgeRepository, GeoStockArticleAge.class);
        this.stockArticleAgeRepository = stockArticleAgeRepository;
        this.functionRepo = functionsRepo;
        this.ordreRepo = ordreRepo;
        this.ordreLigneRepo = ordreLigneRepo;
        this.stockMouvementRepo = stockMouvementRepo;
        this.securityService = securityService;
        this.stockRepository = stockRepository;
    }

    public RelayPage<GeoStockArticleAge> fetchStockArticleAge(GeoSociete societe, List<GeoSecteur> secteurs,
            List<GeoClient> clients, List<GeoFournisseur> fournisseurs, String search, Pageable pageable) {
        Page<GeoStockArticleAge> page;

        if (pageable == null)
            pageable = PageRequest.of(0, 20);

        Specification<GeoStockArticleAge> spec = GeoStockArticleAgeSpecifications.withDistinctArticleInOrdreLigne();

        if (societe != null)
            spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInSociete(societe));

        if (secteurs != null)
            spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInSecteurs(secteurs));

        if (clients != null)
            spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInClients(clients));

        if (fournisseurs != null)
            spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInFournisseurs(fournisseurs));

        if (search != null && !search.isBlank())
            spec = spec.and(this.parseSearch(search));

        page = this.stockArticleAgeRepository.findAll(spec, pageable); // , GeoEntityGraph.getEntityGraph(env));

        return PageFactory.asRelayPage(page);
    }

    public FunctionResult reservationStock(
            String ordreId,
            String articleId,
            String societeId,
            String stockId,
            Integer quantite,
            String commentaire) {

        // Initialize ordre ligne
        FunctionResult res = this.functionRepo.ofInitArticle(ordreId, articleId, societeId);
        String newligneRef = res.getData().get("new_orl_ref").toString();

        // Update generated row with history values
        if (res.getRes() == 1) {
            GeoOrdreLigne ligne = this.ordreLigneRepo.getOne(newligneRef);
            GeoStock stock = this.stockRepository.getOne(stockId);
            GeoUtilisateur utilisateur = this.securityService.getUser();

            // Create mouvement
            GeoStockMouvement mouvement = new GeoStockMouvement();
            mouvement.setQuantite(quantite);
            mouvement.setDescription(commentaire);
            mouvement.setOrdre(this.ordreRepo.getOne(ordreId));
            mouvement.setStock(stock);
            mouvement.setOrdreLigne(ligne);
            mouvement.setNomUtilisateur(utilisateur.getUsername());
            mouvement.setArticle(ligne.getArticle());
            mouvement.setType('R');
            mouvement = stockMouvementRepo.save(mouvement);

            // Update ordre ligne
            ligne.setStockMouvement(mouvement.getId());
            ligne.setNombreColisCommandes(quantite.floatValue());
            ligne.setProprietaireMarchandise(stock.getProprietaire());
            ligne.setFournisseur(stock.getFournisseur());
            ligne.setNombreReservationsSurStock(stock.getQuantiteInitiale() - stock.getQuantiteReservee() < 0 ? -1 : 1);
            ligne.setBureauAchat(ligne.getFournisseur().getBureauAchat());
            ligne = this.ordreLigneRepo.save(ligne);

            res = this.functionRepo.onChangeCdeNbCol(newligneRef, utilisateur.getUsername());
            res = this.functionRepo.setTransporteurBassin(newligneRef, societeId);
            res = this.functionRepo.fVerifLogistiqueOrdre(ordreId);
        }

        return res;
    }

}
