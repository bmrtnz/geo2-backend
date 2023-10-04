CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_VERIF_STOCK_DISPO" (
    arg_num_cde_edi IN GEO_EDI_LIGNE.REF_EDI_ORDRE%TYPE,
    arg_entrep IN GEO_ENTREP.CEN_REF%TYPE,
    arg_cam_code IN GEO_CAMPAG.CAM_CODE%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_dept_entrep varchar(10);
    ls_cli_ref GEO_ENTREP.CLI_REF%TYPE;
    ls_pal_code GEO_ENTREP.pal_code%TYPE;
    ls_bac_code GEO_DEPT.BAC_CODE%TYPE;

    ls_plateforme varchar2(100);
    ls_bassin varchar2(100);
    ls_ya_art_bassin char(1);
    ls_ACH_BTA_CODE GEO_STOCK_ART_EDI_BASSIN.ACH_BTA_CODE%TYPE;
    ls_VTE_BTA_CODE GEO_STOCK_ART_EDI_BASSIN.VTE_BTA_CODE%TYPE;
    ls_ACH_DEV_CODE GEO_STOCK_ART_EDI_BASSIN.ACH_DEV_CODE%TYPE;
    ld_ACH_DEV_PU GEO_STOCK_ART_EDI_BASSIN.ACH_DEV_PU%TYPE;
    ld_ACH_PU GEO_STOCK_ART_EDI_BASSIN.ACH_PU%TYPE;
    ld_VTE_PU GEO_STOCK_ART_EDI_BASSIN.VTE_PU%TYPE;
    ld_VTE_PU_NET GEO_STOCK_ART_EDI_BASSIN.VTE_PU_NET%TYPE;

    ls_gem_code GEO_ARTICLE_COLIS.GEM_CODE%TYPE;
    ld_pmb_per_com GEO_ARTICLE_COLIS.U_PAR_COLIS%TYPE;
    ld_pdnet_client GEO_ARTICLE_COLIS.PDNET_CLIENT%TYPE;
    ld_col_tare GEO_ARTICLE_COLIS.COL_TARE%TYPE;

    ls_sto_ref geo_stock.STO_REF%TYPE;
    ls_fou_code geo_stock.fou_code%TYPE;
    ll_qte_res number;
    ls_age number;
    ls_bac_code_station geo_fourni.BAC_CODE%TYPE;
    ls_prop_code geo_stock.prop_code%TYPE;

    ld_ach_dev_taux GEO_STOCK_ART_EDI_BASSIN.ACH_DEV_TAUX%TYPE;
    ll_k_stock_art_edi_bassin GEO_STOCK_ART_EDI_BASSIN.K_STOCK_ART_EDI_BASSIN%TYPE;

    ls_sql clob;

    CURSOR C_LIG_EDI_L (ref_edi GEO_EDI_LIGNE.REF_EDI_ORDRE%TYPE) IS
        select ref_edi_ligne, num_ligne, ean_prod_client, quantite_colis,  prix_vente
        from geo_edi_ligne
        where ref_edi_ordre = ref_edi
        order by ref_edi_ligne, num_ligne;
    CURSOR C_ARTICLE (ref_cli geo_edi_art_cli.CLI_REF%TYPE, gtin_client GEO_EDI_ART_CLI.GTIN_COLIS_client%TYPE) IS
        select E.art_ref
        from geo_edi_art_cli E, geo_article_colis A
        where gtin_colis_client = gtin_client
          and cli_ref = ref_cli
          and E.valide = 'O'
          and E.art_ref = A.art_ref
          and A.valide = 'O'
        order by E.priorite;
    C_STOCK SYS_REFCURSOR;
