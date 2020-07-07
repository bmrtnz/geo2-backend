package fr.microtec.geo2.service;

import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import fr.microtec.geo2.persistance.repository.produits.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticleService {

	private final GeoArticleRepository articleRepository;
	private final GeoArticleMatierePremiereRepository matierePremiereRepository;
	private final GeoArticleCahierDesChargeRepository cahierDesChargeRepository;
	private final GeoArticleNormalisationRepository normalisationRepository;
	private final GeoArticleEmballageRepository emballageRepository;

	public ArticleService(
			GeoArticleRepository articleRepository,
			GeoArticleMatierePremiereRepository matierePremiereRepository,
			GeoArticleCahierDesChargeRepository cahierDesChargeRepository,
			GeoArticleNormalisationRepository normalisationRepository,
			GeoArticleEmballageRepository emballageRepository
	) {
		this.articleRepository = articleRepository;
		this.matierePremiereRepository = matierePremiereRepository;
		this.cahierDesChargeRepository = cahierDesChargeRepository;
		this.normalisationRepository = normalisationRepository;
		this.emballageRepository = emballageRepository;
	}

	public GeoArticle save(GeoArticle article) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id");

		article.setMatierePremiere(this.save(this.matierePremiereRepository, article.getMatierePremiere(), matcher));
		article.setCahierDesCharge(this.save(this.cahierDesChargeRepository, article.getCahierDesCharge(), matcher));
		article.setNormalisation(this.save(this.normalisationRepository, article.getNormalisation(), matcher));
		article.setEmballage(this.save(this.emballageRepository, article.getEmballage(), matcher));

		return this.articleRepository.save(article);
	}

	private <T> T save(GeoGraphRepository<T, String> repository, T entity, ExampleMatcher matcher) {
		Example<T> example = Example.of(entity, matcher);
		Optional<T> entityOptional = repository.findOne(example);

		return entityOptional.orElse(repository.save(entity));
	}

}
