CREATE OR REPLACE PROCEDURE GEO_ADMIN.FN_GEN_ORDRE_NEUTRE_TESCO_SA (
    arg_ord_ref_buk in GEO_ORDRE.ORD_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_ord_ref_buk varchar2(50);
    ls_ord_ref_sa varchar2(50);
    ls_ord_ref_sa_n varchar2(50);
    ls_sa_cli_ref varchar2(50);
    ls_sa_cen_ref varchar2(50);
    ls_sa_cli_code varchar2(50);
    ls_sa_cen_code varchar2(50);
    ls_nordre_sa_n varchar2(50);

    ls_tab_ord_ref_buk p_str_tab_type := p_str_tab_type();
    ls_tab_sa_cli_ref p_str_tab_type := p_str_tab_type();
    ls_tab_sa_cen_ref p_str_tab_type := p_str_tab_type();

    ll_ord_ref_sa_n number;
    li_indice number;
    ldc_dev_taux_GBP number;
BEGIN
    res := 0;
    msg := '';

    select C.CLI_REF,C.CLI_CODE,E.CEN_REF,E.CEN_CODE,OBS.ORD_REF_SA
    into  ls_sa_cli_ref,ls_sa_cli_code,ls_sa_cen_ref,ls_sa_cen_code,ls_ord_ref_sa
    FROM GEO_ENTREP E, GEO_CLIENT C, GEO_ORDRE_BUK_SA OBS,  GEO_ORDRE O
    where E.CLI_REF ='007657' and
            C.CLI_REF =  E.CLI_REF and
            OBS.ORD_REF_BUK  =arg_ord_ref_buk and
            OBS.ORD_REF_SA  = O.ORD_REF and
            (O.CEN_CODE||'2' = E.CEN_CODE OR substr(O.CEN_CODE,1,17)||'2'= E.CEN_CODE);


    select  DEV_TX into ldc_dev_taux_GBP from GEO_DEVISE_REF
    where DEV_CODE_REF ='EUR' and DEV_CODE ='GBP';



    select seq_ord_num.nextval into ll_ord_ref_sa_n from dual;

    ls_ord_ref_sa_n	:= to_char(ll_ord_ref_sa_n,'000000');
    f_nouvel_ordre('SA', res, msg, ls_nordre_sa_n);

    begin
        insert into GEO_ORDRE (ord_ref ,soc_code,cam_code,nordre,per_codeass,per_codecom,cli_ref,cli_code,ref_cli,cen_ref,cen_code,sco_code,pay_code,dev_code,dev_tx,inc_code,inc_lieu,trp_code,trp_bta_code,trp_pu,trp_prix_visible,ref_logistique,ref_document,trs_code,trs_bta_code,trs_pu,trs_prix_visible,trs_ville,crt_code,crt_bta_code,crt_pu,crt_prix_visible,depdatp,livdatp,credat,tvt_code,tvr_code,mpm_code,bpm_code,ent_echnbj,ent_echle,cov_code,remsf_tx,remhf_tx,totvte,totrem,totres,totfrd,totach,totmob,tottrp,tottrs,totcrt,flexp,flliv,flbaf,flfac,ord_ref_pere,ent_factcom,instructions_logistique,mod_user,mod_date,valide,version_ordre,version_detail,version_ordre_date,version_detail_date,ttr_code,lib_dlv,totpal,totcol,totpdsnet,totpdsbrut,fac_num,datech,tot_ht_brut,tot_remsf,tot_remhf,datfac,seqfac,compte,pied_ht_brut,pied_ht_rist,pied_ht_ctifl,pied_rist,pied_ht_net,pied_ctifl,pied_ht_final,pied_tva,pied_ttc,pied_ht_brut_0,pied_ht_brut_1,pied_ht_brut_2,pied_ht_rist_0,pied_ht_rist_1,pied_ht_rist_2,pied_ht_ctifl_0,pied_ht_ctifl_1,pied_ht_ctifl_2,pied_rist_0,pied_rist_1,pied_rist_2,pied_ht_net_0,pied_ht_net_1,pied_ht_net_2,pied_ctifl_0,pied_ctifl_1,pied_ctifl_2,pied_ht_final_0,pied_ht_final_1,pied_ht_final_2,cpte_rist,cpte_ctifl,cpte_tva,taux_ctifl,taux_tva_1,taux_tva_2,pied_tva_1,pied_tva_2,flag_imprime,flag_compta,depdatp_asc,facture_avoir,frais_pu,frais_unite,frais_desc,lino_flag,flag_qp,flag_udc,avmem,ack_transp,flag_public,vente_commission,flbagqp,flgenqp,fbagudc,flgenudc,invoic,invoic_file,rem_sf_tx_mdd,totremsf,comm_interne,pal_nb_sol,pal_nb_pb100x120,pal_nb_pb80x120,pal_nb_pb60x80,invoic_demat,cpte_interfel,taux_interfel,pied_interfel,pied_interfel_0,pied_interfel_1,pied_interfel_2,pied_ht_interfel,pied_ht_interfel_0,pied_ht_interfel_1,pied_ht_interfel_2,came_code,camf_code,nordre_pere,totfad,tot_cde_nb_pal,tot_exp_nb_pal,comment_tva,code_chargement,etd_date,eta_date,etd_location,eta_location,ref_edi_ordre,pal_nb_soltrans,list_trp_code,list_nordre,list_ord_ref,date_envoi_syleg_trp,num_camion,ordre_chargement,trp_dev_code,trp_dev_taux,trp_dev_pu,ord_ref_palox_pere,ord_ref_palox_list_fils,file_cmr,frais_plateforme,totfrais_plateforme,fldet_autom,ref_eta,ref_etd,typ_ordre,totfob,flannul,list_nordre_comp,list_nordre_regul,ind_exclu_frais_pu,ind_pres_spec,totfrd_net,totpereq,trp_bac_code,list_nordre_orig,nordre_rgp)
        select ls_ord_ref_sa_n ,soc_code,cam_code,ls_nordre_sa_n,per_codeass,per_codecom,ls_sa_cli_ref,ls_sa_cli_code,ref_cli,ls_sa_cen_ref,ls_sa_cen_code,sco_code,pay_code,dev_code,dev_tx,inc_code,inc_lieu,trp_code,trp_bta_code,trp_pu,trp_prix_visible,ref_logistique,ref_document,trs_code,trs_bta_code,trs_pu,trs_prix_visible,trs_ville,crt_code,crt_bta_code,crt_pu,crt_prix_visible,depdatp,livdatp,credat,tvt_code,tvr_code,mpm_code,bpm_code,ent_echnbj,ent_echle,cov_code,remsf_tx,remhf_tx,-1*totvte,-1*totrem,-1*totres,-1*totfrd,-1*totach,-1*totmob,-1*tottrp,-1*tottrs,-1*totcrt,flexp,flliv,'O','N',ord_ref_pere,ent_factcom,instructions_logistique,mod_user,mod_date,valide,version_ordre,version_detail,version_ordre_date,version_detail_date,ttr_code,lib_dlv,-1*totpal,-1*totcol,-1*totpdsnet,-1*totpdsbrut,NULL,datech,-1*tot_ht_brut,tot_remsf,-1*tot_remhf,NULL,NULL,compte,-1*pied_ht_brut,-1*pied_ht_rist,-1*pied_ht_ctifl,-1*pied_rist,-1*pied_ht_net,-1*pied_ctifl,-1*pied_ht_final,-1*pied_tva,-1*pied_ttc,-1*pied_ht_brut_0,-1*pied_ht_brut_1,-1*pied_ht_brut_2,-1*pied_ht_rist_0,-1*pied_ht_rist_1,pied_ht_rist_2,-1*pied_ht_ctifl_0,-1*pied_ht_ctifl_1,-1*pied_ht_ctifl_2,-1*pied_rist_0,-1*pied_rist_1,-1*pied_rist_2,pied_ht_net_0,-1*pied_ht_net_1,-1*pied_ht_net_2,-1*pied_ctifl_0,-1*pied_ctifl_1,-1*pied_ctifl_2,-1*pied_ht_final_0,-1*pied_ht_final_1,-1*pied_ht_final_2,cpte_rist,cpte_ctifl,cpte_tva,taux_ctifl,taux_tva_1,taux_tva_2,-1*pied_tva_1,-1*pied_tva_2,flag_imprime,flag_compta,depdatp_asc,'A',frais_pu,frais_unite,frais_desc,lino_flag,flag_qp,flag_udc,avmem,ack_transp,flag_public,vente_commission,flbagqp,flgenqp,fbagudc,flgenudc,'N',NULL,-1*rem_sf_tx_mdd,-1*totremsf,comm_interne,-1*pal_nb_sol,-1*pal_nb_pb100x120,-1*pal_nb_pb80x120,-1*pal_nb_pb60x80,invoic_demat,cpte_interfel,taux_interfel,-1*pied_interfel,-1*pied_interfel_0,-1*pied_interfel_1,-1*pied_interfel_2,-1*pied_ht_interfel,-1*pied_ht_interfel_0,-1*pied_ht_interfel_1,-1*pied_ht_interfel_2,came_code,camf_code,nordre_pere,-1*totfad,-1*tot_cde_nb_pal,-1*tot_exp_nb_pal,comment_tva,code_chargement,etd_date,eta_date,etd_location,eta_location,ref_edi_ordre,pal_nb_soltrans,list_trp_code,list_nordre,list_ord_ref,date_envoi_syleg_trp,num_camion,ordre_chargement,trp_dev_code,trp_dev_taux,trp_dev_pu,ord_ref_palox_pere,ord_ref_palox_list_fils,file_cmr,frais_plateforme,-1*totfrais_plateforme,fldet_autom,ref_eta,ref_etd,'UKN',-1*totfob,flannul,list_nordre_comp,list_nordre_regul,ind_exclu_frais_pu,ind_pres_spec,-1*totfrd_net,-1*totpereq,trp_bac_code,list_nordre_orig,nordre_rgp
        from GEO_ORDRE O , GEO_ORDRE_BUK_SA OBS
        where  O.ORD_REF  = OBS.ORD_REF_SA and
                OBS.ORD_REF_SA =  ls_ord_ref_sa and
                OBS.ORD_REF_BUK =  arg_ord_ref_buk;
    exception when others then
        msg := 'insert INTO GEO_ORDRE' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;

    insert into GEO_ORDLIG ( orl_ref, ORD_REF,orl_lig,pro_ref,pal_code,pan_code,pal_nb_col,cde_nb_pal,cde_nb_col,exp_nb_pal,exp_nb_col,exp_pds_brut,exp_pds_net,ach_pu,ach_dev_code,ach_bta_code,ach_qte,vte_pu,vte_bta_code,vte_qte,fou_code,grp_code,trp_code,mod_user,mod_date,valide,lib_dlv,cde_pds_brut,cde_pds_net,obs_fourni,totvte,totrem,totres,totfrd,totach,totmob,tottrp,tottrs,totcrt,flexp,flliv,flbaf,flfac,fac_nb_pal,fac_nb_col,fac_pds_brut,fac_pds_net,ht_net,tva_1,tva_2,tva_3,compte_a_credit,lig_ht_brut_0,lig_ht_brut_1,lig_ht_brut_2,lig_ht_rist_0,lig_ht_rist_1,lig_ht_rist_2,lig_ht_ctifl_0,lig_ht_ctifl_1,lig_ht_ctifl_2,tvc_code,lig_ht_ctiflrist_0,lig_ht_ctiflrist_1,lig_ht_ctiflrist_2,ht_brut,fou_flver,fou_facnum,fou_facdate,var_ristourne,frais_pu,frais_unite,frais_desc,flverfou,flvertrp,lig_remsf,lig_remhf,cq_ref,fou_facref,trp_facref,pde_ref,pca_ref,bac_code,exp_nb_pal_inter,remsf_tx,remhf_tx,totremsf,vte_pu_net,lig_ht_net_0,lig_ht_net_1,lig_ht_net_2,lig_ht_net_3,bac_fac_num,fou_bac_fac_num,art_ref,esp_code,suc_code,pen_code,tem_code,stm_ref,cql_ref,stock_nb_resa,lig_ht_interfel_0,lig_ht_interfel_1,lig_ht_interfel_2,lig_ht_interfelrist_0,lig_ht_interfelrist_1,lig_ht_interfelrist_2,totfad,ach_dev_taux,ach_dev_pu,user_verfou,user_vertrp,date_verfou,date_vertrp,exp_pal_nb_col,ref_edi_ligne,pal_nb_palinter,palinter_code,demipal_ind,propr_code,ind_gratuit,date_entree_ifco,date_sortie_ifco,nb_cqphotos,indbloq_ach_dev_pu,date_envoi_syleg_fou,palox_ko_nbr,palox_ko_cause,totfrais_plateforme,promo_code,cotisation_interfel_fr,cotisation_interfel_imp,lig_ht_interfel_imp_0,lig_ht_interfel_imp_1,lig_ht_interfel_imp_2,lig_ht_interfelrist_imp_0,lig_ht_interfelrist_imp_1,lig_ht_interfelrist_imp_2,art_ref_kit,gtin_colis_kit,nb_colis_manquant,list_certifs,cert_origine,totfrd_net,totpereq,ind_check_fraude,marg_pu,rist_pu,trp_pu,frais_plat_pu,doua_pu,pres_court_pu,mark_pu,ach_udc_marge)
    select F_SEQ_ORL_SEQ(),ls_ord_ref_sa_n,orl_lig,pro_ref,pal_code,pan_code,pal_nb_col,-1*cde_nb_pal,-1*cde_nb_col,-1*exp_nb_pal,-1*exp_nb_col,-1*exp_pds_brut,-1*exp_pds_net,ach_pu,ach_dev_code,ach_bta_code,ach_qte,vte_pu,vte_bta_code,-1*vte_qte,fou_code,grp_code,trp_code,mod_user,mod_date,valide,lib_dlv,-1*cde_pds_brut,-1*cde_pds_net,obs_fourni,-1*totvte,-1*totrem,-1*totres,-1*totfrd,-1*totach,-1*totmob,-1*tottrp,-1*tottrs,-1*totcrt,flexp,flliv,flbaf,flfac,-1*fac_nb_pal,-1*fac_nb_col,-1*fac_pds_brut,-1*fac_pds_net,-1*ht_net,tva_1,tva_2,tva_3,compte_a_credit,-1*lig_ht_brut_0,-1*lig_ht_brut_1,-1*lig_ht_brut_2,-1*lig_ht_rist_0,-1*lig_ht_rist_1,-1*lig_ht_rist_2,-1*lig_ht_ctifl_0,-1*lig_ht_ctifl_1,-1*lig_ht_ctifl_2,tvc_code,lig_ht_ctiflrist_0,lig_ht_ctiflrist_1,-1*lig_ht_ctiflrist_2,-1*ht_brut,fou_flver,fou_facnum,fou_facdate,var_ristourne,frais_pu,frais_unite,frais_desc,flverfou,flvertrp,lig_remsf,lig_remhf,cq_ref,fou_facref,trp_facref,pde_ref,pca_ref,bac_code,exp_nb_pal_inter,remsf_tx,remhf_tx,-1*totremsf,-1*vte_pu_net,lig_ht_net_0,lig_ht_net_1,lig_ht_net_2,lig_ht_net_3,bac_fac_num,fou_bac_fac_num,art_ref,esp_code,suc_code,pen_code,tem_code,stm_ref,cql_ref,stock_nb_resa,-1*lig_ht_interfel_0,-1*lig_ht_interfel_1,-1*lig_ht_interfel_2,-1*lig_ht_interfelrist_0,-1*lig_ht_interfelrist_1,-1*lig_ht_interfelrist_2,-1*totfad,ach_dev_taux,ach_dev_pu,user_verfou,user_vertrp,date_verfou,date_vertrp,exp_pal_nb_col,ref_edi_ligne,pal_nb_palinter,palinter_code,demipal_ind,propr_code,ind_gratuit,date_entree_ifco,date_sortie_ifco,nb_cqphotos,indbloq_ach_dev_pu,date_envoi_syleg_fou,palox_ko_nbr,palox_ko_cause,-1*totfrais_plateforme,promo_code,cotisation_interfel_fr,cotisation_interfel_imp,-1*lig_ht_interfel_imp_0,-1*lig_ht_interfel_imp_1,-1*lig_ht_interfel_imp_2,-1*lig_ht_interfelrist_imp_0,-1*lig_ht_interfelrist_imp_1,-1*lig_ht_interfelrist_imp_2,art_ref_kit,gtin_colis_kit,nb_colis_manquant,list_certifs,cert_origine,-1*totfrd_net,-1*totpereq,ind_check_fraude,marg_pu,rist_pu,trp_pu,frais_plat_pu,doua_pu,pres_court_pu,mark_pu,ach_udc_marge
    from GEO_ORDLIG L , GEO_ORDRE_BUK_SA OBS
    where L.ORD_REF = OBS.ORD_REF_SA and
            OBS.ORD_REF_SA =  ls_ord_ref_sa and
            OBS.ORD_REF_BUK =  arg_ord_ref_buk;

    begin
        insert  INTO GEO_ORDLOG (  ORX_REF,ORD_REF,fou_code,grp_code,trp_code,instructions,datdep_fou_p,datdep_grp_p,datdep_fou_r,datdep_grp_r,fou_ref_doc,ref_logistique,ref_document,mod_user,mod_date,valide,flag_exped_fournni,flag_exped_groupa,obs_fourni,grp_bta_code,grp_pu,grp_pu_visible,trp_bta_code,trp_pu,trp_pu_visible,ack_fourni,ack_groupa,ack_transp,cq_desc,pal_nb_sol,pal_nb_pb100x120,pal_nb_pb80x120,pal_nb_pb60x80,datdep_fou_p_yyyymmdd,tot_cde_nb_pal,tot_exp_nb_pal,date_modif_bw,date_lecture_station,locus_trace,typ_grp,typ_fou,datliv_grp,orx_rat,incot_fourn,plomb,immatriculation,certif_controle,certif_phyto,bill_of_lading,container,detecteur_temp)
        select  f_seq_orx_seq(),ls_ord_ref_sa_n,fou_code,grp_code,trp_code,instructions,datdep_fou_p,datdep_grp_p,datdep_fou_r,datdep_grp_r,fou_ref_doc,ref_logistique,ref_document,mod_user,mod_date,valide,flag_exped_fournni,flag_exped_groupa,obs_fourni,grp_bta_code,grp_pu,grp_pu_visible,trp_bta_code,trp_pu,trp_pu_visible,ack_fourni,ack_groupa,ack_transp,cq_desc,pal_nb_sol,pal_nb_pb100x120,pal_nb_pb80x120,pal_nb_pb60x80,datdep_fou_p_yyyymmdd,tot_cde_nb_pal,tot_exp_nb_pal,date_modif_bw,date_lecture_station,locus_trace,typ_grp,typ_fou,datliv_grp,orx_rat,incot_fourn,plomb,immatriculation,certif_controle,certif_phyto,bill_of_lading,container,detecteur_temp
        from  GEO_ORDLOG LG, GEO_ORDRE_BUK_SA OBS
        where  LG.ORD_REF  = OBS.ORD_REF_SA and
                    OBS.ORD_REF_SA =  ls_ord_ref_sa and
                OBS.ORD_REF_BUK =  arg_ord_ref_buk;
    exception when others then
        msg := 'insert INTO GEO_ORDLOG' || SQLERRM;
        res := 0;
        rollback;
        return;
	end;

    begin
        UPDATE GEO_ORDRE_BUK_SA SET  ORD_REF_SA_N  = ls_ord_ref_sa_n
        where ORD_REF_SA 			=  ls_ord_ref_sa and
   	 	      ORD_REF_BUK 		=  arg_ord_ref_buk;
    exception when others then
        msg := 'insert INTO GEO_ORDRE_BUK_SA' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;
	commit;
    res := 1;
END;
/

