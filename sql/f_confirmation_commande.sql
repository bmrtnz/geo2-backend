CREATE OR REPLACE PROCEDURE F_CONFIRMATION_COMMANDE (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    is_soc_dev_code IN GEO_SOCIETE.DEV_CODE%TYPE,
    is_tvr_code_entrepot IN GEO_ENTREP.TVR_CODE%TYPE,
    is_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
    is_cur_cen_code IN varchar2,
    is_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
    is_cam_code_old IN GEO_ORDRE.CAM_CODE%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ll_count number;
    ld_TRP_PU number;
    ld_TRP_DEV_PU number;
    ld_TRP_DEV_TAUX number;
    ls_TRP_DEV_CODE number;
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    -- ls_rc;
    ls_regime_tva GEO_TVAREG.TVR_CODE%TYPE;
    lc_art_ref sys_refcursor;
    ls_art_ref GEO_ORDLIG.ART_REF%TYPE;
    msg_verif_pal varchar2(1000);
    msg_verif_art varchar2(1000);
BEGIN
    -- correspond à f_confirmation_commande.pbl
    msg := '';
    res := 0;

    select TRP_PU, TRP_DEV_PU, TRP_DEV_TAUX, TRP_DEV_CODE
    into ld_TRP_PU, ld_TRP_DEV_PU, ld_TRP_DEV_TAUX, ls_TRP_DEV_CODE
    from geo_ordre
    where ord_ref = is_ord_ref;

    If ( ld_TRP_PU is null OR ld_TRP_PU = 0 ) AND ld_TRP_DEV_PU > 0 Then
        If ld_TRP_DEV_TAUX is null OR ld_TRP_DEV_TAUX = 0 Then
            ld_TRP_DEV_TAUX := 1;
            ls_TRP_DEV_CODE := is_soc_dev_code;

            update geo_ordre set TRP_DEV_TAUX = ld_TRP_DEV_TAUX, TRP_DEV_CODE = ls_TRP_DEV_CODE where ORD_REF = is_ord_ref;
        End if;

        ld_TRP_PU := ld_TRP_DEV_PU * ld_TRP_DEV_TAUX;
        update geo_ordre set TRP_PU = ld_TRP_PU where ord_ref = is_ord_ref;
    End If;

    of_sauve_ordre(is_ord_ref ,res, msg);

    select TYP_ORDRE into ls_typ_ordre from GEO_ORDRE
    where ORD_REF = is_ord_ref;

    -- on verifie qu'il n'y a pas de lignes fantômes
    select count(orl_ref) into ll_count from geo_ordlig where ord_ref = is_ord_ref and (art_ref is null or fou_code is null);
    if ll_count > 0 then
        delete from geo_ordlig where ord_ref = is_ord_ref and (art_ref is null or fou_code is null);
        commit;

        -- TODO
        /*IF ib_ligne_ordre_bloquer = TRUE 	then
                ls_bloque ='1'
        ELSE
                ls_bloque ='0'
        End IF

        idw_lig_cde.Retrieve(is_cur_ord_ref,ls_bloque)*/
    end if;

    -- Verification des régimes de TVA
    f_calcul_regime_tva(is_ord_ref,is_tvr_code_entrepot, ls_regime_tva, msg);
    if msg <> '' then
        msg := 'validation refusée:' || msg || '~nveuillez faire les modifications nécessaires';
        return;
    end if;

    -- on s'assure de la synchro avec la logistique
    f_verif_logistique_ordre(is_ord_ref, res, msg);

    if is_soc_code = 'BWS' then
        of_get_article_bws_non_ref(is_ord_ref, lc_art_ref, res, msg);
    else
        of_get_article_bwstoc_non_ref(is_ord_ref, lc_art_ref, res, msg);
    end if;

    if lc_art_ref%rowcount > 0 then
        msg := 'les articles ';

        loop
            fetch lc_art_ref into ls_art_ref;
            EXIT WHEN lc_art_ref%notfound;

            msg := msg || ls_art_ref || '/';
            update geo_article set bwstock = 'O' where art_ref = ls_art_ref;
        end loop;

        msg := msg || 'ne sont pas référencés pour BWSTOC - veuillez les avertir';
    end if;

    of_verif_palette_chep(is_ord_ref, is_soc_code, is_cur_cen_code,res, msg_verif_pal);
    if msg_verif_pal <> '' then
        msg := 'validation refusée ' || msg_verif_pal || ' - veuillez faire les modifications nécessaires';
        res := -1;
        return;
    ELSE
        of_verif_palette(is_ord_ref, is_soc_code, is_cur_cen_code, res, msg_verif_pal);
        if msg_verif_pal <> '' then
            msg := 'validation refusée ' || msg_verif_pal || ' - veuillez faire les modifications nécessaires';
            res := -1;
            return;
        end if;
    End IF;

    -- LLEF: Blocage si article IFCO et entrepôt n'est pas IFCO
    -- Uniquement sur la SA et pas pour les PREORDRE
    if is_soc_code = 'SA' and substr(is_cur_cen_code, 1, 6) <> 'PREORD' then
        of_verif_article_ifco(is_ord_ref, res, msg_verif_art);

        if msg_verif_art <> '' then
            msg := msg_verif_art || '- veuillez faire les modifications nécessaires';
            res := -1;
            return;
        end if;
    end if;
    -- FIN LLEF

    If ls_typ_ordre = 'RGP' THEN
        f_verif_coherence_rgp_orig(is_ord_ref, res, msg);
        if res <> 1 then
            return;
        end if;

        f_verif_confirmation_ordre(is_ord_ref, is_soc_code, is_utilisateur, is_cam_code, is_cam_code_old, res, msg);
        if res <> 1 then
            return;
        end if;

        f_ctrl_coherence_orig_art_sta(is_ord_ref, res, msg);
        If res <> 1 then
            return;
        end if;
    end if;

    res := 1;
    msg := 'OK';
END F_CONFIRMATION_COMMANDE;
/
