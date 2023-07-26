CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."W_AJOUT_ART_RECAP_EDI_COLIBRI" (
    arg_art_ref IN AVI_ART_GESTION.ART_REF%TYPE,
    arg_fou_code IN GEO_FOURNI.FOU_CODE%TYPE,
    arg_prop_code IN GEO_FOURNI.FOU_CODE%TYPE,
    arg_qte_valide IN number,
    arg_k_stock_art_edi_bassin IN number,
	res out number,
    msg out clob
) AS
    ls_gtin varchar2(50);
    ls_cli_ref varchar2(50);
    ls_valide varchar2(50);
    ls_art_ref_client varchar2(50);
    ls_valide_art varchar2(50);
    ls_erreur clob;
    ls_cam_code varchar2(50);
    ls_ach_bta_code varchar2(50);
    ls_ach_dev_code varchar2(50);
    ls_vte_bta_code varchar2(50);
    ls_pal_code varchar2(50);
    ls_bac_code varchar2(50);
    ls_bac_code_entrep varchar2(50);
    ls_bac_code_edi varchar2(50);
    ls_flag_hors_bassin varchar2(50);
    ls_cen_ref varchar2(50);
    ls_pal_code_entrep varchar2(50);
    ll_priorite number;
    ll_edi_ord number;
    ll_edi_lig number;
    ld_ach_dev_pu number;
    ld_ach_pu number;
    ld_ach_dev_taux number;
    ld_vte_pu number;
    ld_vte_pu_net number;
begin

    msg := '';
    res := 0;

    begin
        select bac_code into ls_bac_code
        from geo_fourni
        where fou_code = arg_fou_code;
    exception when others then
        ls_erreur := 'alert select GEO_FOURNI, Une erreur est survenue ' || SQLERRM;
    end;

    begin
        select valide into ls_valide_art from geo_article_colis where art_ref = arg_art_ref;
        if ls_valide_art <> 'O'  then
            ls_erreur := 'Le code article ' || arg_art_ref || ' est non valide';
        end if;
    exception when no_data_found then
        ls_erreur := 'Le code article ' || arg_art_ref || ' est inexistant';
    end;

    if ls_erreur is not null then
        msg := 'ERREUR, Veuillez faire les corrections adéquates: ' || ls_erreur;
        res := 0;
        return;
    end if;

    if  arg_k_stock_art_edi_bassin is not null then
        begin
            select edi_ord, edi_lig, cli_ref, cam_code, gtin, ach_bta_code, ach_dev_code, ach_dev_pu, ach_pu, ach_dev_taux,
                    vte_bta_code, vte_pu, vte_pu_net, pal_code
            into
                    ll_edi_ord, ll_edi_lig, ls_cli_ref, ls_cam_code, ls_gtin, ls_ach_bta_code, ls_ach_dev_code, ld_ach_dev_pu, ld_ach_pu,
                    ld_ach_dev_taux, ls_vte_bta_code, ld_vte_pu, ld_vte_pu_net, ls_pal_code
            from geo_stock_art_edi_bassin
            where k_stock_art_edi_bassin = arg_k_stock_art_edi_bassin;
        exception when others then
			msg := 'alert insert GEO_EDI_ART_CLI, Une erreur est survenue ' || SQLERRM;
            res := 0;
            return;
        end;

        select bac_code, cen_ref into ls_bac_code_edi, ls_cen_ref
        from geo_edi_ordre
        where ref_edi_ordre = ll_edi_ord;
        if ls_bac_code_edi is null then
            ls_bac_code_edi := '';
        end if;

        --ls_flag_hors_bassin = 'O'
        ls_flag_hors_bassin := 'HB';
        if (ls_bac_code = ls_bac_code_edi) or (ls_bac_code = 'UDC' and ls_bac_code_edi = 'SW') then
            --ls_flag_hors_bassin = 'N'
            ls_flag_hors_bassin := 'DB';
        else
            if  ls_bac_code_edi = ''  then
                declare
                    rres number;
                    rmsg clob;
                begin
                    f_recup_region_entrep(ls_cen_ref, rres, rmsg, ls_bac_code_entrep);
                    if rres = 0 then
                        res := 0;
                        msg := msg || ' ' || rmsg;
                        return;
                    end if;
                end;
                if (ls_bac_code = ls_bac_code_entrep) or (ls_bac_code = 'UDC' and ls_bac_code_entrep = 'SW') then
                    --ls_flag_hors_bassin = 'N'
                    ls_flag_hors_bassin := 'DB';
                end if;
            end if;
        end if;

        if ls_pal_code = '' or ls_pal_code is null then
            begin
                select pal_code into ls_pal_code_entrep
                from geo_entrep
                where cen_ref = ls_cen_ref
                and valide = 'O';

                if ls_pal_code_entrep is not null then
                    ls_pal_code := ls_pal_code_entrep;
                else
                    ls_pal_code := '-';
                end if;
            exception when no_data_found then
                ls_pal_code := '-';
            end;
        end if;

        begin
            insert into geo_stock_art_edi_bassin (edi_ord, edi_lig, cli_ref, cam_code, art_ref, gtin, fou_code, prop_code,
            bac_code, qte_res, age, ach_bta_code, ach_dev_code, ach_dev_pu, ach_pu, ach_dev_taux, vte_bta_code, vte_pu, vte_pu_net,
            flag_hors_bassin, choix, pal_code, qte_valide)
            VALUES (ll_edi_ord, ll_edi_lig, ls_cli_ref, ls_cam_code, arg_art_ref, ls_gtin, arg_fou_code, arg_prop_code,
                        ls_bac_code, 0, '', ls_ach_bta_code, ls_ach_dev_code, ld_ach_dev_pu, ld_ach_pu,
                        ld_ach_dev_taux, ls_vte_bta_code, ld_vte_pu, ld_vte_pu_net,ls_flag_hors_bassin, 'O', ls_pal_code, arg_qte_valide);
            commit;
        exception when others then
            msg := 'alert insert geo_stock_art_edi_bassin, Une erreur est survenue ' || SQLERRM;
            rollback;
        end;
	end if;

    res := 1;

end;
/

