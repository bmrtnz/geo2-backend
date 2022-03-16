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
public class GeoFunctionOrdreRepositoryImpl extends AbstractFunctionsRepositoryImpl implements GeoFunctionOrdreRepository {

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
    public FunctionResult fRecupFrais(String varCode, String catCode, String scoCode, String tvtCode, Integer modeCulture, String origine) {
        FunctionQuery query = this.build("F_RECUP_FRAIS");

        query.attachInput("arg_var_code", String.class, varCode);
        query.attachInput("arg_cat_code", String.class, catCode);
        query.attachInput("arg_sco_code", String.class, scoCode);
        query.attachInput("arg_tvt_code", String.class, tvtCode);
        query.attachInput("arg_mode_culture", Integer.class, modeCulture);
        query.attachInput("arg_origine", String.class, origine);

        return query.fetch();
    }

    //@Override
    public FunctionResult fCalculQte(String argOrdRef, String argOrlRef, Float argPdsBrut, Float argPdsNet, Integer argAchQte, Integer argVteQte) {
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
    public FunctionResult fAfficheOrdreBaf(String socCode, String scoCode, String cliRef, String cenRef, LocalDate dateMin, LocalDate dateMax, String codeAss, String codeCom) {
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
        query.attachOutput("bloquer", Character.class, v -> ((Character)v).equals('O'));

        return query.fetch();
    }
}
