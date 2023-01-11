/*  AUTEUR : B.AMADEI																			DATE : 10/04/19	 	*/
/*  BUT 	: Créer un ordre de replacement 																				*/
/*  PARAMETRE																												*/
/*  	   	ordre d'origine du litige																							*/
/* RETOUR								ff																						*/
/*																																	*/
/***************************************************************************/

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREE_ORDRE_REPLACEMENT" (
    arg_ord_ref_origine in GEO_ORDLIG.ORD_REF%TYPE,
    arg_cen_ref	varchar2,
    gs_username	varchar2,
    gs_soc_code varchar2,
    res in out number,
    msg in out varchar2,
    ls_ord_ref_replace out varchar2
)
AS
    ls_old_soc_code	 varchar2(50);
    ls_nordre_ori varchar2(50);
    ls_nordre varchar2(50);
    ls_cli_ref varchar2(50);
    ls_cli_code varchar2(50);
    ls_cen_code varchar2(50);
    ls_dat_dep varchar2(50);
    ls_transp varchar2(50);
    ls_ref_cli varchar2(50);
    ls_entrep_per_code_ass varchar2(50);
    ls_entrep_per_code_com varchar2(50);
    ls_client_per_code_ass varchar2(50);
    ls_client_per_code_com varchar2(50);
BEGIN
    res := 0;

    if length(arg_cen_ref)  = 6 then

        /* INFORMATION CLIENT */
        select GEO_CLIENT.CLI_REF,
            GEO_CLIENT.CLI_CODE,
            GEO_ENTREP.CEN_CODE,
            GEO_ENTREP.TRP_CODE,
            GEO_ENTREP.PER_CODE_ASS,
            GEO_ENTREP.PER_CODE_COM,
            GEO_CLIENT.PER_CODE_ASS,
            GEO_CLIENT.PER_CODE_COM
            into 	ls_cli_ref,
                    ls_cli_code,
                    ls_cen_code,
                    ls_transp,
                    ls_entrep_per_code_ass,
                    ls_entrep_per_code_com,
                    ls_client_per_code_ass,
                    ls_client_per_code_com
            from 	GEO_CLIENT ,
                    GEO_ENTREP
            where 	GEO_ENTREP.CEN_REF =arg_cen_ref 					AND
                        GEO_CLIENT.CLI_REF = GEO_ENTREP.CLI_REF	 ;

            ls_ref_cli :='EX O/ ' || ls_nordre_ori;
            if ls_transp = '' then ls_transp :='-'; end if;
                f_create_ordre_v2(gs_soc_code, ls_cli_code, ls_cen_code, ls_transp,'' , false, false, ls_dat_dep,'REP', res,msg,ls_ord_ref_replace);
                if res = 0 then return; end if;
    --			of_add_ligne_ordre_replace(ls_ord_ref_replace)

                select NORDRE into ls_nordre
                from GEO_ORDRE
                where ORD_REF= ls_ord_ref_replace;

                If ls_entrep_per_code_ass is null Then
                    ls_entrep_per_code_ass := ls_client_per_code_ass;
                End IF;

                If ls_entrep_per_code_com is null Then
                    ls_entrep_per_code_com := ls_client_per_code_com;
                End If;

                Update GEO_ORDRE
                SET 	PER_CODEASS = ls_entrep_per_code_com,
                        PER_CODECOM = ls_entrep_per_code_ass,
                        COMM_INTERNE = ls_ref_cli,
                        ORD_REF_PERE = arg_ord_ref_origine
                where ORD_REF= ls_ord_ref_replace;

                f_insert_mru_ordre(ls_ord_ref_replace,gs_username, res, msg);
                if res = 0 then return; end if;

            End If;

            -- If ls_old_soc_code <> gs_soc_code then
            --         geo_soc	= f_load_geo_societe(ls_old_soc_code)
            --         gs_soc_code	= geo_soc.soc_code
            -- end If

    --		this.object.ord_ref_replace[row] = 	ls_ord_ref_replace
    --		this.object.nordre_replace[row] = 	ls_nordre

    res := 1;
END;
/


