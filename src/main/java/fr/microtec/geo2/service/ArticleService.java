package fr.microtec.geo2.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cz.jirutka.rsql.parser.RSQLParser;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleCahierDesCharge;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleCahierDesChargeRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleEmballageRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleMatierePremiereRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleNormalisationRepository;
import fr.microtec.geo2.persistance.repository.produits.GeoArticleRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import fr.microtec.geo2.service.graphql.produits.GeoArticleGraphQLService;

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

	private <T extends Duplicable<T>> T fetch(GeoRepository<T, String> repository, T entity) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id", "dateCreation", "dateModification",
				"userCreation", "userModification", "valide");
		Example<T> example = Example.of(entity, matcher);
		List<T> entitiesFound = repository.findAll(example);

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
	 * This function is used to fetch distinct records from the database
	 * 
	 * @param clazz      The class of the entity you want to fetch.
	 * @param repository The GeoRepository to use.
	 * @param pageable   The pageable object that will be used to paginate the
	 *                   results.
	 * @param search     The search string.
	 * @return A RelayPage<T>
	 */
	public <T, K extends Serializable> RelayPage<T> fetchDistinct(
			final Class<T> clazz,
			final GeoRepository<T, K> repository,
			Pageable pageable,
			String search) {
		if (pageable == null)
			pageable = PageRequest.of(0, 20);

		Specification<T> spec = Specification.where(null);

		spec = (root, query, cb) -> {
			query = query.distinct(true);
			return null;
		};

		if (search != null && !search.isBlank())
			spec = spec.and(this.rsqlParser.parse(search).accept(new GeoCustomVisitor<>()));

		Page<T> page = repository.findAll(spec, pageable);

		return PageFactory.asRelayPage(page);

	}

}
