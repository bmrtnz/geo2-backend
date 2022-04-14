package fr.microtec.geo2.persistance.repository.ordres;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.repository.function.AbstractFunctionsRepositoryImpl;
import fr.microtec.geo2.persistance.repository.function.FunctionQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public class GeoFunctionOrdreRepositoryImpl extends AbstractFunctionsRepositoryImpl
        implements GeoFunctionOrdreRepository {

    @Override
    public FunctionResult ofValideEntrepotForOrdre(String code_entrepot) {
        FunctionQuery query = this.build("OF_VALIDE_ENTREPOT_FOR_ORDRE");

        query.attachInput("code_entrepot", String.class, code_entrepot);

        return query.fetch();
    }

    @Override
    public FunctionResult fCalculMarge(String refOrdre) {
        FunctionQuery query = this.build("F_CALCUL_MARGE");

        query.attachInput("arg_ord_ref", String.class, refOrdre);

        return query.fetch();
    }

    @Override
    public FunctionResult fCalculPerequation(String refOrdre, String codeSociete) {
        FunctionQuery query = this.build("F_CALCUL_PEREQUATION");

        query.attachInput("arg_ord_ref", String.class, refOrdre);
        query.attachInput("arg_soc_code", String.class, codeSociete);

        return query.fetch();
    }

    @Override
    public FunctionResult fRecupFrais(String varCode, String catCode, String scoCode, String tvtCode,
            Integer modeCulture, String origine) {
        FunctionQuery query = this.build("F_RECUP_FRAIS");

        query.attachInput("arg_var_code", String.class, varCode);
        query.attachInput("arg_cat_code", String.class, catCode);
        query.attachInput("arg_sco_code", String.class, scoCode);
        query.attachInput("arg_tvt_code", String.class, tvtCode);
        query.attachInput("arg_mode_culture", Integer.class, modeCulture);
        query.attachInput("arg_origine", String.class, origine);

        return query.fetch();
    }

    // @Override
    public FunctionResult fCalculQte(String argOrdRef, String argOrlRef, Float argPdsBrut, Float argPdsNet,
            Integer argAchQte, Integer argVteQte) {
        FunctionQuery query = super.build("F_CALCUL_QTE");

        query
                .attachInput("arg_ord_ref", String.class, argOrdRef)
                .attachInput("arg_orl_ref", String.class, argOrlRef)
                .attachInputOutput("arg_pds_brut", Float.class, argPdsBrut)
                .attachInputOutput("arg_pds_net", Float.class, argPdsNet)
                .attachInputOutput("arg_ach_qte", Integer.class, argAchQte)
                .attachInputOutput("arg_vte_qte", Integer.class, argVteQte);

        return query.fetch();
    }

    @Override
    public FunctionResult fAfficheOrdreBaf(String socCode, String scoCode, String cliRef, String cenRef,
            LocalDate dateMin, LocalDate dateMax, String codeAss, String codeCom) {
        FunctionQuery query = this.build("F_AFFICHE_ORDRE_BAF", GeoOrdreBaf.class);

        query.attachInput("gs_soc_code", String.class, socCode);
        query.attachInput("is_sco_code", String.class, scoCode);
        query.attachInput("is_cli_ref", String.class, cliRef);
        query.attachInput("is_cen_ref", String.class, cenRef);
        query.attachInput("id_date_min", LocalDate.class, dateMin);
        query.attachInput("id_date_max", LocalDate.class, dateMax);
        query.attachInput("is_per_codeass", String.class, codeAss);
        query.attachInput("is_per_codecom", String.class, codeCom);
        query.attachCursor("c_ordre_baf");

        return query.fetch();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Require for skip error (Max cursor open) in oracle
    public FunctionResult fControlOrdreBaf(String refOrdre, String scoCode) {
        FunctionQuery query = this.build("F_CONTROL_ORDRE_BAF");

        query.attachInput("arg_ord_ref", String.class, refOrdre);
        query.attachInput("arg_soc_code", String.class, scoCode);

        query.attachOutput("ind_baf", String.class);
        query.attachOutput("ind_trp", String.class);
        query.attachOutput("ind_prix", String.class);
        query.attachOutput("ind_qte", String.class);
        query.attachOutput("ind_autre", String.class);
        query.attachOutput("ind_station", String.class);
        query.attachOutput("ind_date", String.class);
        query.attachOutput("desc_ctl", String.class);
        query.attachOutput("pc_marge_brute", BigDecimal.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fNouvelOrdre(String socCode) {
        FunctionQuery query = this.build("F_NOUVEL_ORDRE");

        query.attachInput("arg_soc_code", String.class, socCode);
        query.attachOutput("ls_nordre", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult fVerifOrdreWarning(String refOrdre, String socCode) {
        FunctionQuery query = this.build("F_VERIF_ORDRE_WARNING");

        query.attachInput("arg_ord_ref", String.class, refOrdre);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult ofInitArticle(String ordRef, String artRef, String socCode) {
        FunctionQuery query = this.build("OF_INIT_ARTICLE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_art_ref", String.class, artRef);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult fGenereDluo(String input, LocalDate dateExp, LocalDate dateLiv) {
        FunctionQuery query = this.build("F_GENERE_DLUO");

        query.attachInput("arg_param", String.class, input);
        query.attachInput("arg_datexp", LocalDate.class, dateExp);
        query.attachInput("arg_datliv", LocalDate.class, dateLiv);
        query.attachOutput("arg_dluo", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofInitArtrefGrp(String orlRef) {
        FunctionQuery query = this.build("OF_INIT_ARTREF_GRP");

        query.attachInput("cur_orl_ref", String.class, orlRef);

        return query.fetch();
    }

    @Override
    public FunctionResult fInitBlocageOrdre(String ordRef, String user) {
        FunctionQuery query = this.build("F_INIT_BLOCAGE_ORDRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("arg_user", String.class, user);
        query.attachOutput("bloquer", Character.class, v -> ((Character) v).equals('O'));

        return query.fetch();
    }

    @Override
    public FunctionResult ofRepartitionPalette(String ordreLigneRef, String secteurCode, String nomUtilisateur) {
        FunctionQuery query = this.build("OF_REPARTITION_PALETTE");

        query.attachInput("arg_orl_ref", String.class, ordreLigneRef);
        query.attachInput("gs_sco_code", String.class, secteurCode);
        query.attachInput("gs_user", String.class, nomUtilisateur);

        return query.fetch();
    }

    @Override
    public FunctionResult ofVerifLogistiqueDepart(String ordRef) {
        FunctionQuery query = this.build("OF_VERIF_LOGISTIQUE_DEPART");

        query.attachInput("arg_ord_ref", String.class, ordRef);

        return query.fetch();
    }

    @Override
    public FunctionResult ofCalculRegimeTvaEncours(String ordRef, String regimeTVA) {
        FunctionQuery query = this.build("OF_CALCUL_REGIME_TVA_ENCOURS");

        query.attachInput("arg_ord_ref", String.class, ordRef);
        query.attachInput("str_regime_tva_defaut", String.class, regimeTVA);

        return query.fetch();
    }

    @Override
    public FunctionResult ofSauveOrdre(String ordRef) {
        FunctionQuery query = this.build("OF_SAUVE_ORDRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);

        return query.fetch();
    }

    @Override
    public FunctionResult fVerifLogistiqueOrdre(String ordRef) {
        FunctionQuery query = this.build("F_VERIF_LOGISTIQUE_ORDRE");

        query.attachInput("arg_ord_ref", String.class, ordRef);

        return query.fetch();
    }

    @Override
    public FunctionResult fAjoutOrdlog(String orxRef, String typePassage, String choixPassage) {
        FunctionQuery query = this.build("F_AJOUT_ORDLOG");

        query.attachInput("arg_orx_ref", String.class, orxRef);
        query.attachInput("arg_typ_passage", String.class, typePassage);
        query.attachInput("arg_ch_passage", String.class, choixPassage);

        return query.fetch();
    }

    @Override
    public FunctionResult geoPrepareEnvois(
            String ordRef,
            String fluCode,
            Character modeAuto,
            Character annOrdre,
            String user) {
        FunctionQuery query = this.build("GEO_PREPARE_ENVOIS");

        query.attachInput("is_ord_ref", String.class, ordRef);
        query.attachInput("is_flu_code", String.class, fluCode);
        query.attachInput("mode_auto", Character.class, modeAuto);
        query.attachInput("ann_ordre", Character.class, annOrdre);
        query.attachInput("arg_nom_utilisateur", String.class, user);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeCdeNbPal(String orlRef, String scoCode) {
        FunctionQuery query = this.build("ON_CHANGE_CDE_NB_PAL");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_sco_code", String.class, scoCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeDemipalInd(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_DEMIPAL_IND");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangePalNbCol(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_PAL_NB_COL");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeCdeNbCol(String orlRef, String username) {
        FunctionQuery query = this.build("ON_CHANGE_CDE_NB_COL");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("gs_user", String.class, username);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeProprCode(String orlRef, String username, String socCode) {
        FunctionQuery query = this.build("ON_CHANGE_PROPR_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeFouCode(String orlRef, String username, String socCode) {
        FunctionQuery query = this.build("ON_CHANGE_FOU_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);
        query.attachInput("arg_soc_code", String.class, socCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangePalCode(String orlRef, String username, String scoCode) {
        FunctionQuery query = this.build("ON_CHANGE_PAL_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);
        query.attachInput("arg_user", String.class, username);
        query.attachInput("arg_sco_code", String.class, scoCode);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeVtePu(String orlRef) {
        FunctionQuery query = this.build("ON_CHANGE_VTE_PU");

        query.attachInput("arg_orl_ref", String.class, orlRef);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangePalinterCode(String orlRef) {
        FunctionQuery query = this.build("ON_CHANGE_PALINTER_CODE");

        query.attachInput("arg_orl_ref", String.class, orlRef);

        return query.fetch();
    }

    @Override
    public FunctionResult onChangeIndGratuit(String orlRef) {
        FunctionQuery query = this.build("ON_CHANGE_IND_GRATUIT");

        query.attachInput("arg_orl_ref", String.class, orlRef);

        return query.fetch();
    }
}
