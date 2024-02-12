CREATE OR REPLACE PROCEDURE GEO_ADMIN."F_CREE_ORD_REFACT_TRSP" (
    arg_ord_ref_origine in GEO_ORDRE.ord_ref%type,
    arg_mont_indemn in number,
    gs_soc_code in varchar2,
    gs_username in varchar2,
	res out number,
    msg out varchar2,
    ls_ord_ref_refacturer out GEO_ORDRE.ord_ref%type
)
AS
	ls_old_soc_code varchar(50);
    ls_cen_ref varchar(50);
    ls_nordre_ori varchar(50);
    ls_cli_ref varchar(50);
    ls_cli_code varchar(50);
    ls_cen_code varchar(50);
    ls_dat_dep varchar(50);
    ls_transp varchar(50);
    ls_ref_cli varchar(50);
    ls_entrep_per_code_ass varchar(50);
    ls_entrep_per_code_com varchar(50);
    ls_client_per_code_ass varchar(50);
    ls_client_per_code_com varchar(50);
    ls_ordre_per_code_ass varchar(50);
    ls_ordre_per_code_com varchar(50);
    ls_cli_code_ori varchar(50);
    ls_cen_code_ori varchar(50);
begin

	res := 0;
	msg := '';

    /* INFORMATION CLIENT */
    BEGIN
        select GEO_CLIENT.CLI_REF,
        GEO_CLIENT.CLI_CODE,
        GEO_ENTREP.CEN_CODE,
        GEO_ENTREP.TRP_CODE,
        GEO_ORDRE.NORDRE,
        to_char(GEO_ORDRE.DEPDATP,'dd/mm/yy'),
        GEO_ENTREP.PER_CODE_ASS,
        GEO_ENTREP.PER_CODE_COM,
        GEO_CLIENT.PER_CODE_ASS,
        GEO_CLIENT.PER_CODE_COM,
        GEO_ORDRE.CLI_CODE,
        GEO_ORDRE.CEN_CODE,
        GEO_ORDRE.PER_CODEASS,
        GEO_ORDRE.PER_CODECOM
        into 	ls_cli_ref,
                ls_cli_code,
                ls_cen_code,
                ls_transp,
                ls_nordre_ori,
                ls_dat_dep,
                ls_entrep_per_code_ass,
                ls_entrep_per_code_com,
                ls_client_per_code_ass,
                ls_client_per_code_com,
                ls_cli_code_ori,
                ls_cen_code_ori,
                ls_ordre_per_code_ass,
                ls_ordre_per_code_com
        from 	GEO_ORDRE,
                GEO_TRANSP,
                GEO_CLIENT ,
                GEO_ENTREP
        where 	GEO_ORDRE.ORD_REF = arg_ord_ref_origine AND
                    GEO_ORDRE.TRP_CODE = GEO_TRANSP.TRP_CODE and
                    GEO_TRANSP.CLI_REF_ASSOC= GEO_CLIENT.CLI_REF  and
                    GEO_CLIENT.CLI_REF = GEO_ENTREP.CLI_REF	  and
                    GEO_ENTREP.VALIDE = 'O';
    exception
        when too_many_rows then
            msg := 'Sélection de client ambiguë';
            res := 0;
            return;
    END;

    ls_ref_cli := 'REFACTURATION / ' || ls_nordre_ori || ' ' || ls_cli_code_ori || ' ' || ls_cen_code_ori;
    if ls_transp = '' then ls_transp := '-'; end if;

    f_create_ordre_v2('SA', ls_cli_code, ls_cen_code, ls_transp, ls_ref_cli , false, false, ls_dat_dep,'REF', res, msg, ls_ord_ref_refacturer);
    if res = 0 then
        msg := 'Erreur: Impossible de créer ordre :' || ls_ord_ref_refacturer;
        rollback;
        return;
    End If;

	declare
	  ref_ret varchar2(50);
	begin
    	 f_cree_ord_refact_trsp_lig(arg_ord_ref_origine, ls_ord_ref_refacturer, arg_mont_indemn, gs_soc_code, gs_username, res, msg, ref_ret);
    	if res = 0 then 
			delete GEO_ORDRE where ORD_REF = ls_ord_ref_refacturer;
			return; 
		End If;
	end;

    If ls_entrep_per_code_ass is null Then
        ls_entrep_per_code_ass := ls_client_per_code_ass;
    End IF;

    If ls_entrep_per_code_com is null Then
        ls_entrep_per_code_com := ls_entrep_per_code_com;
    End If;

    Update GEO_ORDRE
    SET 	PER_CODEASS = ls_ordre_per_code_ass,
            PER_CODECOM = ls_ordre_per_code_com,
            COMM_INTERNE = ls_ref_cli
        where ORD_REF= ls_ord_ref_refacturer;

    f_insert_mru_ordre(ls_ord_ref_refacturer,gs_username, res, msg);
    if res = 0 then return; End If;

	res := 1;
	msg := 'OK';

end;
/
