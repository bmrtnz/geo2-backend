/***************************************************************************/
/*  AUTEUR : B.AMADEI																			DATE : 02/03/20	 	*/
/*  BUT 	: Créer un ordre de reedition facture	 																		*/
/*  PARAMETRE																												*/
/*  	   	ordre d'origine 																										*/
/* RETOUR																														*/
/*																																	*/
/***************************************************************************/

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREE_ORDRE_REED_FACT" (
    arg_ord_ref_origine in GEO_ORDLIG.ORD_REF%TYPE,
    gs_soc_code in varchar2,
    gs_username in varchar2,
    res in out number,
    msg in out varchar2,
    ls_ord_ref_reed_fact out varchar2
)
AS
    ls_old_soc_code	 varchar2(50);
    ls_cen_ref	 varchar2(50);
    ls_nordre_ori varchar2(50);
    ls_nordre varchar2(50);
    ls_cli_ref varchar2(50);
    ls_cli_code varchar2(50);
    ls_cen_code varchar2(50);
    ls_dat_dep varchar2(50);
    ls_transp varchar2(50);
    ls_ref_cli varchar2(50);
    ls_ordre_percode_ass varchar2(50);
    ls_ordre_percode_com varchar2(50);
    ls_comm_intern varchar2(50);
    ls_TOTVTE varchar2(50);
    ls_TOTCOL varchar2(50);
    ls_TOTPDSNET varchar2(50);
    ls_TOTPDSBRUT varchar2(50);
    ldate_liv timestamp;
BEGIN
    res := 0;

        select NORDRE,
            to_char(DEPDATP,'dd/mm/yy'),
            PER_CODECOM,
            PER_CODEASS,
            CLI_REF,
            CLI_CODE,
            CEN_CODE,
            TRP_CODE,
            LIVDATP,
            REF_CLI,
            TOTVTE,
            TOTCOL,
            TOTPDSNET,
            TOTPDSBRUT
            into 	ls_nordre_ori,
                    ls_dat_dep,
                    ls_ordre_percode_ass,
                    ls_ordre_percode_com,
                    ls_cli_ref,
                    ls_cli_code,
                    ls_cen_code,
                    ls_transp,
                    ldate_liv,
                    ls_ref_cli,
                    ls_TOTVTE,
                    ls_TOTCOL,
                    ls_TOTPDSNET,
                    ls_TOTPDSBRUT
    from GEO_ORDRE
    where ORD_REF = arg_ord_ref_origine;


            f_create_ordre_v2(gs_soc_code, ls_cli_code, ls_cen_code, ls_transp,'' , false, false, ls_dat_dep,'RDF',res,msg,ls_ord_ref_reed_fact);
            if res = 0 then return; end if;
    --		of_add_ligne_ordre_replace(ls_ord_ref_replace)


            ls_comm_intern := 'REEDITION DE FACTURE O/'||ls_nordre_ori;

                select NORDRE into ls_nordre
                from GEO_ORDRE
                where ORD_REF= ls_ord_ref_reed_fact;

                Update GEO_ORDRE
                SET 	PER_CODEASS =ls_ordre_percode_com,
                        PER_CODECOM =ls_ordre_percode_ass,
                        COMM_INTERNE =ls_comm_intern,
                        REF_CLI = ls_ref_cli,
                        LIVDATP = ldate_liv,
                        TOTVTE = ls_TOTVTE,
                        TOTCOL = ls_TOTCOL,
                        TOTPDSNET = ls_TOTPDSNET,
                        TOTPDSBRUT = ls_TOTPDSBRUT
                where ORD_REF= ls_ord_ref_reed_fact;

                f_insert_mru_ordre(ls_ord_ref_reed_fact,gs_username,res,msg);
            if res = 0 then return; end if;


    res := 1;
END;
/


