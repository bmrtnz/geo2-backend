package fr.microtec.geo2.persistance.repository.litige;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.repository.function.AbstractFunctionsRepositoryImpl;
import fr.microtec.geo2.persistance.repository.function.FunctionQuery;

@Repository
public class GeoFunctionLitigeRepositoryImpl extends AbstractFunctionsRepositoryImpl
        implements GeoFunctionLitigeRepository {
    @Override
    public FunctionResult ofClotureLitigeClient(
            String is_cur_lit_ref,
            String arg_soc_code,
            String arg_username,
            String prompt_frais_annexe,
            String prompt_avoir_client,
            String prompt_create_avoir_client) {
        FunctionQuery query = this.build("OF_CLOTURE_LITIGE_CLIENT");

        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("arg_username", String.class, arg_username);
        query.attachInput("prompt_frais_annexe", String.class, prompt_frais_annexe);
        query.attachInput("prompt_avoir_client", String.class, prompt_avoir_client);
        query.attachInput("prompt_create_avoir_client", String.class, prompt_create_avoir_client);
        query.attachOutput("triggered_prompt", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofClotureLitigeResponsable(
            String is_cur_lit_ref,
            String arg_soc_code,
            String arg_username,
            String prompt_frais_annexe,
            String prompt_avoir_fourni,
            String prompt_create_avoir_fourni) {
        FunctionQuery query = this.build("OF_CLOTURE_LITIGE_RESPONSABLE");

        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("arg_username", String.class, arg_username);
        query.attachInput("prompt_frais_annexe", String.class, prompt_frais_annexe);
        query.attachInput("prompt_avoir_fourni", String.class, prompt_avoir_fourni);
        query.attachInput("prompt_create_avoir_fourni", String.class, prompt_create_avoir_fourni);
        query.attachOutput("triggered_prompt", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofClotureLitigeGlobale(
            String is_cur_lit_ref,
            String arg_soc_code,
            String arg_username,
            String prompt_frais_annexe,
            String prompt_avoir_client,
            String prompt_avoir_global,
            String prompt_create_avoir_global) {
        FunctionQuery query = this.build("OF_CLOTURE_LITIGE_GLOBALE");

        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("arg_username", String.class, arg_username);
        query.attachInput("prompt_frais_annexe", String.class, prompt_frais_annexe);
        query.attachInput("prompt_avoir_client", String.class, prompt_avoir_client);
        query.attachInput("prompt_avoir_global", String.class, prompt_avoir_global);
        query.attachInput("prompt_create_avoir_global", String.class, prompt_create_avoir_global);
        query.attachOutput("triggered_prompt", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofSauveLitige(String arg_lit_ref) {
        FunctionQuery query = this.build("OF_SAUVE_LITIGE");

        query.attachInput("arg_lit_ref", String.class, arg_lit_ref);

        return query.fetch();
    }

    @Override
    public FunctionResult ofChronoLitige(String is_cur_ord_ref) {
        FunctionQuery query = this.build("OF_CHRONO_LITIGE");

        query.attachInput("is_cur_ord_ref", String.class, is_cur_ord_ref);
        query.attachOutput("is_cur_lit_ref", String.class);

        return query.fetch();
    }

    @Override
    public FunctionResult ofLitigeCtlClientInsert(
            String gs_soc_code,
            String gs_ord_ref,
            String arg_lit_ref) {
        FunctionQuery query = this.build("OF_LITIGE_CTL_CLIENT_INSERT");

        query.attachInput("gs_soc_code", String.class, gs_soc_code);
        query.attachInput("gs_ord_ref", String.class, gs_ord_ref);
        query.attachInput("arg_lit_ref", String.class, arg_lit_ref);

        return query.fetch();
    }

    @Override
    public FunctionResult ofInitLigneLitige(
            String arg_list_ref,
            String is_cur_lit_ref,
            String is_orl_lit) {
        FunctionQuery query = this.build("OF_INIT_LIGNE_LITIGE");

        query.attachInput("arg_list_ref", String.class, arg_list_ref);
        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("is_orl_lit", String.class, is_orl_lit);
        query.attachOutput("ll_nb_ligne", String.class);

        return query.fetch();
    }

}
