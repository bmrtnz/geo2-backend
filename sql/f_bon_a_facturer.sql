CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_BON_A_FACTURER (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    li_ret number;
BEGIN
    -- correspond à f_bon_a_facturer.pbl deuxième partie
    res := 0;
    msg := '';

    -- Deja lancé par le frontend
    -- f_verif_ordre_warning(arg_ord_ref, arg_soc_code, res, msg);

    -- Consigne palox
    f_baf_gen_pallox(arg_ord_ref, arg_soc_code, res, msg);
    li_ret := res;

    -- Kit
    f_baf_kit_article(arg_ord_ref, res, msg);
    li_ret := li_ret + res;

    -- Kit eps
    f_baf_eps_article(arg_ord_ref, res, msg);
    li_ret := li_ret + res;

    -- facturation tesco
    fn_decl_frais_doua(arg_ord_ref, arg_soc_code, res, msg);
    fn_gen_tesco_factu(arg_ord_ref, res, msg);
    li_ret := li_ret + res;

    -- Traitement des lignes avec des colis expédiés et a ne pas facturer (NB_COLIS_MANQUANT)
    f_traite_colis_manquants(arg_ord_ref, res, msg);
    li_ret := li_ret + res;

    -- 5, chaque function doit renvoyer 1
    If li_ret = 5 Then
        f_update_bonafacturer(arg_ord_ref, res, msg);

        if res = 1 then
            msg := 'validation de l''ordre : l''ordre est à présent bon à facturer, il n''est plus modifiable';
            return;
        else
            msg := 'problème technique validation bon à facturer : ' || msg;
        end if;
    End If;

    --res := 0; -- Reset res

end F_BON_A_FACTURER;
/

