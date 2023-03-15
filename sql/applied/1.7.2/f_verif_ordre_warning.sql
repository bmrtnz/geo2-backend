CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_VERIF_ORDRE_WARNING" (
    arg_ord_ref in GEO_ORDLIG.ORD_REF%TYPE,
    arg_soc_code in GEO_SOCIETE.SOC_CODE%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_dev_code GEO_DEVISE.DEV_CODE%TYPE;
    ls_rc clob := '';
    ls_err varchar2(50);
    ls_truc varchar2(50);
    ls_machin varchar2(50);
    ls_rc_tvr varchar2(50);
    ls_laf_no varchar2(50);
    ls_flbaf varchar2(50);
    ls_crlf varchar2(50) := chr(13) || chr(10);
    ld_achqte number;
    ld_achpu number;
    ld_vteqte number;
    ld_vtepu number;
    ld_devtx number;
    ld_achpu_calc number;
    ld_totvte number;
    ld_totrem number;
    ld_totres number;
    ld_totfrd number;
    ld_totach number;
    ld_tottrp number;
    ld_totmob number;
    ld_totcrt number;
    ld_tottrs number;
    ld_marge_montant number;
    ld_marge_pct number;
    ld_max_mt_lig number;
    ls_tvr_code varchar2(50);
    ls_tvr_code_entrep varchar2(50);
    ls_aff_type varchar2(50);
    ls_esp_code varchar2(50);
    ls_compte_vente varchar2(50);
    ls_tvr_code_client varchar2(50);
    ls_fou_code varchar2(50);
    ls_var_code varchar2(50);
    ld_pu_max number;
    ld_exp_pds_net number;
    ld_pu_min number;
    ls_inc_code varchar2(50);
    ls_trp_code varchar2(50);
    ls_inc_rd varchar2(50);
    ld_tottrp_ordre number;
    ls_ach_bta_code varchar2(50);
    ls_vte_bta_code varchar2(50);
    ld_achqte_calc number;
    ld_vteqte_calc number;
    ld_nb_pal number;
    ld_nb_col number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_totfad number;
    ld_cc_trans number;
    ls_facture_avoir varchar2(50);
    ls_ord_ref_pere varchar2(50);
    ls_suivi_pallox varchar2(50);
    ls_ref_cli varchar2(70);
    ls_cli_code varchar2(50);
    ls_cli_ref varchar2(50);
    ls_ref_cli_num varchar2(50);
    ll_count number;
    ldt_livdatp timestamp;
    ldt_depdatp timestamp;
    ll_res number;
    ls_cen_ref varchar2(50);
    ls_ctl_champ varchar2(50);
    ls_gem_code varchar2(50);
    ld_trp_pu number;
    ls_pal_code varchar2(50);
    ls_pal_gestcode varchar2(50);
    ls_palinter_code varchar2(50);
    ls_palinter_gestcode varchar2(50);
    li_nb_pal number;
    li_nb_palinter number;
    ls_propr_code varchar2(50);
    ls_ind_palox_gratuit varchar2(50);
    ls_ind_ordlig_gratuit varchar2(50);
    ls_ind_usage_interne varchar2(50) :='N';
    ls_ind_frais_ramas varchar2(50) := 'N';
    ls_sqlerr varchar2(50);
    ls_col_code varchar2(50);
    ls_sco_code varchar2(50);
    ls_IND_GEST_COLIS_MANQUANT varchar2(50);
    ls_vente_commission varchar2(50);
    ll_count_envois number; --LLEF
    ls_typ_ordre varchar2(50);
    ls_trp_bta_code varchar2(50);
    ll_nb_ligne_ordre number;
    ll_nb_ligne_gratuit number;
    ls_ident_ref_cli varchar2(50); -- Identificateur client devant avoir une ref cli par entrepôt
    ls_find_ref_cli varchar2(70);
    ls_find_cen_ref varchar2(50);
    ls_cen_code varchar2(50);
    ls_nordre varchar2(50);
    ll_edi number;
    ls_bloc_factu_edi varchar2(50);
    ls_cli_est_transporteur varchar2(50) := '';
    ls_fctlvaleurstring varchar(500);
    ll_mont_max_transport number := 15000;
    ll_mont_warning_transport number := 5000;
    CURSOR C2 (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select L.orl_lig, A.esp_code, L.ach_qte, L.ach_pu, L.vte_qte, L.vte_pu, O.dev_tx, V.pu_max, E.pu_min, L.exp_pds_net,
               L.totvte, L.totrem, L.totres, L.totfrd, L.totach, L.totmob, L.tottrp, L.tottrs, L.totcrt, L.fou_code, A.var_code,
               L.ach_bta_code, L.vte_bta_code, L.exp_nb_pal, L.exp_nb_col, L.exp_pds_net, L.exp_pds_brut, C.suivi_pallox, C.gem_code,
               L.propr_code,L.ind_gratuit,C.col_code, round(L.ACH_DEV_PU *L.ACH_DEV_TAUX,2)
        from geo_ordlig L, geo_ordre O, geo_article A,  geo_espece E, geo_colis C, geo_variet V
        where O.ord_ref = ref_ordre
          and A.art_ref = L.art_ref
          and E.esp_code = A.esp_code
          and L.ord_ref = O.ord_ref
          and C.esp_code = A.esp_code
          and C.col_code = A.col_code
          and E.esp_code = V.esp_code
          and A.var_code = V.var_code;
    CURSOR C_typpal (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select L.ORL_LIG,CASE WHEN EXP_NB_PAL is NULL THEN CDE_NB_PAL ELSE EXP_NB_PAL END,L.PAL_CODE,P.GEST_CODE, L.PALINTER_CODE,PI.GEST_CODE,L.PAL_NB_PALINTER
        from GEO_ORDRE O, GEO_ORDLIG L, GEO_PALETT P,GEO_PALETT PI
        where    O.ORD_REF = ref_ordre                      and
                O.ORD_REF           = L.ORD_REF                 and
                L.PAL_CODE          = P.PAL_CODE                and
            (L.PALINTER_CODE IS NOT NULL                or
             L.PAL_CODE <>  L.PALINTER_CODE )       and
                L.PALINTER_CODE         =  PI.PAL_CODE(+);
    CURSOR C_CONTROLE (ref_ordre GEO_ORDRE.ORD_REF%type, reference_client varchar2)
    IS
    select ref_cli, cen_ref,nordre from geo_ordre, geo_client
    where geo_ordre.cli_ref = reference_client and flbaf ='N' and ord_ref <> ref_ordre AND
        ( GEO_CLIENT.CLI_REF = GEO_ORDRE.CLI_REF ) and ( GEO_ORDRE.VALIDE = 'O' ) AND  ( GEO_CLIENT.IND_USINT <> 'O');
BEGIN
    res := 0;
    ls_rc := '';
    select dev_code into ls_dev_code from GEO_SOCIETE where SOC_CODE = arg_soc_code;
    -- ajuste montant maxi ligne en fonction de la devise de référence de la société
    ld_max_mt_lig := case ls_dev_code
        WHEN 'EUR' THEN 30000
        WHEN 'GBP' THEN 50000
        ELSE 1 -- déclenchera warnings intempestifs pour obliger ajuster cette valeur
    end;
    begin
        select geo_ordre.inc_code, geo_ordre.trp_code, geo_ordre.tottrp, geo_incote.inc_rd, geo_ordre.flbaf, geo_ordre. facture_avoir, geo_ordre.ref_cli, geo_ordre.cli_code, geo_ordre.livdatp,geo_ordre.cen_ref,geo_ordre.trp_pu,geo_ordre.depdatp, geo_ordre.totfad,geo_ordre.cli_ref,geo_ordre.sco_code,geo_ordre.vente_commission,geo_ordre.typ_ordre,geo_ordre.trp_bta_code, geo_ordre.cen_code
        into ls_inc_code, ls_trp_code, ld_tottrp, ls_inc_rd, ls_flbaf, ls_facture_avoir, ls_ref_cli, ls_cli_code, ldt_livdatp,ls_cen_ref,ld_trp_pu,ldt_depdatp,ld_totfad,ls_cli_ref,ls_sco_code,ls_vente_commission,ls_typ_ordre,ls_trp_bta_code, ls_cen_code
        from geo_ordre, geo_incote
        where geo_ordre.ord_ref = arg_ord_ref and geo_incote.inc_code = geo_ordre.inc_code;
    exception when no_data_found then
        null;
    end;
    begin
        select  ctl_ref_cli into ls_ctl_champ
        from geo_entrep
        where geo_entrep.cen_ref = ls_cen_ref;
        exception when no_data_found then
            ls_ctl_champ := null;
    end;
    If ls_ctl_champ is null or ls_ctl_champ = '' Then
        begin
            select ctl_ref_cli into ls_ctl_champ
            from geo_client
            where geo_client.cli_ref = ls_cli_ref and geo_client.ctl_ref_cli is not null and ROWNUM = 1;
        exception when no_data_found then
            ls_ctl_champ := '';
        end;
    End If;
    begin
        select IND_PALOX_GRATUIT,IND_USINT,IND_FRAIS_RAMAS, IND_GEST_COLIS_MANQUANT, FL_IDENT_REF_CLI
        into ls_ind_palox_gratuit,ls_ind_usage_interne,ls_ind_frais_ramas, ls_IND_GEST_COLIS_MANQUANT, ls_ident_ref_cli
        from geo_client
        where geo_client.cli_ref = ls_cli_ref and ROWNUM = 1;
    exception when no_data_found then
        null;
    end;
    begin
        select count(*) into ll_nb_ligne_gratuit
        from GEO_ORDLIG
        where ORD_REF = arg_ord_ref and IND_GRATUIT ='O';
        exception when no_data_found then
        ll_nb_ligne_gratuit := 0;
    end;
    begin
        select T.TRP_CODE into ls_cli_est_transporteur
        from GEO_ORDRE O, GEO_TRANSP T
        where   O.ORd_REF = arg_ord_ref and
                O.CLI_REF = T.CLI_REF_ASSOC;
        exception when no_data_found then
            ls_cli_est_transporteur := null;
            when too_many_rows then
              ls_cli_est_transporteur := null;
    end;
    begin
        select count(*) into ll_nb_ligne_ordre
        from GEO_ORDLIG
        where ORD_REF = arg_ord_ref;
        exception when no_data_found then
        ll_nb_ligne_ordre := 0;
    end;
    If ls_ind_usage_interne = 'O' Then
        ls_rc := ls_rc || '(A) %%% le client est à usage interne pas de facturation' || ls_crlf;
    End If;
    If ls_ind_frais_ramas = 'O' and ld_totfad = 0 Then
        ls_rc := ls_rc || '(T) il n''y a pas de frais de ramasse' || ls_crlf;
    End  If;
    If ls_vente_commission ='O' then
        ls_rc := ls_rc || '(P) c''est une  vente à la commission' || ls_crlf;
    End IF;
    -- verif statut bon à facturer OK
    if ls_flbaf = 'O' then
        ls_rc := ls_rc || '(A) %%% l''ordre est déja bon à facturer' || ls_crlf;
        -- deb llef
    else
        begin
            -- Message spécifiant qu'il n'y a pas eu de confirmation au moment de la mise en bon à facturer
            select count(*) into ll_count_envois
            from geo_envois
            where ord_ref = arg_ord_ref and flu_code = 'ORDRE';
            exception when no_data_found then
                ll_count_envois := 0;
        end;
        if ll_count_envois = 0 then
            ls_rc := ls_rc || '(A) Aucune confirmation n''a été effectuée' || ls_crlf;
        end if;
        -- fin llef
    end if;
    -- Vérifi si le transporteur est valide
    if ls_trp_code = '-' then
        ls_rc := ls_rc || '(T) %%% Transporteur à préciser' || ls_crlf;
    end if;
    -- Bruno le 01/09
    -- Ajout du controle paramètrable
    if ls_ref_cli is null then ls_ref_cli := ''; end if;
    f_parse_numeric(ls_ref_cli, ls_ref_cli_num);
    f_ctl_valeur_string('Ref client', length(ls_ref_cli_num), ls_ctl_champ, ls_fctlvaleurstring);
    ls_rc := ls_rc || ls_fctlvaleurstring;
    -- verif ordre pere bon à facturer si avoir
    if ls_facture_avoir = 'A' and ls_ord_ref_pere is not null then
        begin
            select O.flbaf , O.nordre
            into ls_truc, ls_machin
            from geo_ordre O, geo_ordre X
            where X.ord_ref = arg_ord_ref and O.ord_ref = X.ord_ref_pere;
            exception when no_data_found then
                ls_truc := null;
                ls_machin := null;
        end;
        if ls_truc <> 'O' then
            if ls_machin is null then ls_machin := 'd''origine'; end if;
                ls_rc := ls_rc || '(A) %%%  vous devez clotûrer l''ordre ' || ls_machin || ' avant de clotûrer l''avoir correspondant' || ls_crlf;
        end if;
    end if;
    -- date de livraison vide  ?
    if ldt_livdatp is null then
        ls_rc := ls_rc || '(D) %%% la date de livraison est obligatoire' || ls_crlf;
    end if;
    -- date de départ vide  ?
    if ldt_depdatp is null or ldt_depdatp = to_timestamp('01/01/1900 00:00:00', 'DD/MM/RRRR HH24:MI:SS') then
        ls_rc := ls_rc || '(D) %%% la date de départ est obligatoire' || ls_crlf;
    end if;
    If EXTRACT(day from ldt_livdatp - ldt_depdatp) < 0 Then
        ls_rc := ls_rc || '(D) %%% la date de départ (' || to_char(ldt_depdatp, 'dd/mm/yy') || ') est incohérente par rapport à la  date de livraison  ('  || to_char(ldt_livdatp, 'dd/mm/yy') || ')' || ls_crlf;
    End If;
    -- le code incoterm est nécessaire à la facturation
    If ls_inc_code is null or ls_inc_code='' then
        ls_rc := ls_rc || '(T) %%% le code incoterm est obligatoire' || ls_crlf;
    end if;
    -- coût du transport selon procédure ? que dans le cas des factures pas des avoirs
    if ls_trp_code <> 'CLIENT' and ld_tottrp = 0 and ls_inc_rd <> 'D' and ls_facture_avoir = 'F' and (ll_nb_ligne_ordre <> ll_nb_ligne_gratuit) and arg_soc_code <> 'BUK' then
        ls_rc := ls_rc || '(T) %%% le coût prévisionnel du transport est obligatoire pour l''incoterm ' || ls_inc_code || ls_crlf;
    end if;
    IF ld_tottrp > ll_mont_max_transport  Then
        ls_rc      := ls_rc || '(T) %%% le montant transport dépasse ' || to_char(ll_mont_max_transport) || ' ' || ls_crlf;
    Else
        If ld_tottrp > ll_mont_warning_transport  Then
            ls_rc      := ls_rc || '(T)  le montant transport dépasse ' || to_char(ll_mont_warning_transport) || ' ' || ls_crlf;
        End IF;
    End IF;
    CASE ls_inc_code
        WHEN 'EXW' THEN
            If ld_trp_pu > 0 Then
                ls_rc := ls_rc || '(T)  %%% Pour l''incoterm ' || ls_inc_code || ', le pu tarif transport devrait être nulle' || ls_crlf;
            End If;
        WHEN 'CPT' THEN
            If ld_trp_pu is null or ld_trp_pu = 0 Then
                ls_rc := ls_rc || '(T)  %%% Pour l''incoterm ' ||ls_inc_code || ', le pu tarif transport devrait être mentionné' || ls_crlf;
            End IF;
            if ls_trp_bta_code is null or ls_trp_bta_code = '' Then
                ls_rc := ls_rc || '(T)  %%% Pour l''incoterm ' ||ls_inc_code || ', l''unité de transport devrait être mentionné' || ls_crlf;
            End If;
        WHEN 'FCA' THEN
            If ld_trp_pu > 0 Then
                ls_rc := ls_rc || '(T)  %%% Pour l''incoterm ' || ls_inc_code || ', le pu tarif transport devrait être nulle' || ls_crlf;
            End If;
        ELSE
            If ls_trp_code <> 'CLIENT' and (ld_trp_pu is null or ld_trp_pu =0) and ls_facture_avoir = 'F' and arg_soc_code <> 'BUK' Then
                ls_rc := ls_rc || '(T)  %%% Pour l''incoterm ' || ls_inc_code || ', le pu tarif transport devrait être mentionné' || ls_crlf;
            End IF;
            if ls_trp_code <> 'CLIENT' and (ls_trp_bta_code is null or ls_trp_bta_code = '') and ls_facture_avoir = 'F' and arg_soc_code <> 'BUK' Then
                ls_rc := ls_rc || '(T)  %%% Pour l''incoterm ' || ls_inc_code || ', l''unité de transport devrait être mentionné' || ls_crlf;
            End If;
    END CASE;
    begin
        -- tous les détails sont clôturés ?
        select count(0) into ll_count from geo_ordlog
        where ord_ref = arg_ord_ref and flag_exped_fournni <> 'O';
        exception when no_data_found then
            ll_count := 0;
    end;
    if ll_count = 1 then
        ls_rc := ls_rc || '(S) %%%  un détail n''est pas clôturé' || ls_crlf;
    elsif ll_count > 1 then
        ls_rc := ls_rc || '(S) %%%  ' || to_char(ll_count) || ' détails ne sont pas clôturés' || ls_crlf;
    end if;
    begin
        -- aucun détail ?
        select count(*) into ll_count
        from geo_ordlog
        where ord_ref = arg_ord_ref;
        exception when no_data_found then
             ll_count := 0;
    end;
    if ll_count = 0 then
        ls_rc := ls_rc || '(S) %%%  Aucun détail n''est saisi' || ls_crlf;
    end if;
    OPEN C2 (arg_ord_ref);
    LOOP
        fetch C2 into ls_laf_no, ls_esp_code, ld_achqte, ld_achpu, ld_vteqte, ld_vtepu, ld_devtx, ld_pu_max, ld_pu_min, ld_exp_pds_net,
            ld_totvte, ld_totrem, ld_totres, ld_totfrd, ld_totach,  ld_totmob, ld_tottrp, ld_tottrs, ld_totcrt,     ls_fou_code, ls_var_code,
            ls_ach_bta_code, ls_vte_bta_code, ld_nb_pal, ld_nb_col, ld_pds_net, ld_pds_brut, ls_suivi_pallox, ls_gem_code,ls_propr_code,ls_ind_ordlig_gratuit,ls_col_code,ld_achpu_calc;
        EXIT WHEN C2%notfound;
        if ls_suivi_pallox = 'O' and ld_nb_col = 0 and ( ld_achqte > 0 or  ld_vteqte > 0)  then
            ls_rc := ls_rc || '(Q) %%% Ligne=' || ls_laf_no || ' veuillez renseigner le nbre de palox dans  "nombre de colis"' || ls_crlf;
        end if;
        if ls_fou_code = 'STANOR' and ls_var_code = 'KIWI' then
            ls_rc := ls_rc || '(S) %%% Ligne=' || ls_laf_no || ' station STANOR pour KIWI --> passer par KIWICOOP' || ls_crlf;
        end if;
        if ls_fou_code is null or ls_fou_code = '' then
            ls_rc := ls_rc || '(S) Ligne=' || ls_laf_no || ' fournisseur non renseigné' || ls_crlf;
        end if;
        if ls_propr_code is null or ls_propr_code = '' then
            ls_rc := ls_rc || '(S) Ligne=' || ls_laf_no || ' propriétaire non renseigné' || ls_crlf;
        end if;
        -- calcul nombre unité d'achat
        case ls_ach_bta_code
            WHEN 'COLIS' THEN
                ld_achqte_calc := ld_nb_col;
            WHEN 'KILO' THEN
                ld_achqte_calc := ld_pds_net;
                If substr(ls_gem_code,1,2) = 'UC' and arg_soc_code ='SA' Then
                    ls_rc := ls_rc || '(P) Ligne=' || ls_laf_no || ' le prix/kilo achat n''est pas cohérent avec le type UC du colis' || ls_crlf;
                End If;
            WHEN 'PAL' THEN
                ld_achqte_calc := ld_nb_pal;
            ELSE
                ld_achqte_calc := 0;
        END CASE;
        if ld_achqte_calc <> 0 and ld_achqte_calc <> ld_achqte then
            ls_rc := ls_rc || '(Q) Ligne=' || ls_laf_no || ' pb sur nbre unités achat' || ls_crlf;
        end if;
        -- calcul nombre unité de vente
        case ls_vte_bta_code
            WHEN 'COLIS' THEN
                ld_vteqte_calc := ld_nb_col;
            WHEN 'KILO' THEN
                ld_vteqte_calc := ld_pds_net;
                If substr(ls_gem_code,1,2) = 'UC' and arg_soc_code ='SA' Then
                    ls_rc := ls_rc || '(P) Ligne=' || ls_laf_no || ' le prix/kilo vente n''est pas cohérent avec le type UC du colis' || ls_crlf;
                End If;
            WHEN 'PAL' THEN
                ld_vteqte_calc := ld_nb_pal;
            ELSE
                ld_vteqte_calc := 0;
        end case;
        if ld_vteqte_calc <> 0 and ld_vteqte_calc <> ld_vteqte then
            ls_rc := ls_rc || '(Q) Ligne=' || ls_laf_no || ' pb sur nbre unités vente' || ls_crlf;
        end if;
        if ld_pu_max is null then ld_pu_max := 0; end if;
        if ld_exp_pds_net is null then ld_exp_pds_net := 0; end if;
        if ld_pu_max <> 0 and ld_exp_pds_net <> 0  then
            if ld_achpu * ld_achqte > ld_pu_max * ld_exp_pds_net then
                ls_rc := ls_rc || 'Ligne=' || ls_laf_no || '(P) le prix/kilo achat semble trop grand (' || to_char(round(ld_achpu * ld_achqte / ld_exp_pds_net, 2), '0.00') || ' euros supérieur au prix max conseillé '  || to_char(ld_pu_max, '0.00') || ' euros le kilo ) ' || ls_crlf;
            end if;
            if (ld_vtepu * ld_vteqte  * ld_devtx) > ld_pu_max * ld_exp_pds_net  then
                ls_rc := ls_rc || 'Ligne=' || ls_laf_no || '(P) le prix/kilo vente semble trop grand (' || to_char(round(ld_vtepu * ld_vteqte  * ld_devtx / ld_exp_pds_net, 2), '0.00') || ' euros supérieur au prix max conseillé ' || to_char(ld_pu_max, '0.00') || ' euros le kilo ) ' || ls_crlf;
            end if;
        end if;
        if ld_pu_min <> 0 and ld_exp_pds_net <> 0 then
            if ld_achpu * ld_achqte < ld_pu_min * ld_exp_pds_net then
                    ls_rc := ls_rc || 'Ligne=' || ls_laf_no || '(P) le prix/kilo achat semble trop petit (' || to_char(round(ld_achpu * ld_achqte / ld_exp_pds_net, 2), '0.00') || ' euros inferieur au prix mininum ' || to_char(ld_pu_min, '0.00') || ' euros le kilo ) ' || ls_crlf;
            end if;
            if (ld_vtepu * ld_vteqte  * ld_devtx) < ld_pu_min * ld_exp_pds_net and ls_ind_ordlig_gratuit <> 'O' and ls_IND_GEST_COLIS_MANQUANT <> 'O' then
                ls_rc := ls_rc || 'Ligne=' || ls_laf_no || '(P) le prix/kilo vente semble trop petit (' || to_char(round(ld_vtepu * ld_vteqte  * ld_devtx / ld_exp_pds_net, 2), '0.00') || ' euros inferieur au prix mininum ' || to_char(ld_pu_min, '0.00') || ' euros le kilo ) ' || ls_crlf;
            end if;
        end if;
        if (ld_achqte * ld_achpu) > (ld_vteqte * ld_vtepu * ld_devtx) and  ls_var_code <> 'PALLOX'  and ls_ind_ordlig_gratuit <> 'O' and ls_IND_GEST_COLIS_MANQUANT <> 'O' then
            ls_rc := ls_rc || '(P) Ligne=' || ls_laf_no || ' achat ' || to_char(ld_achqte * ld_achpu) || ' > vente ' || to_char(ld_vteqte * ld_vtepu * ld_devtx) || ls_crlf;
        end if;
        If ls_ind_palox_gratuit ='O' and  ls_var_code ='PALLOX' and  ld_vtepu > 0 Then
            ls_rc := ls_rc || '(P) Ligne=' || ls_laf_no || ' Ce client ne doit pas avoir des palox valorisés car il a l''indicateur palox gratuit.' || ls_crlf;
        end if;
        if ld_achqte = 0 and   ( ld_vteqte > 0 or ld_nb_col > 0) then
            ls_rc := ls_rc || '(Q) Ligne=' || ls_laf_no || ' qté achat à zéro' || ls_crlf;
        end if;
        if ld_vteqte = 0 and ( ld_achqte > 0  or ld_nb_col > 0)  then
            ls_rc := ls_rc || '(Q) Ligne=' || ls_laf_no || ' qté vente à zéro' || ls_crlf;
        end if;
        if ld_achpu = 0 and ls_col_code <> 'PLV' and ls_var_code <> 'IFCO' and ( ls_ind_palox_gratuit <> 'O' or  ls_var_code <>'PALLOX') and (ls_cli_est_transporteur ='' or ls_cli_est_transporteur is null)  then
            ls_rc := ls_rc || '(P) Ligne=' || ls_laf_no || ' PU achat à zéro' || ls_crlf;
        end if;
        If (ld_achpu is null and (ls_cli_est_transporteur = '' or ls_cli_est_transporteur is null)) Then
            ls_rc := ls_rc || '(P) %%%  Ligne=' || ls_laf_no || ' PU achat non renseigné' || ls_crlf;
        End If;
        If abs(ld_achpu - ld_achpu_calc) > 0.02 Then
            ls_rc := ls_rc || '(P) %%%  Ligne=' || ls_laf_no || ' Probleme de devise sur achat PU, contacter service informatique' || ls_crlf;
        End If;
        if ld_vtepu = 0 and ls_ind_ordlig_gratuit <> 'O' and ls_IND_GEST_COLIS_MANQUANT <> 'O' then
            ls_rc := ls_rc || '(P) Ligne=' || ls_laf_no || ' PU vente à zéro' || ls_crlf;
        end if;
        if (ld_vteqte * ld_vtepu * ld_devtx) > ld_max_mt_lig then
            ls_rc := ls_rc ||  '(P) Ligne=' || ls_laf_no || ' montant vente ' || to_char(ld_vteqte * ld_vtepu * ld_devtx) || ' > ' || to_char(ld_max_mt_lig) || ' ' || ls_dev_code || ls_crlf;
        end if;
        --ld_marge_montant := ld_totvte - ld_totrem - ld_totres - ld_totfrd - ld_totach - ld_tottrp - ld_totmob - ld_totcrt; -- marge nette
        ld_marge_montant := ld_totvte - ld_totrem - ld_totres - ld_totfrd - ld_totach - ld_tottrp - ld_totcrt; -- marge brute
        if ld_totvte <> 0 then
            ld_marge_pct := round(ld_marge_montant / ld_totvte * 100, 1);
        else
            ld_marge_pct := 0;
        end if;
        if ld_marge_pct < 0 then
            ls_rc := ls_rc || '(P) Ligne ' || ls_laf_no || ' marge ' || to_char(ld_marge_pct) || '%' || ls_crlf;
        end if;
    END LOOP;
    CLOSE C2;
    begin
        -- verif présence lignes à zéro
        select count(0) into ll_count
        from geo_ordlig
        where ord_ref = arg_ord_ref and art_ref is null;
        exception when no_data_found then
            ll_count := 0;
    end;
    if ll_count > 0 then
        delete from geo_ordlig where ord_ref = arg_ord_ref and art_ref is null;
        commit;
        ls_rc := ls_rc ||'(Q)  ' || to_char(ll_count) || ' lignes à zéro ont été supprimées' || ls_crlf;
    end if;
    -- Calcule le régime TVA à appliquer
    -- récupère le regime TVA du client = regime par défaut
    select e.tvr_code, c.tvr_code into ls_tvr_code_entrep, ls_tvr_code_client
    from geo_ordre o, geo_entrep e , geo_client c
    where o.cen_ref = e.cen_ref and
            o.cli_ref = c.cli_ref and
            o.ord_ref = arg_ord_ref;
    If ls_tvr_code_client <> ls_tvr_code_entrep Then
        ls_rc := ls_rc || '(P)  TVA client différente de celle entrepôt' || ls_crlf;
    End If;
    f_calcul_regime_tva(arg_ord_ref, ls_tvr_code_entrep, ls_tvr_code, ls_rc_tvr);
    --msg := ls_tvr_code; return;
    if (ls_tvr_code is not null) then
        f_set_regime_tva(arg_ord_ref, ls_tvr_code, ll_res, msg);
    else
        ls_rc := ls_rc || '(P) %%% ' || ls_rc_tvr || ls_crlf;
        ll_res := 0;
    end if;
    if ll_res <> 1 then
        ls_rc := ls_rc || '(A) %%% Erreur mise à jour du régime TVA. Contacter l''informatique' || ls_crlf;
    end if;
    -- Controle palette et palette intermediaire
    open C_typpal(arg_ord_ref);
    LOOP
        fetch C_typpal into ls_laf_no,li_nb_pal, ls_pal_code,ls_pal_gestcode, ls_palinter_code,ls_palinter_gestcode,li_nb_palinter;
        EXIT WHEN C_typpal%notfound;
        If (li_nb_pal > 0 and li_nb_palinter > 0 and ls_pal_gestcode ='CHEP' and (ls_palinter_gestcode <> 'CHEP' or ls_palinter_gestcode is null) )  or  ((ls_pal_gestcode <>'CHEP' or ls_pal_gestcode is null) and ls_palinter_gestcode =  'CHEP' and li_nb_palinter > 0) Then
            ls_rc := ls_rc || '(A) Ligne=' || ls_laf_no || ' les palettes au sol et palettes intermédiaires ne sont pas tous du type CHEP' || ls_crlf;
            return;
        End If;
    END LOOP;
    CLOSE C_typpal;
    -- BAM le 29/08/2016
    -- A la demande de A.Vialaret
    -- Contrôler le montant au kilo du transport
    f_calcul_cout_transport(arg_ord_ref, ls_sqlerr, ld_cc_trans);
    if ld_cc_trans > 1 then
        /* Bruno le 07/09/21*/
        /* Libérer ADV  */
        /*If (gs_user.geo_facture = 'O'  OR (ll_nb_ligne_ordre = ll_nb_ligne_gratuit))  Then  */
        ls_rc := ls_rc || '(P) le coût prévisionnel du transport est supérieur à 1 ' || ls_dev_code || ' au kilo' || ls_crlf;
    ELSE
        If (ls_sqlerr is not null) Then
            ls_rc := ls_rc || '(A) %%% ' || ls_sqlerr || ls_crlf;
        End If;
    end if;
    -- DEB LLEF
    -- Enlever les apostrophes dans la référence client qui cause des soucis à la facturation
    if instr(ls_ref_cli, '''') <> 0 then
        ls_ref_cli  := replace(ls_ref_cli, '''', ' ');
        UPDATE GEO_ORDRE SET REF_CLI = ls_ref_cli WHERE ORD_REF = arg_ord_ref;
        ls_rc := ls_rc || '(A) %%% Attention la référence client comporte un apostrophe ! ' || ls_crlf;
    end if;
    -- On vérifie que tous les ordres d'un même client avec deux entrepôts différents ont un numéro de commande différent
    -- cas particulier de SOCOMOEUROP, LIDL, ect... qui livre avec le même numéro de cmd deux entrepôts. Donc on tient pas compte de l'entrepôt
    if ls_ident_ref_cli = 'N' and ls_ref_cli is not null then  -- Le client a un n° de cde par entrepôt
        open C_CONTROLE (arg_ord_ref, ls_cli_ref);
        LOOP
            fetch C_CONTROLE into  ls_find_ref_cli, ls_find_cen_ref, ls_nordre;
            EXIT WHEN C_CONTROLE%notfound;
            ls_find_ref_cli := replace(ls_find_ref_cli, '''', ' ');
            if ls_find_cen_ref <> ls_cen_ref and ls_ref_cli = ls_find_ref_cli then
                ls_rc := ls_rc || '(A) %%% Même n° de commande pour des entrepôts différents Ordre: ' || ls_nordre || ls_crlf;
            end if;
        END LOOP;
        CLOSE C_CONTROLE;
    END IF;
    -- FIN LLEF
    -- VERIFICATION SI FACTURATION PAR EDI ALORS UNE REFERENCE CLIENT EST OBLIGATOIRE
    begin
        select ind_bloc_factu_edi into ls_bloc_factu_edi from geo_entrep where cen_ref = ls_cen_ref;
    exception when no_data_found then
        null;
    end;
    if ls_bloc_factu_edi = 'O' and (ls_ref_cli = '' or ls_ref_cli is null) then
        ls_rc := ls_rc || '(A) %%% Facturation en EDI mais aucune référence client ' || ls_crlf;
    end if;
    res := 1;
    msg := ls_rc;
    IF msg IS null or msg = ''
    THEN msg := 'OK';
    END IF;
END;
/

