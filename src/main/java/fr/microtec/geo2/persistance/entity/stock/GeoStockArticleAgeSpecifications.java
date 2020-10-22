package fr.microtec.geo2.persistance.entity.stock;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class GeoStockArticleAgeSpecifications {

	public static Specification<GeoStockArticleAge> byDistinctArticleInOrdreLigne() {
		return (root, criteriaQuery, criteriaBuilder) -> {

			// Select distinct articles
			// criteriaQuery.select(root.get("article")).distinct(true);

			// Join OrdreLignes
			Join<GeoStockArticleAge, GeoOrdreLigne> lignes = root.join("article");
			Predicate joinLignes = criteriaBuilder.equal(root.get("article"), lignes.get("article"));
			lignes = lignes.on(joinLignes);

			return null;
			// return root.get("article").in(lignes.get("article"));
		};
	}

}
