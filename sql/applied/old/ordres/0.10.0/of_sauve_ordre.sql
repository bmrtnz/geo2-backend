--of_sauve_ordre
-- sauve ordre
-- AR 09/04/08 création (permet d'être moins explicite sur la méthode pour sauver un ordre
-- AR 19/04/10 implémente of_verif_litlig
-- SL 29/01/13 verifie que le champ palette n'est pas null. sinon bug et génère un fichier vide pour le FTP vers SATAR 
CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_SAUVE_ORDRE" (
    arg_ord_ref GEO_ORDRE.ord_ref%type,
	res out number,
    msg out varchar2
) AS
    ll_RowCount number;
    ll_RowCountbis number;
    ll_rslt number; --LLEF
    ll_cde_nb_col number;
    ls_rc varchar2(50);
    ls_regime_tva varchar2(50);
    ls_fra_code varchar2(50);
    ls_trp_code_plus varchar2(50);
    ls_ref_art_grp varchar2(50);
    ls_ref_art varchar2(50);
    ls_gtin varchar2(50);
    ls_gem_code varchar2(50);
    ls_ref_grp_suiv varchar2(50);
    ls_ref_suiv varchar2(50);--LLEF
    ls_gratuit varchar2(50);
    ls_erreur_kit varchar2(50);
    ls_erreur varchar2(50);
    ls_arret varchar2(50); --LLEF
    ls_art varchar2(50);
    ls_last_ord varchar2(50);
    ls_orl_ref varchar2(50);
    ls_stm_ref varchar2(50);
    is_edi_ord varchar2(50);
    is_cur_cli_ref varchar2(50);
