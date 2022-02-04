package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface GeoFunctionOrdreRepository {

    /**
     * Vérifie si la création de l'ordre pour l'entrepot est autorisé
     */
    FunctionResult ofValideEntrepotForOrdre(String code_entrepot);

    /**
     * Retourne les ordres BAF.
     */
    FunctionResult fAfficheOrdreBaf(String socCode, String scoCode, String cliRef, String cenRef, LocalDate dateMin, LocalDate dateMax, String codeAss, String codeCom);

    /**
     * Control les ordres BAF.
     */
    FunctionResult fControlOrdreBaf(String refOrdre, String socCode);

    /**
     * Génère une nouvelle ref ordre.
     */
    FunctionResult fNouvelOrdre(String socCode);

    /**
     * Génère une nouvelle ligne d'ordre avec l'article sélectionné.
     */
    FunctionResult ofInitArticle(String ordRef, String artRef, String socCode);

    // Sub procedures, declare only for testing
    FunctionResult fCalculMarge(String refOrdre);
    FunctionResult fRecupFrais(String varCode, String catCode, String scoCode, String tvtCode, Integer modeCulture, String origine);
    FunctionResult fCalculPerequation(String refOrdre, String codeSociete);
    FunctionResult fVerifOrdreWarning(String refOrdre, String socCode);
    FunctionResult fGenereDluo(String input, LocalDate dateExp, LocalDate dateLiv);
    FunctionResult ofInitArtrefGrp(String orlRef);

    //FunctionResult fCalculQte(String argOrdRef, String argOrlRef, Float argPdsBrut, Float argPdsNet, Integer argAchQte, Integer argVteQte);

}
