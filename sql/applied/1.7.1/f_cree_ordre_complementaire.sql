CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREE_ORDRE_COMPLEMENTAIRE(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_username IN GEO_USER.NOM_UTILISATEUR%TYPE,
    res OUT number,
    msg OUT varchar2,
    ls_ord_ref_compl OUT varchar2
)
AS
    ls_cli_ref GEO_ORDRE.CLI_REF%TYPE;
    ls_cli_code GEO_ORDRE.CLI_CODE%TYPE;
    ls_cen_code GEO_ORDRE.CEN_CODE%TYPE;
    ls_transp GEO_ORDRE.TRP_CODE%TYPE;
    ls_nordre_ori GEO_ORDRE.NORDRE%TYPE;
    ls_dat_dep varchar(20);
    ls_ordre_per_code_ass GEO_ORDRE.PER_CODEASS%TYPE;
    ls_ordre_per_code_com GEO_ORDRE.PER_CODECOM%TYPE;
    ls_cli_code_ori GEO_ORDRE.CLI_CODE%TYPE;
    ls_cen_code_ori GEO_ORDRE.CEN_CODE%TYPE;
    ls_ref_cli GEO_ORDRE.REF_CLI%TYPE;
    ls_list_nordre_comp GEO_ORDRE.LIST_NORDRE_COMP%TYPE;
    ll_num_edi GEO_ORDRE.REF_EDI_ORDRE%TYPE;
    ls_date_liv varchar(20);
    ls_cen_ref GEO_ORDRE.CEN_REF%TYPE;
    ls_comm_interne varchar(60);
    ls_nordre_comp GEO_ORDRE.NORDRE%TYPE;
BEGIN
    -- correspond à f_cree_ordre_complementaire.pbl
    msg := '';
    res := 0;

    -- INFORMATION CLIENT
    select GEO_ORDRE.CLI_REF,
           GEO_ORDRE.CLI_CODE,
           GEO_ORDRE.CEN_CODE,
           GEO_ORDRE.TRP_CODE,
           GEO_ORDRE.NORDRE,
           to_char(GEO_ORDRE.DEPDATP,'dd/mm/yy'),
           GEO_ORDRE.PER_CODEASS,
           GEO_ORDRE.PER_CODECOM,
           GEO_ORDRE.CLI_CODE,
           GEO_ORDRE.CEN_CODE,
           GEO_ORDRE.REF_CLI,
           GEO_ORDRE.LIST_NORDRE_COMP,
           GEO_ORDRE.REF_EDI_ORDRE,
           to_char(GEO_ORDRE.LIVDATP,'dd/mm/yy'),
           GEO_ORDRE.CEN_REF
    into ls_cli_ref,
        ls_cli_code,
        ls_cen_code,
        ls_transp,
        ls_nordre_ori,
        ls_dat_dep,
        ls_ordre_per_code_ass,
        ls_ordre_per_code_com,
        ls_cli_code_ori,
        ls_cen_code_ori,
        ls_ref_cli,
        ls_list_nordre_comp,
        ll_num_edi,
        ls_date_liv,
        ls_cen_ref
    from    GEO_ORDRE
    where 	GEO_ORDRE.ORD_REF = arg_ord_ref;

    if ls_transp = '' or ls_transp is null then
        ls_transp :='-';
    end if;

    if ll_num_edi is null /*OR ll_num_edi = ''*/ then -- LLEF CDE EDI
        f_create_ordre_v2(arg_soc_code, ls_cli_code, ls_cen_code, ls_transp, ls_ref_cli , false, false, ls_dat_dep,'COM', res, msg, ls_ord_ref_compl);
    else
        f_create_ordre_v3(arg_soc_code, ls_cli_ref,ls_cen_ref, ls_transp, ls_ref_cli , false, false,  ls_dat_dep,'COM', ls_date_liv, ll_num_edi, res, msg, ls_ord_ref_compl); -- LLEF
    end if;

    -- Prise en compte d'un potentiel retour de code erreur
    if substr(msg, 1, 3) = '%%%' or res <> 1 then
        res := 0;
        msg := 'Anomalie lors de la création de l''ordre EDI: ' || ls_ord_ref_compl;
    else
        ls_comm_interne := 'COMPLEMENT O/ ' || ls_nordre_ori;

        Update GEO_ORDRE
        SET 	PER_CODEASS = ls_ordre_per_code_ass,
               PER_CODECOM = ls_ordre_per_code_com,
               COMM_INTERNE = substr(ls_comm_interne, 1, 128)
        where ORD_REF = ls_ord_ref_compl;
        f_insert_mru_ordre(ls_ord_ref_compl, arg_username, res, msg);

        select NORDRE
        into ls_nordre_comp
        from GEO_ORDRE
        where ORD_REF = ls_ord_ref_compl;

        If ls_list_nordre_comp is null then
            ls_list_nordre_comp := '';
        end if;
        ls_list_nordre_comp := ls_list_nordre_comp || ls_nordre_comp || ';';

        UPDATE GEO_ORDRE
        SET LIST_NORDRE_COMP = ls_list_nordre_comp
        where ORD_REF = arg_ord_ref;

        res := 1;
    end if;

end F_CREE_ORDRE_COMPLEMENTAIRE;
/

