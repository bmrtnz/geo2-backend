package fr.microtec.geo2.persistance.entity.historique;

public interface GeoHistorique {

	enum GeoHistoriqueType { CLIENT, FOUNISSEUR, ARTICLE };

	String getId();

	String getCommentaire();

	String getUserModification();

	String getDateModification();

	Boolean getValide();

}
