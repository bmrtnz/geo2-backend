CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREATE_EDI_ESP (
    arg_edi_ordre IN GEO_EDI_ORDRE.ref_edi_ordre%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_cli_ref IN GEO_CLIENT.CLI_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    arg_username in GEO_USER.NOM_UTILISATEUR%TYPE,
    res out number,
    msg out varchar2,
    ls_nordre_tot out varchar2,
    tab_ordre_cree out P_STR_TAB_TYPE
)
AS
    ls_ord_ref GEO_ORDRE.ORD_REF%TYPE;
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ordre_cree_idx number := 0;
	i number := 0;

	CURSOR C_EDI_LIGNE (edi_ordre GEO_EDI_LIGNE.REF_EDI_ORDRE%TYPE) IS
        select EAN_PROD_CLIENT, QUANTITE_COLIS, REF_EDI_LIGNE, CODE_INTERNE_PROD_CLIENT
        from GEO_EDI_LIGNE
        where ref_edi_ordre = edi_ordre
		order by num_ligne;
		
    CURSOR C_EDI_ART_CLI (client GEO_EDI_ART_CLI.cli_ref%TYPE, gtin_art GEO_EDI_ART_CLI.GTIN_COLIS_CLIENT%TYPE, code_art_client GEO_EDI_ART_CLI.ART_REF_CLIENT%TYPE) IS
        select art_ref, priorite, fou_code, vte_pu, ach_pu
        from GEO_EDI_ART_CLI
        where cli_ref = client
        and ( gtin_colis_client = gtin_art or art_ref_client = code_art_client)
		and valide = 'O'
		order by priorite;
BEGIN
    res := 0;
    msg := '';
    tab_ordre_cree := p_str_tab_type();

	F_CREATE_ORDRE_EDI_ESP(arg_soc_code, arg_cli_ref, arg_cen_ref, 'CLIENT', false, false, 'ORD', arg_edi_ordre, res, msg, ls_ord_ref);
	if (res <> 1) then
		msg := 'Erreur lors de la création de l''ordre : ' || msg;
		return;
	end if;

	F_INSERT_MRU_ORDRE(ls_ord_ref, arg_username, res, msg);
	if (res <> 1) then
		msg := 'Erreur lors de la création du mru_ordre : ' || msg;
		return;
	end if;

	for l in C_EDI_LIGNE(arg_edi_ordre)
	loop
		for s in C_EDI_ART_CLI(arg_cli_ref, l.ean_prod_client, l.code_interne_prod_client)
		loop	
				i := i +1;
				F_CREATE_LIGNE_EDI_ESP(l.ref_edi_ligne, ls_ord_ref, arg_cli_ref, arg_cen_ref, s.art_ref, s.priorite, s.fou_code, s.vte_pu, s.ach_pu, l.quantite_colis, l.ean_prod_client, arg_soc_code, arg_username, i, res, msg);
				if (res <> 1) then
					msg := 'Erreur lors de la création de la ligne d''ordre : ' || msg;
					return;
				end if;
		end loop;
	end loop;
	
	tab_ordre_cree.extend();
	ordre_cree_idx := ordre_cree_idx + 1;
	tab_ordre_cree(ordre_cree_idx) := ls_ord_ref;

	select nordre into ls_nordre from geo_ordre where ord_ref = ls_ord_ref;
	ls_nordre_tot := ls_nordre_tot || ls_nordre || ',';

    if ls_nordre_tot is not null then
        update GEO_EDI_ORDRE SET STATUS_GEO = 'T' WHERE REF_EDI_ORDRE = arg_edi_ordre;
		update GEO_ORDRE set list_nordre = ls_nordre_tot, list_ord_ref = ls_ord_ref where ord_ref = ls_ord_ref;
		commit;
		F_VERIF_LOGISTIQUE_ORDRE(ls_ord_ref, res, msg);
		if (res <> 1) then
			msg := 'Erreur lors de la création de la logistique: ' || msg;
			return;
		end if;
		
		F_CALCUL_MARGE(ls_ord_ref, res, msg);
		if (res <> 1) then
			msg := 'Erreur lors du calcul de la marge : ' || msg;
			return;
		end if;
    end if;

    res := 1;

end F_CREATE_EDI_ESP;
/