BEGIN
    -- corresponds à f_verif_stock_dispo.pbl
    res := 0;
    msg := '';

    begin
        select substr(zip,1,2), cli_ref, pal_code into ls_dept_entrep, ls_cli_ref, ls_pal_code
        from geo_entrep
        where cen_ref = arg_entrep;

        select bac_code into ls_bac_code
        from GEO_DEPT
        where num_dept = ls_dept_entrep;

        case
            when ls_bac_code = 'VDL' then
                ls_plateforme := 'TERRYPRES';
                ls_bassin     := '''' || ls_bac_code || '''';
            when ls_bac_code = 'SW' or ls_bac_code = 'UDC' then
				ls_plateforme := 'MOISPRESCDV';
				ls_bassin 	  := '''SW'',''UDC''';
            when ls_bac_code = 'SE' then
                ls_plateforme := 'CHANTEPRES';
				ls_bassin	  := '''' || ls_bac_code || '''';
            when ls_bac_code = 'INDIVIS' then
                ls_plateforme := '%';
				ls_bassin	  := '%';
        end case;
    exception when others then
        msg := 'Erreur : ' || SQLERRM;
        return;
    end;

    -- Récupération de l'ensemble des lignes articles de la commande EDI
    for r in C_LIG_EDI_L(arg_num_cde_edi)
    LOOP
        -- Récupération des codes article du client par priorité ayant ce GTIN
        ls_ya_art_bassin := 'N';
        ls_ACH_BTA_CODE := 'KILO';
		ls_VTE_BTA_CODE := 'KILO';
		ls_ACH_DEV_CODE := 'EUR';
		ld_ACH_DEV_PU	:= 0;
		ld_ACH_PU		:= 0;
		ld_VTE_PU		:= r.PRIX_VENTE;
		ld_VTE_PU_NET	:= r.PRIX_VENTE;

        -- Contrôle si l'ean_prod_client existe dans la table de référence du client
		-- Si aucune référence alors on supprime toutes les précédentes ref et on return pour erreur
        for ll_ind IN 1 .. 4
        LOOP
            /*
                1: stock sur la plateforme ==> VDL : Terryloire, SO : Quercy Soleil, 	SE : Chantegrillet pour les leclerc
                2: stock dans le bassin de l'entrepôt
                3: stock dans un autre bassin
                4: Enregistrement sans station ni proprietaire
			*/
            begin
                for a in C_ARTICLE(ls_cli_ref, r.EAN_PROD_CLIENT)
                LOOP
                    if ls_ya_art_bassin = 'N' then
                        if ll_ind < 4 then
                            -- Init. avec les valeurs de base
                            begin
                                select AC.GEM_CODE, AC.U_PAR_COLIS, AC.pdnet_client, AC.col_tare
                                into ls_gem_code, ld_pmb_per_com, ld_pdnet_client, ld_col_tare
                                FROM GEO_ARTICLE_COLIS AC
                                where AC.ART_REF = a.ART_REF and
                                        AC.valide ='O';
                            exception when no_data_found then
                                msg := msg || ' Echec de l''initialisation des valeurs de base pour l''article ' || a.art_ref;
                            end;

                            case
                                when ls_gem_code = 'PLX' or ls_gem_code = 'PLX2' or ls_gem_code = 'CAISSE' then
									ls_vte_bta_code := 'KILO';
                                when ls_gem_code = 'UCPLT' then
                                    ls_vte_bta_code := 'COLIS';
                                when ls_gem_code = 'UCBARQ' then
                                    ls_vte_bta_code := 'BARQUE';
                                when ls_gem_code = 'UCSAC' then
                                    ls_vte_bta_code := 'SACHET';
                                else
                                    ls_vte_bta_code := 'KILO';
                            end case;

                            -- Récupération des stations du même bassin ayant du stock pour les refs article trouvées
                            ls_sql := ' select sto_ref, S.fou_code, qte_ini - qte_res, age, bac_code, S.prop_code ';
                            ls_sql := ls_sql || ' from geo_stock S, geo_fourni F ';
                            ls_sql := ls_sql || ' where art_ref = ''' || a.ART_REF || '''';
                            ls_sql := ls_sql || ' and S.fou_code = F.fou_code ';
                            ls_sql := ls_sql || ' and (qte_ini - qte_res) >= ' || to_char(r.QUANTITE_COLIS);
                            case ll_ind
                                when 1 then
                                    if ls_plateforme = '%' then
                                        ls_sql := ls_sql || ' and F.fou_code like ''' || ls_plateforme || '''';
                                    else
                                        ls_sql := ls_sql || ' and F.fou_code = ''' || ls_plateforme || '''';
                                    end if;
                                when 2 then
                                    if ls_bassin = '%' then
                                        ls_sql := ls_sql || ' and F.bac_code like ''' || ls_bassin || '''';
                                    else
                                        ls_sql := ls_sql || ' and F.bac_code in (' || ls_bassin || ') and F.bac_code <> ''' || ls_plateforme || '''';
                                    end if;
                                when 3 then
                                    if ls_bassin = '%' then
                                        ls_sql := ls_sql || ' and F.bac_code like ''' || ls_bassin || '''';
                                    else
                                        ls_sql := ls_sql || ' and F.bac_code not in (' || ls_bassin || ')';
                                    end if;
                            end case;
                            ls_sql := ls_sql || ' and S.valide = ''O'' ';
                            ls_sql := ls_sql || ' and F.valide = ''O'' ';
                            ls_sql := ls_sql || ' and S.pal_code = ''' || ls_pal_code || ''''; -- Même code palette que celui de l'entrepôt
                            ls_sql := ls_sql || ' order by age desc ';

                            OPEN C_STOCK FOR to_char(ls_sql);
                            LOOP
                                fetch C_STOCK into ls_sto_ref, ls_fou_code, ll_qte_res, ls_age, ls_bac_code_station, ls_prop_code;
                                EXIT WHEN C_STOCK%notfound;

                                -- INSERT DANS TABLE GEO_STOCK_ART_EDI_BASSIN
                                if ls_ya_art_bassin = 'N' then

                                    begin
                                        -- Récupération des informations d'achats de la précédente commande
                                        select *
                                        into ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_VTE_BTA_CODE, ld_VTE_PU, ld_VTE_PU_NET, ld_ach_dev_taux
                                        from (
                                        select ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, VTE_PU_NET, ACH_DEV_TAUX
                                        from GEO_STOCK_ART_EDI_BASSIN
                                        where cli_ref = ls_cli_ref
                                            and art_ref = a.ART_REF
                                            and gtin = r.EAN_PROD_CLIENT
                                            and fou_code = ls_fou_code
                                            and prop_code = ls_prop_code
                                        order by K_STOCK_ART_EDI_BASSIN DESC )
                                        where rownum = 1;
                                    exception when others then
                                        null;
                                    end;

                                    -- Verification si une ligne à déjà été inséré suite pb de double écriture de la ligne article
                                    begin
                                        select k_stock_art_edi_bassin into ll_k_stock_art_edi_bassin
                                        from GEO_STOCK_ART_EDI_BASSIN
                                        where edi_ord = arg_num_cde_edi
                                          and EDI_LIG = r.REF_EDI_LIGNE
                                          and CLI_REF = ls_cli_ref
                                          and CAM_CODE = arg_cam_code
                                          and ART_REF = a.ART_REF
                                          and GTIN = r.EAN_PROD_CLIENT;

                                        DELETE FROM GEO_STOCK_ART_EDI_BASSIN where k_stock_art_edi_bassin = ll_k_stock_art_edi_bassin;
                                    exception when others then null;
                                    end;

                                    begin
                                        insert into GEO_STOCK_ART_EDI_BASSIN (EDI_ORD, EDI_LIG, CLI_REF, CAM_CODE, ART_REF, GTIN, FOU_CODE, BAC_CODE, QTE_RES, AGE, PROP_CODE, ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, VTE_PU_NET, ACH_DEV_TAUX)
                                        values (arg_num_cde_edi, r.REF_EDI_LIGNE, ls_cli_ref, arg_cam_code, a.ART_REF, r.EAN_PROD_CLIENT, ls_fou_code, ls_bac_code_station, ll_qte_res, ls_age, ls_prop_code, ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_VTE_BTA_CODE, ld_VTE_PU, ld_VTE_PU_NET, ld_ach_dev_taux);

                                        ls_ya_art_bassin := 'O';
                                    exception when others then
                                        msg := 'Erreur a la creation des lignes GEO_STOCK_ART_EDI_BASSIN : ' || SQLERRM;
                                        rollback;
                                        return;
                                    end;
                                end if;
                            end loop;
                            CLOSE C_STOCK;
                            -- FIN STOCK
                        else -- ll_ind = 4 : Enregistrement sans informations sur stock
                            ls_fou_code := '';
                            ls_prop_code := '';
                            ls_bac_code_station := ' ';
                            ll_qte_res := 0;
                            ls_age := '';

                            -- Verification si une ligne à déjà été inséré suite pb de double écriture de la ligne article
                            begin
                                select k_stock_art_edi_bassin into ll_k_stock_art_edi_bassin
                                from GEO_STOCK_ART_EDI_BASSIN
                                where edi_ord = arg_num_cde_edi
                                  and EDI_LIG = r.REF_EDI_LIGNE
                                  and CLI_REF = ls_cli_ref
                                  and CAM_CODE = arg_cam_code
                                  and ART_REF = a.ART_REF
                                  and GTIN = r.EAN_PROD_CLIENT;

                                DELETE FROM GEO_STOCK_ART_EDI_BASSIN where k_stock_art_edi_bassin = ll_k_stock_art_edi_bassin;
                            exception when others then null;
                            end;

                            begin
                                insert into GEO_STOCK_ART_EDI_BASSIN (EDI_ORD, EDI_LIG, CLI_REF, CAM_CODE, ART_REF, GTIN, FOU_CODE, BAC_CODE, QTE_RES, AGE, PROP_CODE, ACH_BTA_CODE, ACH_DEV_CODE, ACH_DEV_PU, ACH_PU, VTE_BTA_CODE, VTE_PU, VTE_PU_NET)
                                values (arg_num_cde_edi, r.REF_EDI_LIGNE, ls_cli_ref, arg_cam_code, a.ART_REF, r.EAN_PROD_CLIENT, ls_fou_code, ls_bac_code_station, ll_qte_res, ls_age, ls_prop_code, ls_ACH_BTA_CODE, ls_ACH_DEV_CODE, ld_ACH_DEV_PU, ld_ACH_PU, ls_VTE_BTA_CODE, ld_VTE_PU, ld_VTE_PU_NET);

                                ls_ya_art_bassin := 'O';
                            exception when others then
                                msg := 'Erreur a la creation des lignes GEO_STOCK_ART_EDI_BASSIN : ' || SQLERRM;
                                rollback;
                                return;
                            end;

                        end if;
                    end if;
                end loop;
            exception when others then
                delete from GEO_STOCK_ART_EDI_BASSIN where edi_ord = arg_num_cde_edi;
                msg := msg || ' GTIN: ' || r.EAN_PROD_CLIENT || ' inexistant pour ce client';
                return;
            end;
        end loop;
    end loop;

    commit;
    res := 1;

end F_VERIF_STOCK_DISPO;
/

