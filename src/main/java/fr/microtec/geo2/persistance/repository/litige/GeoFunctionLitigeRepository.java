package fr.microtec.geo2.persistance.repository.litige;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.FunctionResult;

@Repository
public interface GeoFunctionLitigeRepository {

    FunctionResult ofClotureLitigeClient(
            String is_cur_lit_ref,
            String arg_soc_code,
            String arg_username,
            String prompt_frais_annexe,
            String prompt_avoir_client,
            String prompt_create_avoir_client);

    FunctionResult ofClotureLitigeResponsable(
            String is_cur_lit_ref,
            String arg_soc_code,
            String arg_username,
            String prompt_frais_annexe,
            String prompt_avoir_fourni,
            String prompt_create_avoir_fourni);

    FunctionResult ofClotureLitigeGlobale(
            String is_cur_lit_ref,
            String arg_soc_code,
            String arg_username,
            String prompt_frais_annexe,
            String prompt_avoir_client,
            String prompt_avoir_global,
            String prompt_create_avoir_global);

    FunctionResult ofSauveLitige(String arg_lit_ref);

    FunctionResult ofChronoLitige(String is_cur_ord_ref);

    FunctionResult ofLitigeCtlClientInsert(
            String gs_soc_code,
            String gs_ord_ref,
            String arg_lit_ref);

}