begin

    msg := '';
    res := 0;

    select o.ref_edi_ordre, o.cli_ref
    into is_edi_ord, is_cur_cli_ref
    from geo_ordre o
    where ord_ref = arg_ord_ref;

    -- A APPLIQUER DIRECTEMENT SUR LA GRILLE DES LIGNES DE LITIGES
    -- if of_verif_litlig() = 0 then return -1

    -- Verification des régimes de TVA
    -- Retirer le 20/05/2020 LLEF
    -- Cause des soucis dans le GetFocus au niveau des lignes articles:
    -- Message d'information sur incompatibilité des codes TVA sur fournisseurs s'affiche en boucle et impossible de fermer GEO
    -- Vérification faite au moment de la mise en BAF et de la confirmation d'ordre
    /*
    ls_rc = of_calcul_regime_tva_encours(is_cur_ord_ref,is_tvr_code_entrepot, ls_regime_tva) 
    if ls_rc <> '' then
                    MessageBox("validation refusée",ls_rc + '~nveuillez faire les modifications nécessaires', StopSign!)
                    return -1
    end if
    */

    of_verif_logistique_depart(arg_ord_ref, res, msg);
    if res = 0 then
        res := -1;
        return;
    end if;

    -- On vérifie que les champs palettes sont bien alimentés
    declare
        cursor cur_ols is
            select
                pal_code,
                orl_ref
            from geo_ordlig
            where ord_ref = arg_ord_ref;
    begin
        for r in cur_ols
        loop
            if r.pal_code is null or r.pal_code = '' then
                update geo_ordlig
                set pal_code = '-'
                where orl_ref = r.orl_ref;
                commit;
            end if;
        end loop; 
    end;

    -- On vérifie qu'il existe un fournisseur pour ordre+
    declare
        cursor cur_ofra is
            select
                fra_code,
                trp_code_plus
            from geo_ordfra
            where ord_ref = arg_ord_ref;
    begin
        for r in cur_ofra
        loop
            IF r.fra_code <> 'DIVERS' and  r.fra_code <> 'ANIM' and r.fra_code <> 'QUAI'  and  r.fra_code <> 'FRET' and  r.fra_code <> 'TRANSI' then
                If r.trp_code_plus ='-' or r.trp_code_plus='' or r.trp_code_plus is null Then
                    msg := 'validation refusée: veuillez saisir un fournisseur dans ordre+';
                    res := -1;
                    return;
                end If;    
            End If;
        end loop; 
    end;

    -- Deb LLEF
    --On vérifie que les ref art grp qui sont identiques correspondent à une même:
    -- - varitée
    -- - mode de culture
    -- - Origine
    -- - Code colie : "emballage"
    -- - etiquette client
    -- - stickeur
    -- - marque
    -- Exception faite pour les articles tagger "GRATUIT" et "KIT ARTICLE"

    declare
        cursor cur_ols is
            select
                art_ref_kit,
                art_ref,
                ind_gratuit,
                orl_ref
            from geo_ordlig
            where ord_ref = arg_ord_ref;
        ll_rslt number := -1;
        ls_erreur varchar2(50)        := 'N';
        ls_erreur_kit varchar2(50)    := 'N';
    begin
        for r in cur_ols
        loop   
            if  r.art_ref <> r.art_ref_kit  then
                select GEM_CODE into ls_gem_code
                from geo_colis C, geo_article A 
                where  A.art_ref = r.art_ref_kit
                and A.esp_code = C.esp_code
                and A.col_code = C.col_code;
                if ls_gem_code = 'KIT' then
                    if r.ind_gratuit = 'N' then
                            ls_erreur_kit := 'O';
                    end if;
                else
                    if  r.ind_gratuit = 'N' then
                        select count(*) into ll_rslt
                        from geo_article A1, geo_article A2
                        where A1.art_ref =r.art_ref and A2.art_ref=r.art_ref_kit
                        and A1.var_code = A2.var_code
                        and A1.mode_culture = A2.mode_culture
                        and a1.ORI_CODE = A2.ORI_CODE
                        and a1.col_code = A2.COL_CODE
                        and a1.etc_code = A2.ETC_code
                        and a1.etf_code = A2.ETF_CODE
                        and a1.maq_code = a2.maq_code;
                    else
                        ls_erreur := 'O';
                    end if;
                end if; 	
                if ll_rslt = 0 or ls_erreur='O' or ls_erreur_kit ='O' then
                    msg := 'validation refusée: Le regroupement de la ref art: ' ||  ls_ref_art || ' sur la ref grp: ' || ls_ref_art_grp || ' est impossible. La sauvegarde est annulée';
                    select A1.GTIN_COLIS_BW into ls_gtin  from geo_article A1 where A1.art_ref = r.art_ref; 
                    --deb llef 
                    --  idw_lig_cde.SetItem( ll_RowCount, 'geo_ordlig_gtin_colis_kit', ls_gtin) 
                    --on ne modifie par le GTIN de l'EDI
                    if is_edi_ord is not null then
                        update geo_ordlig
                        set gtin_colis_kit = ls_gtin
                        where orl_ref = r.orl_ref;
                        commit;
                    end if;
                    --fin llef
                    update geo_ordlig
                    set art_ref = r.art_ref
                    where orl_ref = r.orl_ref;
                    commit;
                    ls_arret := 'O';
                end if;	   
            /*	else
                if ls_ref_art = ls_ref_art_grp  and ls_gratuit =  'O' then
                    for ll_RowCountbis = ll_RowCount + 1 to idw_lig_cde.rowcount( )
                        ls_ref_grp_suiv = idw_lig_cde.getitemstring( ll_RowCountbis, 'geo_ordlig_art_ref_kit')
                        ls_ref_suiv 	= idw_lig_cde.getitemstring( ll_RowCountbis, 'geo_ordlig_art_ref')
                        if  ls_ref_grp_suiv = ls_ref_art then
                            MessageBox("validation refusée",'Le regroupement de la ref art: ' +  ls_ref_suiv + ' sur la ref grp: ' + ls_ref_grp_suiv + ' est impossible. La sauvegarde est annulée' , StopSign!)
                        select A1.GTIN_COLIS_BW into :ls_gtin  from geo_article A1 where A1.art_ref = :ls_ref_suiv 
                        USING sqlca;
                        idw_lig_cde.SetItem( ll_RowCountbis, 'geo_ordlig_gtin_colis_kit', ls_gtin) 
                        idw_lig_cde.setitem( ll_RowCountbis, 'geo_ordlig_art_ref_kit', ls_ref_suiv) 
                        ls_arret = 'O'
                        end if
                    next
                end if  */
            end if;
        end loop;
    end;

        
    if ls_arret ='O' then
        res := -1;
        return;
    end if;
    -- Fin LLEF

    --Commande EDI: Suppresion puis INSERT ref article dans la table GEO_EDI_ART_CLI
    -- TABLE STRUCTURE CHANGE, removing for now
    if is_edi_ord is not null then
        declare
            cursor cur_ols is
                select
                    art_ref,
                    gtin_colis_kit,
                    cde_nb_col
                from geo_ordlig
                where ord_ref = arg_ord_ref;
        begin
            for r in cur_ols
            loop
                SELECT LAST_ORD into ls_last_ord FROM GEO_EDI_ART_CLI 
                WHERE GTIN_COLIS_CLIENT = r.gtin_colis_kit 
                AND CLI_REF = is_cur_cli_ref
                AND ROWNUM = 1
                ORDER BY LAST_ORD;
                if arg_ord_ref >= ls_last_ord AND ll_cde_nb_col > 0 then
                    begin
                        DELETE FROM GEO_EDI_ART_CLI
                        WHERE CLI_REF = is_cur_cli_ref
                        AND GTIN_COLIS_CLIENT = r.gtin_colis_kit;
                        INSERT INTO GEO_EDI_ART_CLI (ART_REF,CLI_REF,GTIN_COLIS_CLIENT,LAST_ORD) 
                        VALUES (r.art_ref, is_cur_cli_ref, r.gtin_colis_kit, arg_ord_ref);
                    exception when others then
                        msg := 'Erreur insert GEO_EDI_ART_CLI: Pb Insert sur GEO_EDI_ART_CLI' || SQLERRM;
                        return;
                    end;
                end if;
            end loop;
        end;
    end if;
    --FIN Commande EDI
    -- my_window.TriggerEvent("pfc_save")

    -- on actualise le mouvement de stock par la PK de la ligne insérée
    declare
        cursor cur_ols is
            select
                orl_ref,
                stm_ref
            from geo_ordlig
            where ord_ref = arg_ord_ref;
    begin
        for r in cur_ols
        loop
            if r.stm_ref is not null and r.stm_ref <> '' then	
                update geo_stomvt set orl_ref = r.orl_ref where stm_ref = r.stm_ref;
                commit;
            end if;
        end loop; 
    end;

    msg := 'OK';
    res := 1;
    return;

end;