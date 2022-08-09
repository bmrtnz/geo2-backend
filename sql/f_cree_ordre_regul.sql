CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREE_ORDRE_REGUL(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_lca_code IN GEO_LITCAU.LCA_CODE%TYPE,
    arg_typ_reg IN GEO_ORDRE.TYP_ORDRE%TYPE,
    arg_username IN GEO_USER.NOM_UTILISATEUR%TYPE,
    res OUT number,
    msg OUT varchar2,
    ls_ord_ref_regul OUT GEO_ORDRE.ORD_REF%TYPE
)
AS
    ls_nordre_ori GEO_ORDRE.NORDRE%TYPE;
    ls_dat_dep varchar2(20);
    ls_ordre_percode_ass GEO_ORDRE.PER_CODEASS%TYPE;
    ls_ordre_percode_com GEO_ORDRE.PER_CODECOM%TYPE;
    ls_list_nordre_regul GEO_ORDRE.LIST_NORDRE_REGUL%TYPE;
    ls_cli_ref GEO_ORDRE.CLI_REF%TYPE;
    ls_cli_code GEO_ORDRE.CLI_CODE%TYPE;
    ls_cen_code GEO_ORDRE.CEN_CODE%TYPE;
    ls_cen_ref GEO_ORDRE.CEN_REF%TYPE;
    ls_transp GEO_ORDRE.TRP_CODE%TYPE;
    ldate_liv GEO_ORDRE.LIVDATP%TYPE;
    ls_ref_cli GEO_ORDRE.REF_CLI%TYPE;

    ls_lca_desc GEO_LITCAU.LCA_DESC%TYPE;

    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    -- ls_ord_ref_regul GEO_ORDRE.ORD_REF%TYPE;
    ls_comm_intern GEO_ORDRE.COMM_INTERNE%TYPE;
BEGIN
    -- correspond à f_cree_ordre_regul.pbl
    res := 0;
    msg := '';

    select NORDRE,
           to_char(DEPDATP,'dd/mm/yy'),
           PER_CODECOM,
           PER_CODEASS,
           LIST_NORDRE_REGUL,
           CLI_REF,
           CLI_CODE,
           CEN_CODE,
           CEN_REF,
           TRP_CODE,
           LIVDATP,
           REF_CLI
    into ls_nordre_ori,
         ls_dat_dep,
         ls_ordre_percode_ass,
         ls_ordre_percode_com,
         ls_list_nordre_regul,
         ls_cli_ref,
         ls_cli_code,
         ls_cen_code,
         ls_cen_ref,
         ls_transp,
         ldate_liv,
         ls_ref_cli
    from GEO_ORDRE
    where ORD_REF = arg_ord_ref;

    select LCA_DESC
    into ls_lca_desc
    from GEO_LITCAU
    where LCA_CODE = arg_lca_code;

    -- INFORMATION CLIENT
    select GEO_CLIENT.CLI_REF,
           GEO_CLIENT.CLI_CODE,
           GEO_ENTREP.CEN_CODE,
           GEO_ENTREP.TRP_CODE
    into ls_cli_ref,
         ls_cli_code,
         ls_cen_code,
         ls_transp
    from 	GEO_CLIENT ,
            GEO_ENTREP
    where 	GEO_ENTREP.CEN_REF = ls_cen_ref AND
            GEO_CLIENT.CLI_REF = GEO_ENTREP.CLI_REF;

    ls_transp := '-';
    f_create_ordre_v2(arg_soc_code, ls_cli_code, ls_cen_code, ls_transp,'' , false, false, ls_dat_dep,arg_typ_reg, res, msg, ls_ord_ref_regul);


    ls_comm_intern := 'REGUL O/' || ls_nordre_ori || ' ' || ls_lca_desc;

    select NORDRE into ls_nordre
    from GEO_ORDRE
    where ORD_REF = ls_ord_ref_regul;

    Update GEO_ORDRE
    SET    PER_CODEASS = ls_ordre_percode_com,
           PER_CODECOM = ls_ordre_percode_ass,
           COMM_INTERNE = ls_comm_intern,
           REF_CLI = ls_ref_cli,
           LIVDATP = ldate_liv,
           INC_CODE = 'EXW',
           TRP_CODE = 'CLIENT'
    where ORD_REF = ls_ord_ref_regul;

    f_insert_mru_ordre(ls_ord_ref_regul,arg_username, res, msg);


    ls_list_nordre_regul := ls_list_nordre_regul || ls_nordre || ';';

    UPDATE GEO_ORDRE
    SET LIST_NORDRE_REGUL = ls_list_nordre_regul
    where  ORD_REF = arg_ord_ref;

end F_CREE_ORDRE_REGUL;
/

