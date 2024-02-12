CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREE_ORDRE_REGULARISATION(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_lca_code IN GEO_LITCAU.LCA_CODE%TYPE,
    arg_typ_reg IN GEO_ORDRE.TYP_ORDRE%TYPE,
    arg_ind_detail IN varchar2,
    arg_username IN GEO_USER.NOM_UTILISATEUR%TYPE,
    arg_list_orl_ref IN P_STR_TAB_TYPE,
    res OUT number,
    msg OUT varchar2,
    ls_ord_ref_regul OUT GEO_ORDRE.ORD_REF%TYPE
)
AS
    ls_fou_code GEO_ORDLIG.FOU_CODE%TYPE;
    ls_flag_exped_fournni GEO_ORDLOG.FLAG_EXPED_FOURNNI%TYPE;

    ls_nordre_regul GEO_ORDRE.ORD_REF%TYPE;
    ls_list_ordre_regul GEO_ORDRE.LIST_NORDRE_REGUL%TYPE;
    ls_ret_lig GEO_ORDLIG.ORL_REF%TYPE;

    achat_devise_pu GEO_ORDLIG.ach_dev_pu%TYPE;
    achat_qte GEO_ORDLIG.ach_qte%TYPE;

BEGIN
    -- correspond à f_cree_ordre_regularisation.pbl
    res := 0;
    msg := '';

    for i in 1 .. arg_list_orl_ref.count
        loop
            select fou_code into ls_fou_code from geo_ordlig where ORL_REF = to_char(arg_list_orl_ref(i));

            begin
                select FLAG_EXPED_FOURNNI
                into ls_flag_exped_fournni
                from GEO_ORDLOG
                where  ORD_REF = arg_ord_ref and
                        FOU_CODE = ls_fou_code;
            exception when others then
                ls_flag_exped_fournni := 'N';
            end;

            If ls_flag_exped_fournni <> 'O'	 THEN
                msg := 'La station '  || ls_fou_code || ' n''a pas clôturé, la régularisation n''est pas possible.';
                return;
            End If;
        end loop;

    if (arg_list_orl_ref.COUNT > 0) then
        F_CREE_ORDRE_REGUL(arg_ord_ref, arg_soc_code, arg_lca_code, arg_typ_reg, arg_username, res, msg, ls_ord_ref_regul);

        If substr(msg, 1, 3) = '%%%' Then
            return;
        End If;

        for i in 1 .. arg_list_orl_ref.COUNT
            loop
                f_cree_ordre_regul_ligne(arg_ord_ref,arg_list_orl_ref(i),arg_soc_code, ls_ord_ref_regul,arg_ind_detail, res, msg, ls_ret_lig);

                If substr(msg, 1, 3) = '%%%' Then
					delete GEO_ORDRE where ORD_REF =ls_ord_ref_regul;
                    return;
                End If;

                If arg_ind_detail = 'O' Then

                    select ach_dev_pu, ach_qte into achat_devise_pu, achat_qte
                    from GEO_ORDLIG where orl_ref = to_char(arg_list_orl_ref(i));

                    update GEO_ORDLIG
                    set
                        ach_dev_pu = achat_devise_pu,
                        ach_qte = achat_qte
                    where
                            orl_ref = ls_ret_lig;

                else

                    update GEO_ORDLIG
                    set cde_nb_pal = 0, cde_nb_col = 0, pal_nb_col = 0, pal_nb_palinter = 0
                    where orl_ref = ls_ret_lig;

                end if;
                commit;
            end loop;

        msg := 'Updates done';
        return;
    Else
        msg := 'Veuillez selectionner au moins un article';
        return;
    end if;

    select NORDRE into ls_nordre_regul
    FROM GEO_ORDRE
    where ORD_REF= ls_ord_ref_regul;

    begin
        select LIST_NORDRE_REGUL into ls_list_ordre_regul
        from geo_ordre
        where ord_ref = arg_ord_ref;
    exception when others then
        ls_list_ordre_regul := '';
    end;

    update geo_ordre set LIST_NORDRE_REGUL = ls_list_ordre_regul where ord_ref = arg_ord_ref;

    -- set mod_user on all
    UPDATE GEO_ORDRE SET MOD_USER = arg_username, MOd_DATE = SYSDATE WHERE ORD_REF = ls_ord_ref_regul;
    UPDATE GEO_ORDLOG SET MOD_USER = arg_username, MOD_DATE = SYSDATE WHERE ORD_REF = ls_ord_ref_regul;
    UPDATE GEO_ORDLIG SET MOD_USER = arg_username, MOD_DATE = SYSDATE WHERE ORD_REF = ls_ord_ref_regul;

    commit;

    res := 1;

end F_CREE_ORDRE_REGULARISATION;
/
