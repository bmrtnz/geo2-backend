-- on_change_fou_code

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_FOU_CODE" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_user GEO_USER.nom_utilisateur%type,
    arg_soc_code GEO_SOCIETE.soc_code%type,
	res out number,
    msg out varchar2
) AS
    ls_fou_code varchar2(50);
    ls_soc_dev_code varchar2(50);
    ls_cen_ref varchar2(50);
    ls_propr_code varchar2(50);
    ls_ord_ref varchar2(50);
    ls_sco_code varchar2(50);
    ls_flag_exped_fournni varchar2(50);
    ls_visible_reparcam varchar2(50) := '0';
    ls_typ_ordre varchar2(50);
    ls_vte_bta varchar2(50);
    ls_ach_bta varchar2(50);
    ls_fou_code_old varchar2(50);
    ls_ind_modif_detail varchar2(50);
    cursor cur_ols is
        select
            orl_ref,
            fou_code,
            pal_nb_col,
            demipal_ind,
            pal_nb_palinter,
            cde_nb_col,
            cde_nb_pal
        from geo_ordlig
        where ord_ref = ls_ord_ref;
begin

    msg := '';
    res := 0;

    select ol.fou_code, ol.propr_code, ol.ord_ref, o.sco_code, o.typ_ordre, ol.vte_bta_code, ol.ach_bta_code, o.cen_ref
    into ls_fou_code, ls_propr_code, ls_ord_ref, ls_sco_code, ls_typ_ordre, ls_vte_bta, ls_ach_bta, ls_cen_ref
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    where orl_ref = arg_orl_ref;

    begin
        select flag_exped_fournni
        into ls_flag_exped_fournni
        from GEO_ORDLOG
        where   ORD_REF = ls_ord_ref and FOU_CODE = ls_fou_code;
    exception when others then
        ls_flag_exped_fournni := 'N';
    end;

    select dev_code
    into ls_soc_dev_code
    from GEO_SOCIETE
    where soc_code = arg_soc_code;

    If ls_flag_exped_fournni ='O' Then
        msg := 'Erreur: Attention la station a clôturé le détail, pas possible d''ajouter un détail';
        res := 1;
        update geo_ordlig
        set fou_code = null
        where orl_ref = arg_orl_ref;
        commit;
        return;
    --deb llef
    --Effacer les infos du détail d'expedition lors du changement de fournisseur
    else
        update geo_ordlig
        set
            exp_nb_pal = 0,
            exp_nb_col = 0,
            exp_pds_brut = 0,
            exp_pds_net = 0,
            ach_qte = 0,
            vte_qte = 0
        where orl_ref = arg_orl_ref;
        commit;

        declare
            cursor CT is
                select ref_traca  from geo_traca_ligne where orl_ref = arg_orl_ref;
        begin
            for r in CT
            loop
                begin
                    delete from geo_traca_ligne where orl_ref = arg_orl_ref and ref_traca_ligne = r.ref_traca;
                exception when others then
                    msg := 'Erreur: erreur de suppression de geo_traca_ligne orl_ref: ' || arg_orl_ref;
                    return;
                end;
                begin
                    delete from GEO_TRACA_DETAIL_PAL where ref_traca = r.ref_traca;
                exception when others then
                    msg := 'Erreur: erreur de suppression de geo_traca_detail_pal ref_traca: ' || r.ref_traca;
                    return;
                end;
                commit;
            end loop;
        end;

    --fin llef
    End If;

    -- if ls_fou_code is null then

        If ls_propr_code is null or ls_propr_code = '' Then
            update geo_ordlig
            set propr_code = fou_code
            where orl_ref = arg_orl_ref;
            commit;
        End IF;

        declare
            cursor cur_fns is
                SELECT fou_code
                FROM GEO_FOURNI
                where FOU_CODE =ls_fou_code and IND_REPAR_CAMION= 'O';
        begin
            for r in cur_fns
            loop
                If r.fou_code is not null Then
                    ls_visible_reparcam := 1;
                    exit;
                end if;
            end loop;
        end;

        if ls_sco_code = 'F' then
            If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then
                of_repartition_palette(arg_orl_ref, ls_sco_code, arg_user, res, msg);
                -- On met aussi à jour les lignes avec l'ancien expéditeur
                for r in cur_ols
                loop
                    If  r.fou_code = ls_fou_code Then
                        of_repartition_palette(arg_orl_ref, ls_sco_code, arg_user, res, msg);
                    end if;
                end loop;
            End If;
        end if;

    -- end if;

    -- SELECT ind_modif_detail  INTO ls_ind_modif_detail
    -- FROM GEO_FOURNI
    -- where FOU_CODE =ls_fou_code;

    --DEBUT LLEF AUTOM. IND. TRANSP. + tri et emballage retrait
    if arg_soc_code ='UDC' and ls_sco_code = 'RET' then
        for r in cur_ols
        loop
            update geo_ordlig
            set fou_code = ls_fou_code
            where orl_ref = r.orl_ref;
            commit;
        end loop;
        of_sauve_ordre(ls_ord_ref, res, msg);
    end if;
    --FIN LLEF

    --DEB TRANSPORT PAR DEFAUT
    --Vérification que le bassin de la station est en phase avec le bassin du transport par défaut souhaité par l'entrepôt
    --Table GEO_ENT_TRP_BASSIN
    declare
        ls_bac_code varchar2(50);
        is_bassin varchar2(50);
        ls_trp_code varchar2(50);
        ls_trp_bta_code varchar2(50);
        ls_trp_dev_code varchar2(50);
        ld_trp_dev_pu number;
        ld_dev_tx number;
        ld_trp_pu number;
    begin
        select bac_code into ls_bac_code from geo_ordlig where orl_ref = arg_orl_ref;
        If arg_soc_code <> 'BUK' and  ls_typ_ordre <>'RGP' Then

            if  is_bassin is null or is_bassin = '' then

                begin
                    select trp_code, trp_bta_code, trp_dev_code, trp_pu, dev_tx
                    into ls_trp_code, ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_pu, ld_dev_tx
                    from 	geo_ent_trp_bassin,
                            geo_devise_ref
                    where 	cen_ref = ls_cen_ref and
                                bac_code = ls_bac_code and
                                trp_dev_code = dev_code and
                                dev_code_ref = ls_soc_dev_code;
                exception when others then
                    msg := msg || ' Pas de transporteur par défaut pour ce bassin/entrepôt';
                end;
                ld_trp_pu := ld_dev_tx * ld_trp_dev_pu;

                update geo_ordre
                set
                    trp_pu = ld_trp_pu,
                    trp_code = ls_trp_code,
                    trp_bta_code = ls_trp_bta_code,
                    trp_dev_code = ls_trp_dev_code,
                    trp_dev_pu = ld_trp_dev_pu,
                    trp_dev_taux = ld_dev_tx,
                    trp_bac_code = ls_bac_code
                where ord_ref = ls_ord_ref;
                commit;
                is_bassin := ls_bac_code;

            end if;
        End  If;
    exception when others then
        msg := 'Impossible d''assigner un transporteur par défaut ' || SQLERRM;
        return;
    end;
    --FIN TRANSPORT PAR DEFAUT

    msg := 'OK';
    res := 1;
    return;

end;
