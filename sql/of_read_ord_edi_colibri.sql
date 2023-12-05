CREATE OR REPLACE PROCEDURE "OF_READ_ORD_EDI_COLIBRI" (
    arg_num_cde_edi IN number,
    arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
    arg_stock_type IN char,
    res out number,
    msg out clob
) AS
    ll_ref_edi_ligne number;
    ll_num_ligne number;
    ll_quantite_colis number;
    ld_prix_vente_edi number;

    ls_planif p_str_tab_type := p_str_tab_type();
    ls_null p_str_tab_type := p_str_tab_type();
    array_bws_ecris p_str_tab_type := p_str_tab_type();

    ls_ean_prod_client varchar2(50);
    ls_cli_ref varchar2(50);
    ls_cen_ref varchar2(50);
    ls_art_ref_client varchar2(50);
    ls_inc_code varchar2(50);
    ls_region_pref varchar2(50);
    ls_bac_code varchar2(50);
    ls_region_ent varchar2(50);
    ls_region varchar2(50);
    ls_art_ref varchar2(50);
    ls_plateforme_bws varchar2(50);
    ls_stock_plateforme varchar2(50);
    ls_stock_bassin varchar2(50);
    ls_stock_hors_bassin varchar2(50);
    ls_retour_sauve_planif varchar2(50);
    ls_ya_stock varchar2(50);
    ls_retour_sauve varchar2(50);
    ls_stock_station varchar2(50);
    ls_bw_stock varchar2(50);
