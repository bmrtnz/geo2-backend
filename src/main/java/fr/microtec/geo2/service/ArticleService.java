package fr.microtec.geo2.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import cz.jirutka.rsql.parser.RSQLParser;
import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleCahierDesCharge;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleCahierDesChargeRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleEmballageRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleMatierePremiereRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleNormalisationRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticlePartRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import fr.microtec.geo2.service.graphql.produits.GeoArticleGraphQLService;
import io.leangen.graphql.execution.ResolutionEnvironment;

@Service
public class ArticleService {

    public static final String codeCNUF = "343006";
    private final RSQLParser rsqlParser;

    private final GeoArticleRepository articleRepository;
    private final GeoArticleMatierePremiereRepository matierePremiereRepository;
    private final GeoArticleCahierDesChargeRepository cahierDesChargeRepository;
    private final GeoArticleNormalisationRepository normalisationRepository;
    private final GeoArticleEmballageRepository emballageRepository;
    private final EntityManager entityManager;

    public ArticleService(
            RSQLParser rsqlParser,
            GeoArticleRepository articleRepository,
            GeoArticleMatierePremiereRepository matierePremiereRepository,
            GeoArticleCahierDesChargeRepository cahierDesChargeRepository,
            GeoArticleNormalisationRepository normalisationRepository,
            GeoArticleEmballageRepository emballageRepository,
            EntityManager entityManager) {
        this.rsqlParser = rsqlParser;
        this.articleRepository = articleRepository;
        this.matierePremiereRepository = matierePremiereRepository;
        this.cahierDesChargeRepository = cahierDesChargeRepository;
        this.normalisationRepository = normalisationRepository;
        this.emballageRepository = emballageRepository;
        this.entityManager = entityManager;
    }

    public GeoArticle save(GeoArticle articleChunk, Boolean clone) {
        GeoArticle merged = new GeoArticle();
        Optional<GeoArticle> article = this.articleRepository.findById(articleChunk.getId()); // ,articleGraph);

        Optional<GeoArticleMatierePremiere> mergedMatierePremiere = Optional.empty();
        if (articleChunk.getMatierePremiere() != null) {
            Optional<GeoArticleMatierePremiere> matierePremiere = this.matierePremiereRepository
                    .findById(article.get().getMatierePremiere().getId()); // ,matieresPremieresGraph);
            mergedMatierePremiere = Optional.of(GeoArticleGraphQLService
                    .merge(articleChunk.getMatierePremiere(), matierePremiere.get(), null));
            this.entityManager.detach(article.get().getMatierePremiere());
        }

        Optional<GeoArticleCahierDesCharge> mergedCahierDesCharges = Optional.empty();
        if (articleChunk.getCahierDesCharge() != null) {
            Optional<GeoArticleCahierDesCharge> cahierDesCharges = this.cahierDesChargeRepository
                    .findById(article.get().getCahierDesCharge().getId()); // ,cahierDesChargesGraph);
            mergedCahierDesCharges = Optional.of(GeoArticleGraphQLService
                    .merge(articleChunk.getCahierDesCharge(), cahierDesCharges.get(), null));
            this.entityManager.detach(article.get().getCahierDesCharge());
        }

        Optional<GeoArticleEmballage> mergedEmballage = Optional.empty();
        if (articleChunk.getEmballage() != null) {
            Optional<GeoArticleEmballage> emballage = this.emballageRepository
                    .findById(article.get().getEmballage().getId()); // ,emballagesGraph);
            mergedEmballage = Optional.of(GeoArticleGraphQLService
                    .merge(articleChunk.getEmballage(), emballage.get(), null));
            this.entityManager.detach(article.get().getEmballage());
        }

        Optional<GeoArticleNormalisation> mergedNormalisation = Optional.empty();
        if (articleChunk.getNormalisation() != null) {
            Optional<GeoArticleNormalisation> normalisation = this.normalisationRepository
                    .findById(article.get().getNormalisation().getId()); // ,normalisationsGraph);
            mergedNormalisation = Optional.of(GeoArticleGraphQLService
                    .merge(articleChunk.getNormalisation(), normalisation.get(), null));
            this.entityManager.detach(article.get().getNormalisation());
        }

        merged = GeoArticleGraphQLService.merge(article.get(), articleChunk.duplicate(), null);
        if (mergedMatierePremiere.isPresent())
            merged.setMatierePremiere(this.fetch(this.matierePremiereRepository, mergedMatierePremiere.get()));
        if (mergedCahierDesCharges.isPresent())
            merged.setCahierDesCharge(this.fetch(this.cahierDesChargeRepository, mergedCahierDesCharges.get()));
        if (mergedEmballage.isPresent())
            merged.setEmballage(this.fetch(this.emballageRepository, mergedEmballage.get()));
        if (mergedNormalisation.isPresent())
            merged.setNormalisation(this.fetch(this.normalisationRepository, mergedNormalisation.get()));

        if (articleChunk.getValide() != null)
            merged.setValide(articleChunk.getValide());
        if (articleChunk.getPreSaisie() != null)
            merged.setPreSaisie(articleChunk.getPreSaisie());
        if (articleChunk.getBlueWhaleStock() != null)
            merged.setBlueWhaleStock(articleChunk.getBlueWhaleStock());
        if (articleChunk.getDescription() != null)
            merged.setDescription(articleChunk.getDescription());
        if (articleChunk.getArticleAssocie() != null)
            merged.setArticleAssocie(articleChunk.getArticleAssocie());

        if (clone) {
            this.entityManager.detach(merged);
            merged = merged.duplicate();
            merged.setValide(false);
            merged.setPreSaisie(true);
        }

        GeoArticle saved = this.articleRepository.save(merged);

        // update GTIN
        String gtin = this.genGTIN(saved);
        if (saved.getEmballage().getUniteParColis() > 0) {
            saved.setGtinUcBlueWhale(gtin);
            saved.setGtinColisBlueWhale(null);
        } else {
            saved.setGtinColisBlueWhale(gtin);
            saved.setGtinUcBlueWhale(null);
        }
        saved = this.articleRepository.save(saved);

        return saved;
    }

