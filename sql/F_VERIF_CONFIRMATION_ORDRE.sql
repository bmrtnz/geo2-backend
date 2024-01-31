CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_VERIF_CONFIRMATION_ORDRE (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    is_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_cam_code GEO_ORDRE.CAM_CODE%TYPE;
    ls_cam_code_old GEO_SOCIETE.CAM_CODE_OLD%TYPE;
    ls_ord_ref GEO_ORDRE.ORD_REF%TYPE;
    ldt_depdatp GEO_ORDRE.DEPDATP%TYPE;
    ldt_livdatp GEO_ORDRE.LIVDATP%TYPE;
    ll_cde_nb_pal number;
    ll_exp_nb_pal number;
    li_nb_station_pas_propr number;
    ls_sco_code GEO_ORDRE.SCO_CODE%TYPE;
    ll_nb_nopal number;
    lb_pal_zero boolean := false;
    lb_datneg boolean := false;
    lb_dateliv boolean := false;
    lb_datneg_autorise boolean := false;
    lb_taux_achat_ko boolean:= false;
    li_nb_ok number;
    li_nb_lig_erreur number;
    lb_pluscolismoinspalet boolean := False;
    lb_pasdeprixachat boolean := false;
    lb_pasdeprixvente boolean := false;
    lb_pasdebta boolean := false;
    lb_pasdeUC boolean := false;
    lb_charge_camion boolean := false; -- true si poids brut commande > poids max autorisé du transport
    ls_art_ref geo_ordlig.art_ref%TYPE;
    ls_unit_vte geo_ordlig.vte_bta_code%TYPE;
    ls_gem_code GEO_ARTICLE_COLIS.GEM_CODE%TYPE;
    ls_gem_desc GEO_ARTICLE_COLIS.GEM_DESC%TYPE;
    lb_code_emb_diff boolean := false;
    ll_num_edi GEO_ORDRE.REF_EDI_ORDRE%TYPE;
    ll_nb_col_edi number;
    ls_gtin GEO_EDI_LIGNE.EAN_PROD_CLIENT%TYPE;
	ls_art_client GEO_EDI_LIGNE.CODE_INTERNE_PROD_CLIENT%TYPE;
    ll_nb_col number;
    ld_ord_edi boolean := false;
    ls_mod_cult geo_mode_culture.LIBELLE%TYPE;
    ls_propr_code geo_ordlig.PROPR_CODE%TYPE;
    ls_tiers geo_fourni.K_FOU%TYPE;
    ll_certif geo_certifs_tiers.CERTIF%TYPE;
    ls_list_certifs geo_ordlig.LIST_CERTIFS%type;
    ls_fou_code geo_ordlig.fou_code%type;
    ls_fou_code_old geo_ordlig.fou_code%type := 'tatayoyo';
    ls_orl_ref geo_ordlig.orl_ref%type;
    ls_cli_ref geo_ordre.cli_ref%type;
    ls_exist varchar2(10);
    ld_exist number; -- declare only because mandatory
    array_list_certifs_ligne p_str_tab_type;
    ll_certif_lig number;
    ls_certif_lib geo_certif_modcult.CERT_MENT_LEG%type;
    lb_certif_stat boolean := false;
    ls_user_name GEO_USER.NOM_UTILISATEUR%TYPE;
    lb_non_cloture boolean := false;
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ls_per_codeass GEO_ORDRE.PER_CODEASS%TYPE;
    ls_per_codecom GEO_ORDRE.PER_CODECOM%TYPE;
    ls_percode GEO_PERSON.PER_CODE%TYPE;
    ls_mess_delai CLOB := '';
    ls_mess CLOB := '';
    ls_mess_ko CLOB := 'Le(s) point(s) suivant(s) gêne/gênent la confirmation de commande:~r';
    ls_mess_ok CLOB := 'Le(s) point(s) suivant(s) peut/peuvent gêner la confirmation de commande:~r';
    lb_marge_negative boolean := false;
    ldc_marge_previ number;
    ls_user_facture GEO_USER.geo_facture%TYPE;
	ll_ref_edi_ligne GEO_EDI_LIGNE.ref_edi_ligne%TYPE;

    cursor C_ART_ORD (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select art_ref, vte_bta_code from geo_ordlig where ord_ref = ref_ordre;
		
    CURSOR cur_lig_edi (num_edi GEO_ORDRE.REF_EDI_ORDRE%TYPE)
    IS
        SELECT  REF_EDI_LIGNE, QUANTITE_COLIS
        FROM GEO_EDI_LIGNE EL
        WHERE REF_EDI_ORDRE = num_edi;
		
    CURSOR C_coh_art_fou (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select OL.ART_REF, C.LIBELLE, OL.PROPR_CODE, F.K_FOU
        from geo_ordre O, geo_ordlig OL, geo_article A, geo_fourni F, geo_mode_culture C
        where O.ORD_REF  = ref_ordre AND
                OL.ORD_REF = O.ORD_REF AND
                A.ART_REF = OL.ART_REF AND
                F.FOU_CODE = OL.PROPR_CODE AND
                F.FOU_CODE <> '-' AND
                A.MODE_CULTURE = C.REF AND
                A.MODE_CULTURE IN (1,2,10,14);
    CURSOR CT_LIG (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select L.list_certifs, L.fou_code, L.orl_ref, L.art_ref, L.propr_code, O.cli_ref
        from geo_ordlig L, geo_ordre O
        where L.ord_ref = ref_ordre
          and L.ord_ref = O.ord_ref;
    CURSOR cur_delai_litige (ref_ordre GEO_ORDRE.ORD_REF%type, ref_cam GEO_ORDRE.CAM_CODE%TYPE, ref_cam_old GEO_ORDRE.CAM_CODE%TYPE)
    IS
        select distinct  LL.TIE_CODE,O.NORDRE  , O.PER_CODEASS,O.PER_CODECOM
        from   GEO_LITIGE L , GEO_LITLIG LL, GEO_ORDRE O ,GEO_ORDLIG OL, GEO_FOURNI F
        where  OL.ORD_REF = ref_ordre             and
                OL.FOU_CODE = LL.TIE_CODE         and
                OL.FOU_CODE = F.FOU_CODE         and
                F.PAY_CODE ='FR'                         and
            (O.CAM_CODE = ref_cam         or
             O.CAM_CODE = ref_cam_old)         and
                L.FL_FOURNI_CLOS ='N'                 and
                L.LIT_DATE_CREATION < sysdate -40 and
                L.LIT_REF =LL.LIT_REF                 and
                LL.LCQ_CODE ='F'                         and
                LL.TYT_CODE ='F'                         and
                O.ORD_REF = L.ORD_REF_ORIGINE;
BEGIN
    -- correspond à f_verif_confirmation_ordre.pbl
    res := 0;
    msg := '';

    -- Confirmé par bruno le 29/04/22
    select CAM_CODE, CAM_CODE_OLD into ls_cam_code, ls_cam_code_old FROM GEO_SOCIETE where soc_code = is_soc_code;

    SELECT O.ORD_REF,O.DEPDATP,O.LIVDATP, sum(CDE_NB_PAL),sum(EXP_NB_PAL)
    INTO ls_ord_ref, ldt_depdatp, ldt_livdatp, ll_cde_nb_pal, ll_exp_nb_pal
    FROM GEO_ORDRE O
    LEFT OUTER JOIN GEO_ORDLIG L on O.ORD_REF = L.ORD_REF
    WHERE O.ORD_REF = is_ord_ref
    group by O.ORD_REF,O.DEPDATP,O.LIVDATP;

    select count(*) into li_nb_station_pas_propr
    from GEO_ORDLIG L, GEO_FOURNI F
    where L.ORD_REF = is_ord_ref and
            L.PROPR_CODE =  F.FOU_CODE  and
            F.IND_EXP = 'F';

    select SCO_CODE into ls_sco_code
    from GEO_ORDRE
    where ORD_REF = is_ord_ref;

    If li_nb_station_pas_propr >0 Then
        msg := msg || '* Il existe ' || to_char(li_nb_station_pas_propr) || ' station(s) non proprietaire(s)~r';
    End If;

    If ll_exp_nb_pal = 0 Then
        ll_exp_nb_pal := ll_cde_nb_pal;
    end if;

    If  ll_exp_nb_pal = 0 Then
        begin
            SELECT O.ORD_REF,count(*) into ls_ord_ref, ll_nb_nopal
            FROM GEO_ORDRE O, GEO_ORDLIG L
            where O.ORD_REF = L.ORD_REF         and
                    L.PAL_CODE in('-','DEPAL')         and
                    O.ORD_REF = is_ord_ref
            group by O.ORD_REF;
        exception when no_data_found then
            ll_nb_nopal := 0;
        end;

        If ll_nb_nopal = 0 Then
            lb_pal_zero := True;
            msg := msg || '* ll n''y a aucune palette~r';
        End If;
    End If;

    If trunc(ldt_depdatp) - trunc(SYSDATE) < 0 Then
        msg := msg || '* La date d''expédition est inférieure à la date jour~r';
        lb_datneg := True;
    End If;

    If trunc(ldt_livdatp) - trunc(ldt_depdatp) < 0 Then
        msg := msg || '* La date de livraison est inférieure à la date d''expédition ~r';
        lb_dateliv := True;
    End If;

    If lb_datneg = True Then
        If is_soc_code = 'UDC' then
            lb_datneg := False;
            lb_datneg_autorise := True;
        Else
            select count(*) into li_nb_ok
            from GEO_ORDLIG L, GEO_FOURNI F
            where    L.ORD_REF = is_ord_ref and
                    L.FOU_CODE = F.FOU_CODE and
                    F.IND_MODIF_DETAIL ='O';
            If li_nb_ok > 0 Then
                    lb_datneg := False;
                    lb_datneg_autorise := True;
            End IF;
        End if;
    End If;

    select count(*)  INTO li_nb_lig_erreur
    from GEO_ORDLIG, GEO_ORDRE
    where  GEO_ORDRE.ORD_REF = is_ord_ref                 and
            GEO_ORDRE.ORD_REF  = GEO_ORDLIG.ORD_REF             and
            GEO_ORDLIG.CDE_NB_PAL > GEO_ORDLIG.CDE_NB_COL and
            GEO_ORDLIG.CDE_NB_PAL > 0 and
            GEO_ORDLIG.CDE_NB_COL > 0;

    If li_nb_lig_erreur >  0 Then
        msg := 'll y a plus de palettes que de colis sur au moins une ligne de commande.';
        lb_pluscolismoinspalet := True;
    End If;

    select count(*)  INTO li_nb_lig_erreur
    from GEO_ORDLIG, GEO_ORDRE
    where  GEO_ORDRE.ORD_REF = is_ord_ref                 and
            GEO_ORDRE.ORD_REF  = GEO_ORDLIG.ORD_REF             and
        (GEO_ORDLIG.ACH_PU IS NULL or  GEO_ORDLIG.ACH_PU =0 ) and
            GEO_ORDRE.VENTE_COMMISSION <> 'O' and
        not exists (select 1
                    from  GEO_CLIENT , GEO_ARTICLE_COLIS
                    where  GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF and
                            GEO_ORDLIG.ART_REF =  GEO_ARTICLE_COLIS.ART_REF and
                            GEO_ARTICLE_COLIS.ESP_CODE = 'EMBALL' AND
                            GEO_ARTICLE_COLIS.VAR_CODE = 'PALLOX' and
                            GEO_CLIENT.IND_PALOX_GRATUIT ='O'     ) and
            GEO_ORDRE.CEN_CODE  not like 'PREORDRE%' and
        not exists (select 1
                    from  GEO_ARTICLE_COLIS
                    where  GEO_ORDLIG.ART_REF =  GEO_ARTICLE_COLIS.ART_REF and
                            GEO_ARTICLE_COLIS.ESP_CODE = 'EMBALL' AND
                            GEO_ARTICLE_COLIS.COL_CODE = 'PLV');

    If li_nb_lig_erreur >  0 Then
        msg := msg || '* ll y a des lignes avec un prix d''achat nulle ou zéro~r';
        lb_pasdeprixachat := True;
    End If;

    select count(*)  INTO li_nb_lig_erreur
    from GEO_ORDLIG, GEO_ORDRE
    where  GEO_ORDRE.ORD_REF = is_ord_ref                 and
            GEO_ORDRE.ORD_REF  = GEO_ORDLIG.ORD_REF             and
        (GEO_ORDLIG.VTE_PU IS NULL or ( GEO_ORDLIG.VTE_PU =0 and GEO_ORDLIG.IND_GRATUIT <>'O')) and
            GEO_ORDRE.VENTE_COMMISSION <> 'O' and
        not exists (select 1
                    from  GEO_CLIENT , GEO_ARTICLE_COLIS
                    where  GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF and
                            GEO_ORDLIG.ART_REF =  GEO_ARTICLE_COLIS.ART_REF and
                            GEO_ARTICLE_COLIS.ESP_CODE = 'EMBALL' AND
                            GEO_ARTICLE_COLIS.VAR_CODE = 'PALLOX' and
                            GEO_CLIENT.IND_PALOX_GRATUIT ='O'     )and
            GEO_ORDRE.CEN_CODE  not like 'PREORDRE%';

    If li_nb_lig_erreur >  0 Then
        msg := msg || '* ll y a des lignes avec un prix de vente nulle ou zéro~r';
        lb_pasdeprixvente := True;
    End If;

    select count(*)  INTO li_nb_lig_erreur
    from GEO_ORDLIG ,GEO_ORDRE
    where  GEO_ORDRE.ORD_REF = is_ord_ref    and
            GEO_ORDRE.ORD_REF = GEO_ORDLIG.ORD_REF and
        (GEO_ORDLIG.ACH_BTA_CODE IS NULL OR GEO_ORDLIG.ACH_BTA_CODE ='' OR GEO_ORDLIG.VTE_BTA_CODE IS NULL OR GEO_ORDLIG.VTE_BTA_CODE ='') and
            GEO_ORDRE.CEN_CODE  not like 'PREORDRE%';

    If li_nb_lig_erreur >  0 Then
        msg := msg || '* ll y a des lignes sans type d''unité~r';
        lb_pasdebta := True;
    End If;

    select count(*)  INTO li_nb_lig_erreur
    from  GEO_ORDRE  O,GEO_ORDLIG OL , GEO_ARTICLE A
    where O.ORD_REF = is_ord_ref                 and
            O.ORD_REF = OL.ORD_REF                     and
        (OL.ACH_BTA_CODE  ='PIECE' OR OL.VTE_BTA_CODE  ='PIECE')  and
            OL.ART_REF = A.ART_REF and
            A.U_PAR_COLIS= 0;

    If li_nb_lig_erreur >  0 Then
        msg := msg || '* ll n''y a d UC et le type de vente est à la pièce~r';
        lb_pasdeUC := True;
    End If;

    select count(*) into li_nb_lig_erreur
    from GEO_ORDRE O, GEO_ORDLIG L, GEO_SOCIETE S
    where      O.ORD_REF = is_ord_ref                     and
            O.SOC_CODE = S.SOC_CODE                 and
            O.ORD_REF = L.ORD_REF                     and
            L.ACH_DEV_CODE =S.DEV_CODE                 and
            L.ACH_DEV_TAUX  <> 1;

    If li_nb_lig_erreur >  0 Then
        msg := msg || '* un taux achat est incorrect, contacter le service informatique';
        lb_taux_achat_ko := True;
    End If;



    -- Deb llef
    -- vérification si l'unité de vente de l'article de l'ordre est différente du groupe d'emballage dans le cas du SACHET et de la BARQUETTE uniquement
    open C_ART_ORD (is_ord_ref);
    LOOP
        fetch C_ART_ORD into ls_art_ref, ls_unit_vte;
        EXIT WHEN C_ART_ORD%NOTFOUND;
        begin
            select GEM_CODE, GEM_DESC into ls_gem_code, ls_gem_desc from geo_article_colis where art_ref = ls_art_ref;
             exception when no_data_found then  ls_gem_code:=null;
          end;
        if (ls_unit_vte <> 'SACHET' and ls_gem_code = 'UCSAC') or (ls_unit_vte <> 'BARQUE' and ls_gem_code = 'UCBARQ') then
                lb_code_emb_diff := True;
                msg := msg || '~r * Art: ' || ls_art_ref || ' code emballage: "' || ls_gem_desc || '" unité de vente: "' || ls_unit_vte || '" ~r~n';
        end if;
    end loop;
    close C_ART_ORD;
    -- fin llef

    -- LLEF: Vérification si les qtés commandées correspondent bien à celles de l'ordre EDI
    select REF_EDI_ORDRE into ll_num_edi FROM GEO_ORDRE WHERE ORD_REF = is_ord_ref;
    if ll_num_edi is not null then
        open cur_lig_edi (ll_num_edi);
        LOOP
            fetch cur_lig_edi into ll_ref_edi_ligne, ll_nb_col_edi;
            exit when cur_lig_edi%notfound;

            SELECT SUM(CDE_NB_COL) into ll_nb_col
            FROM  GEO_ORDLIG L, GEO_ORDRE O
            WHERE O.REF_EDI_ORDRE = ll_num_edi
              AND O.ORD_REF = L.ORD_REF
              AND L.REF_EDI_LIGNE = ll_ref_edi_ligne;

            if ll_nb_col_edi <> ll_nb_col and ll_nb_col is not null then
                msg := msg || ' * La qté pour le GTIN:' || ls_gtin || ' / ART_CLIENT: ' || ls_art_client || ' est différente dans l''EDI ' || '~r~n';
                ld_ord_edi := True;
            elsif ll_nb_col is null then
                msg := msg || ' * Le GTIN:' || ls_gtin || ' / ART_CLIENT:' || ls_art_client || ' est absent de l''ordre' || '~r~n';
                ld_ord_edi := True;
            end if;
        end loop;
        close cur_lig_edi;
    end if;
    -- FIN LLEF

    -- LLEF: Vérification si le fournisseur est bien certifié BIO pour des articles avec le mode de culture BIO
    OPEN C_coh_art_fou(is_ord_ref);
    LOOP
        FETCH C_coh_art_fou INTO ls_art_ref, ls_mod_cult, ls_propr_code, ls_tiers;
        EXIT WHEN C_coh_art_fou%notfound;

        begin
            select certif into ll_certif
            from geo_certifs_tiers
            where tiers = ls_tiers and certif = 12 and typ_tiers = 'F';
        exception when no_data_found then
            msg := msg || ' * l''article ' || ls_art_ref || ' avec le mode de culture (' || ls_mod_cult || ') ne peut provenir du propriétaire  (' || ls_propr_code || ') n''étant pas certifié BIO ~r';
        end;
    end loop;
    CLOSE C_coh_art_fou;

    /*
    LLEF 24/11/2020
    vérifie adéquation entre les certifs de la station et celles demandés par le client
    */
    open CT_LIG (is_ord_ref);
    LOOP
        fetch CT_LIG into ls_list_certifs, ls_fou_code, ls_orl_ref, ls_art_ref, ls_propr_code, ls_cli_ref;
        EXIT WHEN CT_LIG%notfound;

        if ls_list_certifs is not null then
            f_exist_certif(ls_cli_ref, ls_orl_ref, ld_exist, ls_exist);

            if ls_exist = 'OUI' and ls_list_certifs <> '0' then
                f_split(ls_list_certifs, ',', array_list_certifs_ligne);
                for i in 1..array_list_certifs_ligne.COUNT LOOP
                    ll_certif_lig := to_number(array_list_certifs_ligne(i));

                    begin
                        select distinct C.CERT_MENT_LEG into ls_certif_lib
                        from geo_certifs_tiers T, geo_fourni F, geo_certif_modcult C
                        where F.fou_code = ls_fou_code  and F.VALIDE = 'O'
                          and F.K_FOU = T.TIERS and T.TYP_TIERS = 'F'
                          and  T.DATE_VALIDITE >= SYSDATE
                          and   T.CERTIF = ll_certif_lig
                          and   C.K_CERTIF = T.CERTIF
                          and   C.TYPE_CERT = 'CERTIF';
                    exception when no_data_found then null;
                        begin
                            select C.CERT_MENT_LEG into ls_certif_lib from geo_certif_modcult C where k_certif = ll_certif_lig and type_cert = 'CERTIF';

                            msg := msg || ' * l''article ' || ls_art_ref || ' avec la certification: ' || ls_certif_lib || ' ne peut provenir de la station: ' || ls_fou_code || ' n''étant pas certificié ' || ls_certif_lib || '~r';
                            lb_certif_stat := TRUE;
                        exception when no_data_found then null;
                        end;
                    end;
                end loop;
            end if;
        end if;
    end loop;
    close CT_LIG;
    -- FIN LLEF

    begin
        ls_user_name := upper(is_utilisateur);
        select PER_CODE into ls_percode
        from GEO_PERSON P
        where P.PER_USERNAME = ls_user_name;
    exception when no_data_found then
        ls_percode := '';
    end;

    open cur_delai_litige (is_ord_ref, ls_cam_code, ls_cam_code_old);
    LOOP
        fetch  cur_delai_litige into ls_fou_code, ls_nordre, ls_per_codeass, ls_per_codecom;
        exit when cur_delai_litige%notfound;

        If (ls_percode = ls_per_codeass or ls_percode = ls_per_codecom) Then
            lb_non_cloture := TRUE;
            If ls_fou_code_old <> ls_fou_code then
                If ls_fou_code_old <> 'tatayoyo' Then
                    ls_mess_delai := ls_mess_delai || ')' || '~r~n';
                End If;
                ls_mess_delai := ls_mess_delai || '*' || ls_fou_code || ' a vos litiges dont le délai est dépassé (ordre: ' || ls_nordre;

                ls_fou_code_old := ls_fou_code;
            Else
                ls_mess_delai := ls_mess_delai || ', ' || ls_nordre;
            End If;
        end if;
    end loop;
    close cur_delai_litige;

    If lb_non_cloture = TRUE Then
        ls_mess_delai := ls_mess_delai || ')~r~n';
        ls_mess_ok := ls_mess_ok || ls_mess_delai;
    end if;

    If is_soc_code = 'SA' and ls_sco_code = 'F' Then
        declare
          tmp_msg varchar2(200) := '';
        begin
            f_calcul_marge_previ(is_ord_ref, is_soc_code, ldc_marge_previ, res, tmp_msg);
            msg := msg || tmp_msg;
        end;
        If ldc_marge_previ < 0 Then
            lb_marge_negative := TRUE;
            msg := msg || ' * La marge est négative :' || to_char(ldc_marge_previ) || '~r~n';
        End If;
    End If;

    -- Check si poids total de la commande est supérieur à la charge maximale du camion
    DECLARE
        poids_brut_total_max number := 0;
        poids_brut_total number := 0;

        CURSOR curs IS
        SELECT A.col_pdnet, P.poids, L.orl_ref, L.cde_nb_col, L.cde_nb_pal, C.col_tare, O.ttr_code, O.trp_bta_code
        FROM GEO_ORDLIG L, GEO_ORDRE O, GEO_COLIS C, GEO_ARTICLE A, GEO_PALETT P
        WHERE O.ord_ref = is_ord_ref
            and  O.ord_ref = L.ord_ref
            and L.pal_code = P.pal_code
            and L.art_ref = A.art_ref
            and A.col_code = C.col_code
            and A.esp_code = C.esp_code;

        BEGIN
            -- Récupère le poids max du type de transport
            SELECT CC.poids_max INTO poids_brut_total_max
            FROM GEO_CHARGE_CAMION CC, GEO_ORDRE O
            WHERE O.ord_ref = is_ord_ref
                and CC.sco_code = O.sco_code
                and CC.ttr_code = O.ttr_code;

            FOR i IN curs LOOP
                -- Cas pas de palette
                IF i.poids is NULL THEN
                    i.poids := 0;
                END IF;

                -- Si type transport = 'BENNE'
                IF i.ttr_code = 'BE' and i.trp_bta_code = 'TONNE' THEN
                    poids_brut_total := poids_brut_total + (1000 * i.cde_nb_col);
                ELSIF i.ttr_code = 'BE' and i.trp_bta_code = 'KILO' THEN
                    poids_brut_total := poids_brut_total + i.cde_nb_col;
                ELSE
                    poids_brut_total := poids_brut_total + round((i.cde_nb_col * (i.col_tare + i.col_pdnet) + (i.cde_nb_pal * i.poids)));
                END IF;
            END LOOP;

            IF poids_brut_total > poids_brut_total_max THEN
                msg := msg || ' Le poids brut de la commande (' || poids_brut_total || ' KILO) dépasse la charge maximale du transport (' || poids_brut_total_max || ' KILO) ! ~r';
                lb_charge_camion := True;
            END IF;

            EXCEPTION WHEN no_data_found THEN
            NULL;
    END;
    BEGIN
    select geo_facture into ls_user_facture from geo_user where NOM_UTILISATEUR = is_utilisateur;
     EXCEPTION WHEN no_data_found THEN
            NULL;
    end;
    If (
        (is_soc_code <> 'BUK' AND lb_pasdeprixachat = True) or
        lb_pasdeprixvente = True or lb_pasdebta = True or lb_pluscolismoinspalet = True or lb_pasdeUC = True or
        lb_dateliv = True or li_nb_station_pas_propr > 0 OR lb_taux_achat_ko= True
        ) and ls_user_facture <> 'O'
    Then
        msg := ls_mess_ko || msg || '~rConfirmation annulée';
        res := 0;
        return;
    Else
        If lb_pasdeprixachat = True or lb_pasdeprixvente = True or lb_pasdebta = True or lb_pluscolismoinspalet = True or
           lb_pasdeUC = True or lb_dateliv= True or li_nb_station_pas_propr > 0  or ld_ord_edi = True or lb_pal_zero = True or
           lb_datneg_autorise = True or lb_datneg = True or lb_code_emb_diff = True  or lb_non_cloture = True or lb_certif_stat = True or
           lb_charge_camion = True
        Then
            msg := ls_mess_ok || msg || '~rVoulez-vous continuer ?~r';
            res := 2;
            return;
        End If;
    end if;

    res := 1;
end F_VERIF_CONFIRMATION_ORDRE;
/
