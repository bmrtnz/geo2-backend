DROP PROCEDURE GEO_ADMIN.F_DUPLICATION_BUK_SA;

CREATE OR REPLACE PROCEDURE GEO_ADMIN."F_DUPLICATION_BUK_SA" (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    is_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
    is_tvr_code_entrepot IN varchar2,
    res in out number,
    msg in out varchar2
) AS
    ls_bloque varchar2(50);
    ll_rc number;
    ll_count number;
    ll_ind number;
    ls_rc varchar2(50);
    ls_art_ref varchar2(50);
    ls_art_ref_array p_str_tab_type := p_str_tab_type();
    lc_art_ref sys_refcursor;
    ls_regime_tva varchar2(50);
    ls_typ_ordre varchar2(50);

    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;
    ls_tvr_code_entrepot GEO_ENTREP.TVR_CODE%TYPE;
    ls_cur_cen_code GEO_ENTREP.CEN_CODE%TYPE;
    ld_TRP_PU number;
    ld_TRP_DEV_PU number;
    ld_TRP_DEV_TAUX number;
    ls_TRP_DEV_CODE GEO_ORDRE.TRP_DEV_CODE%TYPE;
    msg_verif_pal varchar2(1000);
    msg_verif_art varchar2(1000);

BEGIN
    res := 0;

    select o.TRP_PU, o.TRP_DEV_PU, o.TRP_DEV_TAUX, o.TRP_DEV_CODE, e.TVR_CODE, e.CEN_CODE
    into ld_TRP_PU, ld_TRP_DEV_PU, ld_TRP_DEV_TAUX, ls_TRP_DEV_CODE, ls_tvr_code_entrepot, ls_cur_cen_code
    from geo_ordre o, GEO_ENTREP e, GEO_CLIENT c
    where o.ord_ref = is_ord_ref and o.cen_ref = e.cen_ref and o.CLI_REF = c.CLI_REF AND ROWNUM = 1;

    of_sauve_ordre(is_ord_ref,res,msg);
    f_calcul_marge(is_ord_ref,res,msg);
    of_sauve_ordre(is_ord_ref,res,msg);

    select TYP_ORDRE into ls_typ_ordre
    from GEO_ORDRE
    where ORD_REF =is_ord_ref;

    IF  ls_typ_ordre  ='RGP' Then
        msg := 'ERREUR: Un ordre de regroupement ne peut pas être regroupé';
        res := 0;
        return;
    End If;


-- on verifie qu'il n'y a pas de lignes fantômes
    select count(orl_ref) into ll_count from geo_ordlig where ord_ref = is_ord_ref and (art_ref is null or fou_code is null);
    if ll_count > 0 then
        delete from geo_ordlig where ord_ref = is_ord_ref and (art_ref is null or fou_code is null);
        commit;

        /*IF ib_ligne_ordre_bloquer = TRUE 	then
                ls_bloque ='1'
        ELSE
                ls_bloque ='0'
        End IF

        idw_lig_cde.Retrieve(is_ord_ref,ls_bloque)*/
    end if;

    -- Verification des régimes de TVA
    f_calcul_regime_tva(is_ord_ref,is_tvr_code_entrepot, ls_regime_tva, msg);
    if msg is not null then
        msg := 'validation refusée:' || msg || '~nveuillez faire les modifications nécessaires';
        return;
    end if;

    -- on s'assure de la synchro avec la logistique
    f_verif_logistique_ordre(is_ord_ref, res, msg);
    if (msg <> 'OK') then
        msg := 'validation refusée' || msg || '~nveuillez faire les modifications si nécessaires';
        return;
    end if;

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
		close lc_art_ref;
        msg := msg || 'ne sont pas référencés pour BWSTOC - veuillez les avertir';
    end if;

    of_verif_palette_chep(is_ord_ref, is_soc_code, ls_cur_cen_code, res, msg_verif_pal);
    if msg_verif_pal is not null then
        msg := 'validation refusée ' || msg_verif_pal || ' - veuillez faire les modifications nécessaires';
        res := 0;
        return;
    ELSE
        of_verif_palette(is_ord_ref, is_soc_code, ls_cur_cen_code, res, msg_verif_pal);
        if msg_verif_pal is not null then
            msg := 'validation refusée ' || msg_verif_pal || ' - veuillez faire les modifications nécessaires';
            res := 0;
            return;
        end if;
    End IF;

    -- LLEF: Blocage si article IFCO et entrepôt n'est pas IFCO
    -- Uniquement sur la SA et pas pour les PREORDRE
    if is_soc_code = 'SA' and substr(ls_cur_cen_code, 1, 6) <> 'PREORD' then
        of_verif_article_ifco(is_ord_ref, res, msg_verif_art);

        if msg_verif_art is not null then
            msg := msg_verif_art || '- veuillez faire les modifications nécessaires';
            res := 0;
            return;
        end if;
    end if;
    -- FIN LLEF

    If ls_typ_ordre = 'RGP' THEN
        f_verif_coherence_rgp_orig(is_ord_ref, res, msg);
        if res <> 1 then
            return;
        end if;
    end if;

    f_verif_confirmation_ordre(is_ord_ref, is_soc_code, is_utilisateur, res, msg);
    if res <> 1 then
        return;
    end if;

    f_ctrl_coherence_orig_art_sta(is_ord_ref, res, msg);
    If res <> 1 then
        return;
    end if;

    res := 1;
    msg := 'OK';
END;
/