    private <T extends Duplicable<T>, R extends GeoArticlePartRepository<T, String>> T fetch(R repository, T entity) {
        List<T> entitiesFound = repository.findAll(repository.getArticleMatchSpecification(entity));

        if (entitiesFound.isEmpty())
            return repository.save(entity.duplicate());

        return entitiesFound.get(0);
    }

    public String genGTIN(GeoArticle article) {
        String code = codeCNUF + article.getId();
        Integer imp = 0;
        Integer pai = 0;
        Integer check = 0;

        for (int i = 0; i < code.length(); i += 2)
            imp += (int) code.charAt(i);
        for (int i = 1; i < code.length(); i += 2)
            pai += (int) code.charAt(i);

        check = imp + (3 * pai);
        check = (int) (Math.ceil(check.doubleValue() / 10) * 10 - check);

        return code + check;
    }

    /**
     * Fetch a distinct list of entities from the database, with pagination and
     * search/filter capabilities
     *
     * @param clazz    The class of the entity to be fetched.
     * @param pageable The Pageable object that is used to paginate the results.
     * @param search   The search string.
     * @return A Page of T.
     */
    public <T> RelayPage<T> fetchDistinct(final Class<T> clazz, Pageable pageable, String search,
            ResolutionEnvironment env) {

        Set<String> fields = CustomUtils.parseSelectFromPagedEnv(env);

        // Define criteria tools
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // Apply ordering
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        if (sort.isSorted()) {
            query.orderBy(CriteriaUtils.toOrders(sort, CriteriaUtils.findRoot(query, clazz), builder));
        }

        // Handle search/filter
        Specification<?> spec = null;
        if (search != null && !search.isBlank())
            spec = this.rsqlParser.parse(search).accept(new GeoCustomVisitor<>());

        // Build query
        List<Path<?>> selections = CustomUtils.getSelectionPaths(fields, root);
        query.multiselect(selections.stream().map(s -> (Selection<?>) s).collect(Collectors.toList()));
        if (spec != null)
            query.where(((Specification<T>) spec).toPredicate(root, query, builder));
        query
                .groupBy(selections.stream().map(s -> (Expression<?>) s).collect(Collectors.toList()))
                .distinct(true);
        // .distinct(true);
        TypedQuery<T> selectionQuery = this.entityManager.createQuery(query);

        // Add pagination to query
        if (pageable.isPaged()) {
            selectionQuery.setFirstResult((int) pageable.getOffset());
            selectionQuery.setMaxResults(pageable.getPageSize());
        }

        // Build count query
        CriteriaQuery<Long> cQuery = builder.createQuery(Long.class);
        Root<Long> cRoot = (Root<Long>) CriteriaUtils.applySpecification(builder, cQuery, clazz, spec);
        cQuery.select(builder.count(cRoot));
        if (spec != null)
            cQuery.where(((Specification<Long>) spec).toPredicate(cRoot, cQuery, builder));
        cQuery.groupBy(CustomUtils.getSelectionExpressions(fields, cRoot));
        TypedQuery<Long> countQuery = this.entityManager.createQuery(cQuery);

        // Create Page from result
        Page<T> page = PageableExecutionUtils.getPage(
                selectionQuery.getResultList(),
                pageable,
                () -> CustomUtils.executeCountQuery(countQuery));

        return PageFactory.asRelayPage(page);

    }

}
