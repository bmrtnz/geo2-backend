-- on_change_propr_code

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_PROPR_CODE" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_user GEO_USER.nom_utilisateur%type,
    arg_soc_code GEO_SOCIETE.soc_code%type,
	res out number,
    msg out varchar2
) AS
    ls_val varchar2(50);
    ls_ind_exp varchar2(50);
    ls_list_fourni_sql varchar2(50);
    ls_fou_code varchar2(50);
    ls_flag_exped_fournni varchar2(50);
    ls_ord_ref varchar2(50);
    ls_soc_dev_code varchar2(50);
    ls_art_ref varchar2(50);
    ls_tvt_code varchar2(50);
    ls_sco_code varchar2(50);
    ld_prix_mini number;
    ls_typ_ordre varchar2(50);
    ls_vte_bta varchar2(50);
    ls_ach_bta varchar2(50);
    ls_fou_code_old varchar2(50);
    ls_cen_ref varchar2(50);
    cursor cur_ols is
        select
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

    select ol.propr_code, ol.ord_ref, ol.art_ref, o.tvt_code, o.sco_code, ol.vte_bta_code, ol.ach_bta_code, o.typ_ordre, ol.fou_code, o.cen_ref
    into ls_val, ls_ord_ref, ls_art_ref, ls_tvt_code, ls_sco_code, ls_vte_bta, ls_ach_bta, ls_typ_ordre, ls_fou_code_old, ls_cen_ref
    from geo_ordlig ol
    left join geo_ordre o on o.ord_ref = ol.ord_ref
    where orl_ref = arg_orl_ref;

    select dev_code
    into ls_soc_dev_code
    from geo_societe
    where soc_code = arg_soc_code;

    select  IND_EXP, LIST_EXP
    into ls_ind_exp, ls_list_fourni_sql
    from GEO_FOURNI
    where FOU_CODE = ls_val;

    If ls_ind_exp is null then
        ls_ind_exp := '';
    end if;

    if ls_ind_exp ='E' Then
        If ls_list_fourni_sql is not null then
            ls_fou_code := SUBSTR(ls_list_fourni_sql, 1, INSTR(ls_list_fourni_sql, ',') - 1);
        End If;
    Else
        ls_fou_code := ls_val;			
    End If;

    select flag_exped_fournni
    into ls_flag_exped_fournni
    from GEO_ORDLOG
    where ORD_REF = ls_ord_ref and FOU_CODE = ls_val;     

    declare
        cherche_fourni_res number;
        ls_ind_modif_detail varchar2(50);
        cursor CT2 is
            select ref_traca_ligne
            from geo_traca_ligne
            where orl_ref = arg_orl_ref;
    begin
        select rownum
        into cherche_fourni_res
        from geo_ordlig ol
        left join geo_fourni f on ol.fou_code = f.fou_code
        where orl_ref = arg_orl_ref
        and ol.fou_code = ls_fou_code
        order by ol.orl_lig;
        
        if cherche_fourni_res > 0 and ls_flag_exped_fournni <> 'O'  Then                                    
                        
            --deb llef
            --Effacer les infos du détail d'expedition lors du changement de fournisseur
            update geo_ordlig
            set
                fou_code = ls_fou_code,
                exp_nb_pal = 0,
                exp_nb_col = 0,
                exp_pds_brut = 0,
                exp_pds_net = 0,
                ach_qte = 0,
                vte_qte = 0
            where orl_ref = arg_orl_ref;
            commit;

            for r in CT2
            loop
                begin
                    delete from geo_traca_ligne where orl_ref = arg_orl_ref and ref_traca_ligne = r.ref_traca_ligne;
                exception when others then
                    res := 0;
                    msg := 'erreur de suppression de geo_traca_ligne orl_ref: ' || arg_orl_ref;
                    return;
                end;
                begin
                    delete from GEO_TRACA_DETAIL_PAL where ref_traca = r.ref_traca_ligne;
                exception when others then
                    res := 0;
                    msg := 'erreur de suppression de geo_traca_detail_pal ref_traca: ' || r.ref_traca_ligne;
                    return;
                end;
            end loop;

            --fin llef
            -- SELECT ind_modif_detail
            -- INTO ls_ind_modif_detail 
            -- FROM GEO_FOURNI
            -- where FOU_CODE = ls_fou_code;
            -- this.object.geo_fourni_ind_modif_detail[row]= ls_ind_modif_detail                      
                        
        ELSE
            update geo_ordlig
            set fou_code = null
            where orl_ref = arg_orl_ref;
            commit;
        End If;
    end;
                                    
    
    if ls_val <> ls_val or ls_val is null then

        declare
            ls_dev_code varchar2(50);
            ld_dev_taux number;
        begin
            select dev_code into ls_dev_code from geo_fourni where fou_code = ls_val;

            if ls_dev_code =ls_soc_dev_code   then 
                ld_dev_taux := 1.0;
            else
                select dev_tx_achat
                into ld_dev_taux
                from geo_devise_ref
                where dev_code = ls_dev_code
                and dev_code_ref =ls_soc_dev_code  ;
                if ld_dev_taux is null then
                    msg := 'le taux de cette devise n''est pas renseigné';
                    ls_dev_code := ls_soc_dev_code;
                    ld_dev_taux := 1.0;
                end if;
            end if;          
                
            --Vérification s'il existe un pu mini pour la variété club                 
            --New gestion des frais marketing		
            -- ls_varcode = This.object.geo_article_var_code[row]
            -- select ach_pu_mini into :ld_prix_mini from geo_variet where var_code = :ls_varcode ;
            select tvt_code, sco_code into ls_tvt_code, ls_sco_code from geo_ordre where ord_ref = ls_ord_ref;
            declare
                ls_var_code varchar2(50);
                ls_cat_code varchar2(50);
                ll_article_mode_culture varchar2(50);
                ls_ori_code varchar2(50);
                ls_ccw_code varchar2(50);
                ld_frais_pu_mark number;
                ls_frais_unite_mark number;
                ld_accompte number;
                ls_perequation number;
                ll_k_frais number;
            begin
                select var_code, cat_code, mode_culture, ori_code, ccw_code
                into ls_var_code, ls_cat_code, ll_article_mode_culture, ls_ori_code, ls_ccw_code
                from geo_article_colis 
                where art_ref = ls_art_ref and valide ='O';
                --ll_k_frais = f_recup_frais(ls_var_code, ls_cat_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code)
                f_recup_frais(ls_var_code, ls_ccw_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);
                select frais_pu, frais_unite, accompte, perequation
                into ld_frais_pu_mark, ls_frais_unite_mark, ld_accompte, ls_perequation
                from geo_attrib_frais
                where k_frais = ll_k_frais;
        
                if  ls_perequation =  'O' then
                    ld_prix_mini := ld_accompte;
                end if;
            exception when others then
                ld_prix_mini := 0;
            end;
            if ld_prix_mini > 0 and ld_prix_mini is not null and arg_soc_code <> 'IMP' and arg_soc_code <> 'BUK'  then 
                update geo_ordlig
                set
                    ach_pu = ld_prix_mini,
                    ach_dev_pu = ld_dev_taux * ld_prix_mini
                where orl_ref = arg_orl_ref;
                commit;
            else				
                update geo_ordlig
                set ach_dev_pu = ld_dev_taux * ld_dev_taux * ld_prix_mini
                where orl_ref = arg_orl_ref;
                commit;											
            end if;     
            --fin marketing
                    /*if SQLCA.SQLCode = 0 then
            --Pour affecter uniquement le prix mini sur le mode de culture à 0 demande de AVIALARET LLEF
            --DEB LLEF
                    ls_art_ref =  this.object.geo_ordlig_art_ref[row] 
                    select mode_culture into :ll_mode_culture from geo_article where art_ref = :ls_art_ref;
            --FIN LLEF
                    --             if ld_prix_mini > 0 and not(isnull(ld_prix_mini)) and arg_soc_code <> 'IMP' then
                            if ld_prix_mini > 0 and not(isnull(ld_prix_mini)) and arg_soc_code <> 'IMP' and arg_soc_code <> 'BUK' and ll_mode_culture = 0 then --LLEF
                                                this.SetItem(row,'ach_pu',          ld_prix_mini)
                                                ld_ach_pu = ld_prix_mini
                                                ld_ach_dev_pu =  ld_dev_taux * ld_prix_mini
                                                This.SetItem(row,'ach_pu', ld_ach_pu) 
                                                this.SetItem(row,'geo_ordlig_ach_dev_pu',        ld_ach_dev_pu) 
                                                This.object.ach_pu[row]= ld_ach_pu
                                                This.object.geo_ordlig_ach_dev_pu[row] = ld_ach_dev_pu
                            ELSE
                                                                                                
                                    ld_ach_dev_pu = this.object.geo_ordlig_ach_dev_pu[row]
                                    ld_ach_pu = ld_dev_taux * ld_ach_dev_pu
                                        This.object.ach_pu[row] =  ld_ach_pu
        
                                end if                    
                end if  */
        exception when others then
            msg := 'la devise n''est pas renseignée pour ce fournisseur';
            update geo_ordlig
            set
                ach_dev_code = ls_soc_dev_code,
                ach_dev_taux = 1.0
            where orl_ref = arg_orl_ref;
            commit;
        end;
                    
    end if;

    if ls_sco_code = 'F' then

        If (ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR') or  (ls_vte_bta <> 'UNITE'  and ls_ach_bta <> 'UNITE')	then		
            of_repartition_palette(arg_orl_ref, ls_sco_code, arg_user, res, msg);
            -- On met aussi à jour les lignes avec l'ancien expéditeur
            for r in cur_ols
            loop
                If  r.fou_code = ls_fou_code_old Then
                    of_repartition_palette(arg_orl_ref, ls_sco_code, arg_user, res, msg);
                end if;
            end loop;
        End If;
    end if;
    --DEBUT LLEF AUTOM. IND. TRANSP. + tri et emballage retrait
    if arg_soc_code ='UDC' and ls_sco_code = 'RET' then
        for r in cur_ols
        loop
            update geo_ordlig
            set propr_code = ls_val, fou_code = ls_fou_code
            where orl_ref = arg_orl_ref;
            commit;
        end loop;
        of_sauve_ordre(ls_ord_ref, res, msg);
    end if;
    --FIN LLEF
    
    --DEB TRANSPORT PAR DEFAUT
    --Vérification que le bassin de la station est en phase avec le bassin du transport par défaut souhaité par l'entrepôt
    --Table GEO_ENT_TRP_BASSIN

    If arg_soc_code <> 'BUK' and  ls_typ_ordre <>'RGP' Then
        declare
            is_bassin varchar2(50);
            ls_bac_code varchar2(50);
            ls_trp_code varchar2(50);
            ls_trp_bta_code varchar2(50);
            ls_trp_dev_code varchar2(50);
            ld_trp_dev_pu number;
            ld_dev_tx number;
        begin
            select bac_code into ls_bac_code from geo_fourni where fou_code = ls_val;
            -- messagebox ('', 'is_bassin:' + is_bassin + ' ls_bac_code:' + ls_bac_code)
            if is_bassin is null or is_bassin = '' then
                select trp_code, trp_bta_code, trp_dev_code, trp_pu, dev_tx
                into ls_trp_code, ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_pu, ld_dev_tx
                from geo_ent_trp_bassin, geo_devise_ref 
                where cen_ref = ls_cen_ref and 
                            bac_code = ls_bac_code and 
                            trp_dev_code = dev_code and 
                            dev_code_ref =ls_soc_dev_code  ;

                update geo_ordre
                set
                    trp_pu = ld_dev_tx * ld_trp_dev_pu,
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
        end;
    
    End If;
    --FIN TRANSPORT PAR DEFAUT

    msg := 'OK';
    res := 1;
    return;

end;