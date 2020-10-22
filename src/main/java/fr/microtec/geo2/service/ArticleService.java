package fr.microtec.geo2.service;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleCahierDesCharge;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleMatierePremiere;
import fr.microtec.geo2.persistance.entity.produits.GeoArticleNormalisation;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import fr.microtec.geo2.persistance.repository.produits.*;
import fr.microtec.geo2.service.graphql.produits.GeoArticleGraphQLService;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Optional;

import javax.persistence.EntityManager;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;

@Service
public class ArticleService {

	private final GeoArticleRepository articleRepository;
	private final GeoArticleMatierePremiereRepository matierePremiereRepository;
	private final GeoArticleCahierDesChargeRepository cahierDesChargeRepository;
	private final GeoArticleNormalisationRepository normalisationRepository;
	private final GeoArticleEmballageRepository emballageRepository;
	private final EntityManager entityManager;

	public ArticleService(
			GeoArticleRepository articleRepository,
			GeoArticleMatierePremiereRepository matierePremiereRepository,
			GeoArticleCahierDesChargeRepository cahierDesChargeRepository,
			GeoArticleNormalisationRepository normalisationRepository,
			GeoArticleEmballageRepository emballageRepository,
			EntityManager entityManager
	) {
		this.articleRepository = articleRepository;
		this.matierePremiereRepository = matierePremiereRepository;
		this.cahierDesChargeRepository = cahierDesChargeRepository;
		this.normalisationRepository = normalisationRepository;
		this.emballageRepository = emballageRepository;
		this.entityManager = entityManager;
	}

	public GeoArticle save(GeoArticle articleChunk, Boolean clone) {
		GeoArticle merged = new GeoArticle();
		EntityGraph articleGraph = EntityGraphUtils
		.fromAttributePaths("valide","description","blueWhaleStock","matierePremiere","emballage","cahierDesCharge","normalisation");
		Optional<GeoArticle> article = this.articleRepository.findById(articleChunk.getId(),articleGraph);

		Optional<GeoArticleMatierePremiere> mergedMatierePremiere = Optional.empty();
		if (articleChunk.getMatierePremiere() != null) {
			EntityGraph matieresPremieresGraph = EntityGraphUtils
			.fromAttributePaths("espece","variete","calibreFournisseur","calibreUnifie","origine","modeCulture","type","typeVente");
			Optional<GeoArticleMatierePremiere> matierePremiere = this.matierePremiereRepository
			.findById(article.get().getMatierePremiere().getId(),matieresPremieresGraph);
			mergedMatierePremiere = Optional.of(GeoArticleGraphQLService
			.merge(articleChunk.getMatierePremiere(), matierePremiere.get(), null));
			this.entityManager.detach(article.get().getMatierePremiere());
		}

		Optional<GeoArticleCahierDesCharge> mergedCahierDesCharges = Optional.empty();
		if (articleChunk.getCahierDesCharge() != null) {
			EntityGraph cahierDesChargesGraph = EntityGraphUtils
			.fromAttributePaths("espece","categorie","coloration","sucre","penetro","cirage","rangement");
			Optional<GeoArticleCahierDesCharge> cahierDesCharges = this.cahierDesChargeRepository
			.findById(article.get().getCahierDesCharge().getId(),cahierDesChargesGraph);
			mergedCahierDesCharges = Optional.of(GeoArticleGraphQLService
			.merge(articleChunk.getCahierDesCharge(), cahierDesCharges.get(), null));
			this.entityManager.detach(article.get().getCahierDesCharge());
		}

		Optional<GeoArticleEmballage> mergedEmballage = Optional.empty();
		if (articleChunk.getEmballage() != null) {
			EntityGraph emballagesGraph = EntityGraphUtils
			.fromAttributePaths("espece","emballage","conditionSpecial","alveole","marque");
			Optional<GeoArticleEmballage> emballage = this.emballageRepository
			.findById(article.get().getEmballage().getId(),emballagesGraph);
			mergedEmballage = Optional.of(GeoArticleGraphQLService
			.merge(articleChunk.getEmballage(), emballage.get(), null));
			this.entityManager.detach(article.get().getEmballage());
		}

		Optional<GeoArticleNormalisation> mergedNormalisation = Optional.empty();
		if (articleChunk.getNormalisation() != null) {
			EntityGraph normalisationsGraph = EntityGraphUtils
			.fromAttributePaths("espece","calibreMarquage","stickeur","etiquetteColis","etiquetteUc","etiquetteEvenementielle","identificationSymbolique","marque");
			Optional<GeoArticleNormalisation> normalisation = this.normalisationRepository
			.findById(article.get().getNormalisation().getId(),normalisationsGraph);
			mergedNormalisation = Optional.of(GeoArticleGraphQLService
			.merge(articleChunk.getNormalisation(), normalisation.get(), null));
			this.entityManager.detach(article.get().getNormalisation());
		}
		
		merged = GeoArticleGraphQLService.merge(article.get(), articleChunk.duplicate(), null);
		if (mergedMatierePremiere.isPresent())
			merged.setMatierePremiere(this.fetch(this.matierePremiereRepository,mergedMatierePremiere.get()));
		if (mergedCahierDesCharges.isPresent())
			merged.setCahierDesCharge(this.fetch(this.cahierDesChargeRepository,mergedCahierDesCharges.get()));
		if (mergedEmballage.isPresent())
			merged.setEmballage(this.fetch(this.emballageRepository,mergedEmballage.get()));
		if (mergedNormalisation.isPresent())
			merged.setNormalisation(this.fetch(this.normalisationRepository,mergedNormalisation.get()));
		
		if (articleChunk.getValide() != null)
			merged.setValide(articleChunk.getValide());
		if (articleChunk.getBlueWhaleStock() != null)
			merged.setBlueWhaleStock(articleChunk.getBlueWhaleStock());
		if (articleChunk.getDescription() != null)
			merged.setDescription(articleChunk.getDescription());
			
		if (clone) {
			this.entityManager.detach(merged);
			merged = merged.duplicate();
			merged.setValide(false);
		}
		return this.articleRepository.save(merged);
	}

	private <T extends Duplicable<T>> T fetch(GeoGraphRepository<T, String> repository, T entity) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths( "id","dateCreation","dateModification","userCreation","userModification","valide" );
		Example<T> example = Example.of(entity, matcher);
		Optional<T> entityOptional = repository.findOne(example);

		return entityOptional.isPresent() ?
			entityOptional.get() :
			repository.save(entity.duplicate());
	}

}