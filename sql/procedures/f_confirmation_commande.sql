CREATE OR REPLACE PROCEDURE F_CONFIRMATION_COMMANDE (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    is_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;
    ls_tvr_code_entrepot GEO_ENTREP.TVR_CODE%TYPE;
    ls_cur_cen_code GEO_ENTREP.CEN_CODE%TYPE;
    ll_count number;
    ld_TRP_PU number;
    ld_TRP_DEV_PU number;
    ld_TRP_DEV_TAUX number;
    ls_TRP_DEV_CODE GEO_ORDRE.TRP_DEV_CODE%TYPE;
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;

    ls_regime_tva GEO_TVAREG.TVR_CODE%TYPE;
    lc_art_ref sys_refcursor;
    ls_art_ref GEO_ORDLIG.ART_REF%TYPE;
    msg_verif_pal varchar2(1000);
    msg_verif_art varchar2(1000);

    ls_sco_code GEO_SECCOM.SCO_CODE%TYPE;
BEGIN
    -- correspond ? f_confirmation_commande.pbl
    msg := '';
    res := 0;

    select o.TRP_PU, o.TRP_DEV_PU, o.TRP_DEV_TAUX, o.TRP_DEV_CODE, e.TVR_CODE, e.CEN_CODE
    into ld_TRP_PU, ld_TRP_DEV_PU, ld_TRP_DEV_TAUX, ls_TRP_DEV_CODE, ls_tvr_code_entrepot, ls_cur_cen_code
    from geo_ordre o, GEO_ENTREP e, GEO_CLIENT c
    where o.ord_ref = is_ord_ref and o.cen_ref = e.cen_ref and o.CLI_REF = c.CLI_REF AND ROWNUM = 1;

    select dev_code into ls_soc_dev_code from GEO_SOCIETE where soc_code = is_soc_code;

    If ( ld_TRP_PU is null OR ld_TRP_PU = 0 ) AND ld_TRP_DEV_PU > 0 Then
        If ld_TRP_DEV_TAUX is null OR ld_TRP_DEV_TAUX = 0 Then
            ld_TRP_DEV_TAUX := 1;
            ls_TRP_DEV_CODE := ls_soc_dev_code;

            update geo_ordre set TRP_DEV_TAUX = ld_TRP_DEV_TAUX, TRP_DEV_CODE = ls_TRP_DEV_CODE where ORD_REF = is_ord_ref;
        End if;

        ld_TRP_PU := ld_TRP_DEV_PU * ld_TRP_DEV_TAUX;
        update geo_ordre set TRP_PU = ld_TRP_PU where ord_ref = is_ord_ref;
    End If;

    of_sauve_ordre(is_ord_ref ,res, msg);

    select TYP_ORDRE into ls_typ_ordre from GEO_ORDRE
    where ORD_REF = is_ord_ref;

    -- on verifie qu'il n'y a pas de lignes fant?mes
    select count(orl_ref) into ll_count from geo_ordlig where ord_ref = is_ord_ref and (art_ref is null or fou_code is null);
    if ll_count > 0 then
        delete from geo_ordlig where ord_ref = is_ord_ref and (art_ref is null or fou_code is null);
        commit;

        /*IF ib_ligne_ordre_bloquer = TRUE     then
                ls_bloque ='1'
        ELSE
                ls_bloque ='0'
        End IF

        idw_lig_cde.Retrieve(is_cur_ord_ref,ls_bloque)*/
    end if;

    -- Verification des r?gimes de TVA
    f_calcul_regime_tva(is_ord_ref,ls_tvr_code_entrepot, ls_regime_tva, msg);
    if msg is not null then
        res := 0;
        msg := 'validation refus?e:' || msg || '~nveuillez faire les modifications n?cessaires';
        return;
    end if;

    -- on s'assure de la synchro avec la logistique
    f_verif_logistique_ordre(is_ord_ref, res, msg);
    if (msg <> 'OK') then
        msg := 'validation refus?e' || msg || '~nveuillez faire les modifications si n?cessaires';
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
        msg := msg || 'ne sont pas r?f?renc?s pour BWSTOC - veuillez les avertir';
    end if;

    of_verif_palette_chep(is_ord_ref, is_soc_code, ls_cur_cen_code, res, msg_verif_pal);
    if msg_verif_pal is not null then
        msg := 'validation refus?e ' || msg_verif_pal || ' - veuillez faire les modifications n?cessaires';
        res := 0;
        return;
    ELSE
        of_verif_palette(is_ord_ref, is_soc_code, ls_cur_cen_code, res, msg_verif_pal);
        if msg_verif_pal is not null then
            msg := 'validation refus?e ' || msg_verif_pal || ' - veuillez faire les modifications n?cessaires';
            res := 0;
            return;
        end if;
    End IF;

    -- LLEF: Blocage si article IFCO et entrep?t n'est pas IFCO
    -- Uniquement sur la SA et pas pour les PREORDRE
    if is_soc_code = 'SA' and substr(ls_cur_cen_code, 1, 6) <> 'PREORD' then
        of_verif_article_ifco(is_ord_ref, res, msg_verif_art);

        if msg_verif_art is not null then
            msg := msg_verif_art || '- veuillez faire les modifications n?cessaires';
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



    begin
    select sco_code into ls_sco_code from geo_ordre where ord_ref = is_ord_ref;
    if is_soc_code ='BWS' and ls_sco_code = 'GB' then
        PRC_CALCUL_VENTE_AUTO(is_ord_ref, '',  'CONFIRM', res, msg);
    end if;
    EXCEPTION WHEN no_data_found THEN
            NULL;
    end;


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
END F_CONFIRMATION_COMMANDE;
/