begin

    msg := '';
    res := 0;

    declare
        cursor C_LIG_EDI_L IS
            select L.ref_edi_ligne, L.num_ligne, L.ean_prod_client, L.quantite_colis,  L.prix_vente, O.cli_ref, O.cen_ref, L.code_interne_prod_client, O.inc_code, O.bac_code
            from geo_edi_ligne L, geo_edi_ordre O
            where O.ref_edi_ordre = arg_num_cde_edi
            and L.ref_edi_ordre = O.ref_edi_ordre
            and L.status <> 'D'
            order by ref_edi_ligne, num_ligne;
    begin
        open C_LIG_EDI_L;
        fetch C_LIG_EDI_L into ll_ref_edi_ligne, ll_num_ligne, ls_ean_prod_client, ll_quantite_colis, ld_prix_vente_edi, ls_cli_ref, ls_cen_ref, ls_art_ref_client, ls_inc_code, ls_region_pref;
        loop
            ls_art_ref_client := coalesce(ls_art_ref_client,'');
            ls_ean_prod_client := coalesce(ls_ean_prod_client,'');

            declare
                CURSOR C_ARTICLE IS
                    select  E.art_ref
                    from GEO_EDI_ART_CLI E, GEO_ARTICLE_COLIS A
                    where cli_ref = ls_cli_ref
                    and E.valide = 'O'
                    and ( E.gtin_colis_client = ls_ean_prod_client or E.art_ref_client = ls_art_ref_client)
                    and E.art_ref = A.art_ref
                    and A.valide = 'O'
                    order by E.priorite;
            begin
                open C_ARTICLE;
                fetch C_ARTICLE into ls_art_ref;
                loop

                    -- Nous avons des articles avec ce GTIN client
                    ls_planif             := p_str_tab_type();
                    array_bws_ecris     :=  p_str_tab_type();
                    f_recup_region_entrep(ls_cen_ref,res,msg,ls_region_ent);
                    if res = 0 then return; end if;
                    ls_ya_stock := 'N'; -- initialisation indicateur que l'on a trouvé du stock

                    if ls_region_pref is null then -- Région préférentielle dans la commande
                        ls_region := ls_region_ent;
                    else
                        f_verif_region_pref(ls_region_pref,res,msg,ls_region); -- return la région pref. désirée par le client
                        if res = 0 then return; end if;
                    end if;

                    f_verif_bws(arg_num_cde_edi, ll_ref_edi_ligne, ls_art_ref,res,msg,ls_plateforme_bws); -- Return  la plateforme si insert effectué
                    if res = 0 then
                        close C_ARTICLE;
                        close C_LIG_EDI_L;
                        return;
                    end if;

                    if ls_plateforme_bws is not null then
                        f_controle_stock(ls_plateforme_bws, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'BWS', array_bws_ecris, arg_stock_type, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert
                        if res = 0 then
                            close C_ARTICLE;
                            close C_LIG_EDI_L;
                            return;
                        end if;
                        if res = 1 then
                            ls_ya_stock := 'O';
                        end if;
                    end if;

                    if ls_ya_stock = 'O' and arg_stock_type = 'S' then
                        goto continue_label;
                    end if;

                    -- Vérification s'il existe une commande dans société BWS pour les entrepôts MOISSACPRES, TERRYPRES, et CHANTEPRES
                    f_verif_ordre_bws(arg_num_cde_edi, ll_ref_edi_ligne, ls_art_ref, arg_cam_code, array_bws_ecris, res, msg);
                    if res = 1 then
                        ls_ya_stock := 'O';
                    end if;

                    if ls_ya_stock = 'O' and arg_stock_type = 'S' then
                        goto continue_label;
                    end if;

                    -- Vérification s'il y a un SUIVI dans la table GEO_EDI_ART_PLANIF
                    f_verif_planif(ls_region, ls_art_ref, ls_cen_ref, res, msg, ls_planif); -- Recherche dans la table planif avec la région définie ci-dessus.

                    if ls_planif.count() > 0 then
                        f_sauve_stock_planif(ls_planif, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, res, msg); -- Return 'OK' si insert effectué
                        if res = 0 then
                            close C_ARTICLE;
                            close C_LIG_EDI_L;
                            return;
                        end if;
                        if res = 1 then
                            ls_ya_stock := 'O';
                        end if;
                    end if;

                    if ls_ya_stock = 'O' and arg_stock_type = 'S' then
                        goto continue_label;
                    end if;

                    if ls_region <> '''SE''' and ls_region <> '''SW'',''UDC''' and ls_region <> '''UDC''' and ls_region <> '''VDL''' and ls_region <> '%' then -- Le client souhaite une station particuliere
                        f_controle_stock(ls_region, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'STATION', array_bws_ecris, arg_stock_type, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert
                        if res = 0 then
                            close C_ARTICLE;
                            close C_LIG_EDI_L;
                            return;
                        end if;

                        if res = 1 then
                            ls_ya_stock := 'O';
                        end if;
                    else
                        f_controle_stock(ls_region, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'DANS_BASSIN', array_bws_ecris, arg_stock_type, res, msg); -- Return 'OK' si insert effectué sinon 'KO' aucun insert
                        if res = 0 then
                            close C_ARTICLE;
                            close C_LIG_EDI_L;
                            return;
                        end if;

                        if res = 1 then
                            ls_ya_stock := 'O';
                        end if;

                        if ls_ya_stock = 'O' and arg_stock_type = 'S' then
                            goto continue_label;
                        end if;

                        f_controle_stock(ls_region, ls_art_ref, arg_num_cde_edi, ll_ref_edi_ligne, arg_cam_code, 'HORS_BASSIN', array_bws_ecris, arg_stock_type, res, msg);
                        if res = 0 then
                            close C_ARTICLE;
                            close C_LIG_EDI_L;
                            return;
                        end if;

                        if res = 1 then
                            ls_ya_stock := 'O';
                        end if;

                        if ls_ya_stock = 'O' and arg_stock_type = 'S' then
                            goto continue_label;
                        end if;
                    end if;

                    -- On a pas trouvé de stock pour le GTIN client. On insére une ligne sans fournisseur
                    if ls_ya_stock = 'N' then
                        f_sauve_stock('', arg_num_cde_edi, ll_ref_edi_ligne, ls_art_ref, arg_cam_code, 'SANS_STOCK', array_bws_ecris,'?', '?', res, msg);
                        if res = 0 then
                            close C_ARTICLE;
                            close C_LIG_EDI_L;
                            return;
                        end if;
                    end if;

                    <<continue_label>> null;

                    fetch C_ARTICLE into ls_art_ref;
                    EXIT WHEN C_ARTICLE%notfound;
                end loop;
                close C_ARTICLE;
            exception when others then
                close C_ARTICLE;
                close C_LIG_EDI_L;
                delete from GEO_STOCK_ART_EDI_BASSIN where edi_ord = arg_num_cde_edi;
                commit;
                res := 0;
                msg := '%%%ERREUR aucune référence article BW pour le GTIN article client : ' || ls_ean_prod_client || ' et code article client: ' || ls_art_ref_client;
                return;
            end;
            fetch C_LIG_EDI_L into ll_ref_edi_ligne, ll_num_ligne, ls_ean_prod_client, ll_quantite_colis, ld_prix_vente_edi, ls_cli_ref, ls_cen_ref, ls_art_ref_client, ls_inc_code, ls_region_pref;
            EXIT WHEN C_LIG_EDI_L%notfound;
        end loop;
        close C_LIG_EDI_L;
    exception when others then
        close C_LIG_EDI_L;
        res := 0;
        msg := '%%%ERREUR lecture commande EDI: ' || arg_num_cde_edi;
        return;
    end;

    commit;
    res := 1;

end;
/
