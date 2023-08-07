CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_SAUVE_STOCK_PLANIF(
	arg_key_planif  IN P_STR_TAB_TYPE,
	arg_ref_edi_ordre IN GEO_EDI_ORDRE.REF_EDI_ORDRE%TYPE,
    arg_ref_edi_ligne IN GEO_CLIENT.CLI_REF%TYPE,
	arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS

	ls_ACH_BTA_CODE GEO_ORDLIG.ACH_BTA_CODE%TYPE;
	ls_ACH_DEV_CODE GEO_ORDLIG.ACH_DEV_CODE%TYPE;
	ls_VTE_BTA_CODE GEO_ORDLIG.VTE_BTA_CODE%TYPE := 'KILO';
	ls_histo_VTE_BTA_CODE GEO_ORDLIG.VTE_BTA_CODE%TYPE;
	ld_ACH_DEV_PU GEO_ORDLIG.ACH_DEV_PU%TYPE;
	ld_ACH_PU GEO_ORDLIG.ACH_PU%TYPE;
	ld_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
	ld_VTE_PU_NET GEO_ORDLIG.VTE_PU_NET%TYPE;
	ld_ach_dev_taux GEO_ORDLIG.ACH_DEV_TAUX%TYPE;
	ld_histo_VTE_PU GEO_ORDLIG.VTE_PU%TYPE;
	ld_histo_VTE_PU_NET GEO_ORDLIG.VTE_PU_NET%TYPE;
	ls_cli_ref GEO_CLIENT.CLI_REF%TYPE;
	ls_fou_code GEO_FOURNI.FOU_CODE%TYPE;
	ls_prop_code GEO_FOURNI.FOU_CODE%TYPE;
	ls_cen_ref GEO_ENTREP.CEN_REF%TYPE;
	ls_ean_prod_client GEO_EDI_LIGNE.EAN_PROD_CLIENT%TYPE;
	ls_age GEO_STOCK.AGE%TYPE;
	ll_qte_restant_stock number;
	ls_flag_hors_bassin GEO_STOCK_ART_EDI_BASSIN.FLAG_HORS_BASSIN%TYPE;
	ls_sauve_stock varchar2(2);
	ls_canal_cde GEO_EDI_ORDRE.CANAL_CDE%TYPE;
	ls_gem_code GEO_ARTICLE_COLIS.GEM_CODE%TYPE;
	ls_art_ref_client GEO_EDI_LIGNE.CODE_INTERNE_PROD_CLIENT%TYPE;
	ls_code_prod_client varchar2(20);
	ls_pal_code GEO_ENTREP.PAL_CODE%TYPE;
	ls_bac_code_entrep GEO_DEPT.BAC_CODE%TYPE;
	ls_dept_entrep varchar2(2);
	ll_dept_entrep number;
	ls_cen_ref_client GEO_EDI_ORDRE.CEN_REF%TYPE;
	ls_pal_code_entrep GEO_ENTREP.PAL_CODE%TYPE;
	ls_enr_bws_ecris varchar2(150);
	ls_art_ref GEO_ARTICLE_COLIS.ART_REF%TYPE;
	ll_key number;
	ls_bac_code_station GEO_FOURNI.BAC_CODE%TYPE;
	ll_k_stock_art_edi_bassin GEO_STOCK_ART_EDI_BASSIN.K_STOCK_ART_EDI_BASSIN%TYPE;
	ll_qte_res number;

BEGIN
    res := 0;
    msg := '';

   
    begin
        select cli_ref, cen_ref, ean_prod_client, prix_vente, unite_qtt, canal_cde, code_interne_prod_client
		into ls_cli_ref, ls_cen_ref, ls_ean_prod_client, ld_VTE_PU, ls_VTE_BTA_CODE, ls_canal_cde, ls_art_ref_client
		from geo_edi_ordre O, geo_edi_ligne L
		where O.ref_edi_ordre = arg_ref_edi_ordre
		and L.ref_edi_ordre = O.ref_edi_ordre
		and L.ref_edi_ligne = arg_ref_edi_ligne;
    exception when others then
        msg := '%%%ERREUR f_sauve_stock_planif edi_ordre: ' || to_char(arg_ref_edi_ordre) || ' edi_ligne: ' || to_char(arg_ref_edi_ligne) || SQLERRM;
        res := 0;
        return;
    end;

	ld_VTE_PU_NET := ld_VTE_PU;
	ls_age := '';
	ll_qte_res := 0;
	
	if ls_ean_prod_client is null or ls_ean_prod_client = '' then
		ls_code_prod_client := ls_art_ref_client;
	else
		ls_code_prod_client := ls_ean_prod_client;
	end if;
	
	for i in 1 .. arg_key_planif.count
    loop
		ll_key := to_number(arg_key_planif(i));
		begin
			select stat_code_propr, stat_code_embal, art_ref, F.bac_code, P.pal_code
			into ls_prop_code, ls_fou_code, ls_art_ref, ls_bac_code_station, ls_pal_code
			from  geo_edi_art_planif P, geo_fourni F
			where k_edi_art_planif = ll_key
			and F.fou_code = P.stat_code_embal;
		exception when no_data_found then
			msg := msg || ' Impossible de recuperer les données planif pour key: ' || ll_key || SQLERRM;
			res := 0;
			return;
		end;
	end loop;
	
	
	begin
		select * 
			into ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_histo_VTE_BTA_CODE, ld_histo_VTE_PU, ld_ach_dev_taux
		from (
			select ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, ACH_DEV_TAUX
			from geo_ordre O, geo_ordlig L
			where O.cli_ref = ls_cli_ref
			and O.cen_ref = ls_cen_ref
			and O.ord_ref = L.ord_ref
			and L.art_ref = ls_art_ref
			and L.fou_code = ls_fou_code
			and L.propr_code = ls_prop_code 
			and FACTURE_AVOIR ='F'
			and FLANNUL = 'N'
			and typ_ordre ='ORD'
			order by O.CREDAT desc 
		)
		where rownum = 1;
	  exception when no_data_found then
        --msg := msg || ' Impossible de recuperer les données d''achat de la précédente commande. art_ref:' || ls_art_ref || ', entrep:' || ls_cen_ref || SQLERRM;
        --res := 0;
        --return;
		ls_ACH_BTA_CODE := 'KILO';
		ls_ACH_DEV_CODE := 'EUR';
		ld_ACH_DEV_PU	 := 0;
		ld_ACH_PU	:= 0;
		ld_ach_dev_taux	 := 1;
    end;

	--Verification si une ligne à déjà été inséré suite pb de double écriture de la ligne article
	begin
		select k_stock_art_edi_bassin 
		into ll_k_stock_art_edi_bassin 
		from GEO_STOCK_ART_EDI_BASSIN
		where edi_ord = arg_ref_edi_ordre
		and EDI_LIG = arg_ref_edi_ligne
		and CLI_REF = ls_cli_ref
		and CAM_CODE = arg_cam_code
		and ART_REF = ls_art_ref;
	end;
	if ll_k_stock_art_edi_bassin is not null and length(ll_k_stock_art_edi_bassin) > 0 then
		begin
			DELETE FROM GEO_STOCK_ART_EDI_BASSIN where k_stock_art_edi_bassin = ll_k_stock_art_edi_bassin;
		exception when others  then
			msg := msg || ' Impossible de supprimer ll_k_stock_art_edi_bassin:' || ll_k_stock_art_edi_bassin || SQLERRM;
			res := 0;
			return;
		end;
	end if;
	
	if ls_canal_cde = 'EDI' then
		if ld_histo_VTE_PU <> ld_VTE_PU then
			update geo_edi_ligne
			set alert_prix = 'Attention Prix Précédent = ' || ld_histo_VTE_PU
			where ref_edi_ordre = arg_ref_edi_ordre
			and ref_edi_ligne = arg_ref_edi_ligne;
			commit;
		end if;
		begin
			select AC.GEM_CODE
			into ls_gem_code
			FROM GEO_ARTICLE_COLIS AC
			where AC.ART_REF = ls_art_ref 
			and AC.valide = 'O';
		exception when others then
			msg := '%%%Erreur sur récup GEM_CODE pour l''article : ' || ls_art_ref;
		    res := 0;
			return;
		end;
		CASE ls_gem_code
		WHEN 'PLX' THEN ls_vte_bta_code 		:= 'KILO';
		WHEN 'PLX2' THEN ls_vte_bta_code 		:= 'KILO';
		WHEN 'CAISSE' THEN ls_vte_bta_code 		:= 'KILO';
		WHEN 'UCPLT' THEN  ls_vte_bta_code 		:= 'COLIS';
		WHEN 'UCBARQ' THEN ls_vte_bta_code 		:= 'BARQUE';
		WHEN 'UCSAC' THEN ls_vte_bta_code 		:= 'SACHET';
		ELSE ls_vte_bta_code := 'KILO';
		END CASE;		
	end if;

    BEGIN
        insert into GEO_STOCK_ART_EDI_BASSIN (
			EDI_ORD, EDI_LIG, CLI_REF, CAM_CODE, ART_REF, GTIN, FOU_CODE, BAC_CODE, QTE_RES, AGE, PROP_CODE, ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, VTE_PU_NET, 
			ACH_DEV_TAUX, FLAG_HORS_BASSIN, PAL_CODE
		)
		values(
			arg_ref_edi_ordre, arg_ref_edi_ligne, ls_cli_ref, arg_cam_code, ls_art_ref, ls_code_prod_client, ls_fou_code, ls_bac_code_station, ll_qte_res, ls_age, ls_prop_code, ls_ACH_BTA_CODE, 
			ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_VTE_BTA_CODE, ld_VTE_PU, ld_VTE_PU_NET, ld_ach_dev_taux, 'PLA', ls_pal_code
		);
    exception when others then
         msg := '%%%ERREUR f_sauve_stock_planif insert GEO_STOCK_ART_EDI_BASSIN ref_edi_ordre: ' || to_char(arg_ref_edi_ordre) || ' ref_edi_ligne: ' || to_char(arg_ref_edi_ligne) || ' art_ref: ' || ls_art_ref || ' ' || SQLERRM;
		res := 0;
		rollback;
        return;
    END;

	res := 1;
    commit;

end F_SAUVE_STOCK_PLANIF;
/

