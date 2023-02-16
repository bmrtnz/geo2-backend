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
            String prompt_frais_annexe,
            String prompt_avoir_client,
            String prompt_create_avoir_client) {
        FunctionQuery query = this.build("OF_CLOTURE_LITIGE_CLIENT");

        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("prompt_frais_annexe", String.class, prompt_frais_annexe);
        query.attachInput("prompt_avoir_client", String.class, prompt_avoir_client);
        query.attachInput("prompt_create_avoir_client", String.class, prompt_create_avoir_client);

        return query.fetch();
    }

    @Override
    public FunctionResult ofClotureLitigeResponsable(
            String is_cur_lit_ref,
            String arg_soc_code,
            String prompt_frais_annexe,
            String prompt_avoir_fourni,
            String prompt_create_avoir_fourni) {
        FunctionQuery query = this.build("OF_CLOTURE_LITIGE_RESPONSABLE");

        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("prompt_frais_annexe", String.class, prompt_frais_annexe);
        query.attachInput("prompt_avoir_fourni", String.class, prompt_avoir_fourni);
        query.attachInput("prompt_create_avoir_fourni", String.class, prompt_create_avoir_fourni);

        return query.fetch();
    }

    @Override
    public FunctionResult ofClotureLitigeGlobale(
            String is_cur_lit_ref,
            String arg_soc_code,
            String prompt_frais_annexe,
            String prompt_avoir_client,
            String prompt_avoir_globale,
            String prompt_create_avoir_global) {
        FunctionQuery query = this.build("OF_CLOTURE_LITIGE_GLOBALE");

        query.attachInput("is_cur_lit_ref", String.class, is_cur_lit_ref);
        query.attachInput("arg_soc_code", String.class, arg_soc_code);
        query.attachInput("prompt_frais_annexe", String.class, prompt_frais_annexe);
        query.attachInput("prompt_avoir_client", String.class, prompt_avoir_client);
        query.attachInput("prompt_avoir_globale", String.class, prompt_avoir_globale);
        query.attachInput("prompt_create_avoir_global", String.class, prompt_create_avoir_global);

        return query.fetch();
    }

}
