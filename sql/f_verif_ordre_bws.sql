CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_VERIF_ORDRE_BWS (
    arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_CLIENT.CLI_REF%TYPE,
    arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
	arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
	arg_bws_ecris  IN P_STR_TAB_TYPE,  
    res IN OUT number,
    msg IN OUT varchar2
)
AS

	ls_cli_code GEO_CLIENT.CLI_CODE%TYPE;
	ls_cen_code GEO_ENTREP.CEN_CODE%TYPE;
	ls_esp_code GEO_ARTICLE_COLIS.ESP_CODE%TYPE;
	ls_var_code GEO_ARTICLE_COLIS.VAR_CODE%TYPE;
	ls_ord_ref GEO_ORDLIG.ORD_REF%TYPE;
	ls_orl_ref GEO_ORDLIG.ORL_REF%TYPE;
    ll_cde_nb_col number;
	
	cursor C_LIG_ORD_BWS(cam_code GEO_ORDRE.CAM_CODE%TYPE, art_ref GEO_ARTICLE_COLIS.ART_REF%TYPE)
	IS
		select L.cde_nb_col, L.ord_ref, L.orl_ref
		from geo_ordre O, geo_ordlig L
		where O.soc_code ='BWS'
		and O.sco_code = 'F'
		and O.cam_code = cam_code
		and O.ord_ref = L.ord_ref
		and O.TYP_ORDRE = 'ORD'
		and O.FLANNUL = 'N'
		and L.art_ref = art_ref
		and O.livdatp = to_date(sysdate, 'dd/mm/YY')
		and cen_code in ('ENTREPOT PCHANTEGR','ENTREPOT MOISSACDV','ENTREPOT TERRYLOIR','MOISSACDV NON CHEP');
		
		
BEGIN
    -- correspond à of_verif_ordre_bws.pbl
    res := 0;
    msg := '';

    open C_LIG_ORD_BWS (arg_cam_code, arg_art_ref);
        LOOP
            fetch C_LIG_ORD_BWS into ll_cde_nb_col, ls_ord_ref, ls_orl_ref;
            EXIT WHEN C_LIG_ORD_BWS%notfound;
			if ll_cde_nb_col > 0 then
				F_SAUVE_ORD_BWS(ls_ord_ref, ls_orl_ref, arg_ref_edi_ordre, arg_ref_edi_ligne, arg_art_ref, arg_cam_code, arg_bws_ecris, res, msg);
				if substr(msg, 1, 3) = '%%%' then
					res := 0;
					return;
				end if;
			end if;
		END LOOP;
    CLOSE C_LIG_ORD_BWS;

    res := 1;
END F_VERIF_ORDRE_BWS;
/
