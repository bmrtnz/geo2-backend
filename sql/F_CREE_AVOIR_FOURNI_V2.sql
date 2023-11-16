CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREE_AVOIR_FOURNI_V2 (
    ar_old_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    ar_new_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    ar_lit_ref in GEO_LITLIG.LIT_REF%TYPE,
    ar_new_nordre in GEO_ORDRE.NORDRE%TYPE,
    arg_soc_code in GEO_SOCIETE.SOC_CODE%TYPE,
    arg_username IN GEO_USER.nom_utilisateur%type,
    res out number,
    msg out clob
)
AS
    ls_tyt_code varchar2(50);
    ls_ret varchar2(50);
    ls_ref_cli varchar2(70);
    ls_pk_ordlig varchar2(50);
    ls_ordlig_orl_ref varchar2(50);
    ls_ordlig_ord_ref varchar2(50);
    ls_ordlig_orl_lig varchar2(50);
    ls_ordlig_pal_code varchar2(50);
    ls_ordlig_pan_code varchar2(50);
    ls_ordlig_ach_dev_code varchar2(50);
    ls_ordlig_ach_bta_code varchar2(50);
    ls_ordlig_vte_bta_code varchar2(50);
    ls_ordlig_fou_code varchar2(50);
    ls_ordlig_grp_code varchar2(50);
    ls_ordlig_trp_code varchar2(50);
    ls_ordlig_valide varchar2(50);
    ls_ordlig_lib_dlv varchar2(50);
    ls_ordlig_pca_ref varchar2(50);
    ls_ordlig_pde_ref varchar2(50);
    ls_ordlig_art_ref varchar2(50);
    ls_ordlig_propr_code varchar2(50);
    ls_ordlig_tie_code varchar2(50);
    ls_lcq_code varchar2(50);
    ls_fou_code varchar2(50);
    ls_instructions varchar2(50);
    ls_fou_ref_doc varchar2(50);
    ls_ref_logistique varchar2(50);
    ls_ref_document varchar2(50);
    ls_valide varchar2(50);
    ls_flag_exped_fournni varchar2(50);
    ls_datdep_fou_p_yyyymmdd varchar2(50);
    ls_plomb varchar2(50);
    ls_immatriculation varchar2(50);
    ls_DETECTEUR_TEMP varchar2(50);
    ls_certif_controle varchar2(50);
    ls_certif_phyto varchar2(50);
    ls_bill_of_lading varchar2(50);
    ls_container varchar2(50);
    ls_locus_trace varchar2(50);
    ls_list_certifs varchar2(30);
    ls_cert_origine varchar2(30);

    ldc_ordlig_exp_pds_net number;
    ldc_ordlig_ach_pu number;
    ll_ordlig_cde_nb_pal number;
    ll_ordlig_cde_nb_col number;
    ll_ordlig_exp_nb_pal number;
    ll_ordlig_exp_nb_col number;
    ll_ordlig_exp_pds_brut number;
    ldc_ordlig_ach_qte number;
    ldc_ordlig_vte_pu number;
    ldc_ordlig_vte_qte number;
    ldc_ach_dev_pu number;
    ll_pal_nb_sol number;
    ll_pal_nb_pb100x120 number;
    ll_pal_nb_pb80x120 number;
    ll_pal_nb_pb60x80 number;
    li_frais_pu number := 0;

    ld_datdep_fou_p timestamp;
    ld_datdep_fou_r timestamp;
BEGIN
    res := 0;
    msg := '';

    -- on récupère le type de responsable (si fournisseur on remplira la partie achat)
    begin
        select tyt_code, ref_cli into ls_tyt_code, ls_ref_cli from geo_litige where lit_ref = ar_lit_ref;
    exception when others then
        msg := '%%% litige ' || ar_lit_ref || ' introuvable';
        return;
    end;
    if ls_ref_cli is null then
        ls_ref_cli := '';
    else
        ls_ref_cli := ls_ref_cli || ' ';
    end if;
    ls_ref_cli    := ls_ref_cli ||  'litige ' || ar_lit_ref || '/ordre ';
    ls_ref_cli    := substr(ls_ref_cli,0,63);
    --on crée occurence geo_ordre
    begin
        insert
        into geo_ordre ins_tbl
        (ord_ref, soc_code, cam_code, nordre,
         per_codeass, per_codecom, cli_ref, cli_code,
         ref_cli, cen_ref, cen_code, sco_code,
         pay_code, dev_code, dev_tx, inc_code,
         inc_lieu, trp_code, trp_bta_code, trp_pu,
         trp_prix_visible, ref_logistique, ref_document, trs_code,
         trs_bta_code, trs_pu, trs_prix_visible, trs_ville,
         crt_code, crt_bta_code, crt_pu, crt_prix_visible,
         depdatp, livdatp, tvt_code,
         tvr_code, mpm_code, bpm_code, ent_echnbj,
         ent_echle, cov_code, remsf_tx, remhf_tx,
         ord_ref_pere, ent_factcom, instructions_logistique,
         valide, ttr_code,
         lib_dlv, facture_avoir,typ_ordre,frais_pu, mod_user)
        select
            ar_new_ord_ref, soc_code, cam_code, ar_new_nordre,
            per_codeass, per_codecom, cli_ref, cli_code,
            ls_ref_cli || nordre, cen_ref, cen_code, sco_code,
            pay_code, dev_code, dev_tx, inc_code,
            inc_lieu, trp_code, trp_bta_code, 0,
            trp_prix_visible, ref_logistique, ref_document, trs_code,
            trs_bta_code, 0, trs_prix_visible, trs_ville,
            crt_code, crt_bta_code, 0, crt_prix_visible,
            depdatp, livdatp, tvt_code,
            tvr_code, mpm_code, bpm_code, ent_echnbj,
            ent_echle, cov_code, remsf_tx, remhf_tx,
            ar_old_ord_ref, ent_factcom, instructions_logistique,
            valide, ttr_code,
            lib_dlv, 'A','AFO',li_frais_pu,arg_username
        from geo_ordre sel_tbl
        where sel_tbl.ord_ref = ar_old_ord_ref;
    exception when others then
        msg    :=  '%%% litige ' || ar_lit_ref || ' pb sur insertion geo_ordre erreur ' || to_char(SQLCode) || ' ' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;
    -- on crée occurences geo_ordlig
    --ls_pk_ordlig = f_pk_geo_ordlig()


    DECLARE
        cursor C_ordlig_lit is
            select ORL.orl_lig, ORL.pal_code, ORL.pan_code, LIL.cli_nb_pal * -1,
                   LIL.res_nb_col * -1, LIL.res_nb_pal * -1, LIL.res_nb_col * -1, LIL.res_pds_net * -1,LIL.res_pds_net * -1, LIL.res_pu, LIL.res_dev_code, LIL.res_bta_code,
                   LIL.res_qte * -1, 0, '', 0, ORL.fou_code, ORL.grp_code, ORL.trp_code, ORL.valide, ORL.lib_dlv, ORL.pca_ref, ORL.pde_ref, ORL.art_ref,LIL.tie_code,
                   ORL.propr_code, LIL.tyt_code,LIL.lcq_code, LIL.res_dev_pu,ORL.list_certifs,ORL.cert_origine
            from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL
            where ORL.orl_ref = LIL.orl_ref
              and (LIL.tyt_code = 'F' or LIL.lcq_code in ('A','B','F'))
              and (LIL.res_qte is not null and LIL.res_qte <> 0)
              and LIL.lit_ref = ar_lit_ref;
    begin
        OPEN C_ordlig_lit;
        loop
            FETCH     C_ordlig_lit into
                ls_ordlig_orl_lig, ls_ordlig_pal_code, ls_ordlig_pan_code,
                ll_ordlig_cde_nb_pal, ll_ordlig_cde_nb_col, ll_ordlig_exp_nb_pal, ll_ordlig_exp_nb_col,ll_ordlig_exp_pds_brut,
                ldc_ordlig_exp_pds_net, ldc_ordlig_ach_pu,ls_ordlig_ach_dev_code, ls_ordlig_ach_bta_code,ldc_ordlig_ach_qte,
                ldc_ordlig_vte_pu,ls_ordlig_vte_bta_code, ldc_ordlig_vte_qte,ls_ordlig_fou_code, ls_ordlig_grp_code,
                ls_ordlig_trp_code, ls_ordlig_valide, ls_ordlig_lib_dlv, ls_ordlig_pca_ref, ls_ordlig_pde_ref, ls_ordlig_art_ref,
                ls_ordlig_tie_code,ls_ordlig_propr_code,ls_tyt_code,ls_lcq_code, ldc_ach_dev_pu,ls_list_certifs,ls_cert_origine ;
            exit when C_ordlig_lit%notfound;

            CASE ls_tyt_code
                when 'F' then
                    ls_ordlig_propr_code := ls_ordlig_tie_code;
                ELSE

                    CASE ls_lcq_code

                        when 'A' then
                            null;
                        when 'B' then
                            null;
                        when 'F' then
                            null;

                        ELSE
                            null;
                        /*        ldc_ordlig_ach_pu = 0
                                ls_ordlig_ach_bta_code=''
                                ldc_ordlig_ach_qte = 0*/

                        END case;

                END case;

            insert     into geo_admin.geo_ordlig ins_tbl
            (orl_ref, ord_ref, orl_lig,
             pal_code, pan_code, cde_nb_pal,
             cde_nb_col, exp_nb_pal, exp_nb_col, exp_pds_brut,
             exp_pds_net, ach_pu, ach_dev_code, ach_bta_code,
             ach_qte, vte_pu, vte_bta_code, vte_qte,
             fou_code, grp_code, trp_code, valide, lib_dlv, pca_ref, pde_ref, art_ref,propr_code,ach_dev_pu,frais_pu, mod_user,list_certifs,cert_origine)
            VALUES (F_SEQ_ORL_SEQ, ar_new_ord_ref, ls_ordlig_orl_lig, ls_ordlig_pal_code, ls_ordlig_pan_code,
                    ll_ordlig_cde_nb_pal, ll_ordlig_cde_nb_col, ll_ordlig_exp_nb_pal, ll_ordlig_exp_nb_col,ll_ordlig_exp_pds_brut,
                    ldc_ordlig_exp_pds_net, ldc_ordlig_ach_pu,ls_ordlig_ach_dev_code, ls_ordlig_ach_bta_code,ldc_ordlig_ach_qte,
                    ldc_ordlig_vte_pu,ls_ordlig_vte_bta_code, ldc_ordlig_vte_qte,ls_ordlig_fou_code, ls_ordlig_grp_code,
                    ls_ordlig_trp_code, ls_ordlig_valide, ls_ordlig_lib_dlv, ls_ordlig_pca_ref, ls_ordlig_pde_ref, ls_ordlig_art_ref,
                    ls_ordlig_propr_code, ldc_ach_dev_pu,li_frais_pu, arg_username,ls_list_certifs,ls_cert_origine);


        end LOOP;



        CLOSE C_ordlig_lit;
    exception when others then
        msg :=  '%%% litige ' || ar_lit_ref || ' pb sur insertion geo_ordlig erreur ' || to_char(SQLCode) || ' ' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;





    DECLARE
        cursor C_ordlog_lit is
            select LOG.FOU_CODE,LOG.INSTRUCTIONS,LOG.DATDEP_FOU_P,LOG.DATDEP_FOU_R,LOG.FOU_REF_DOC,LOG.REF_LOGISTIQUE,
                   LOG.REF_DOCUMENT,LOG.VALIDE,LOG.FLAG_EXPED_FOURNNI,LOG.PAL_NB_SOL,LOG.PAL_NB_PB100X120,LOG.PAL_NB_PB80X120,LOG.PAL_NB_PB60X80,LOG.DATDEP_FOU_P_YYYYMMDD,
                   LOG.PLOMB, LOG.IMMATRICULATION, LOG.DETECTEUR_TEMP, LOG.CERTIF_CONTROLE, LOG.CERTIF_PHYTO, LOG.BILL_OF_LADING, LOG.CONTAINER, LOG.LOCUS_TRACE
            from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL,geo_admin.geo_ordlog  LOG
            where ORL.orl_ref = LIL.orl_ref
              and (LIL.tyt_code = 'F' or LIL.lcq_code in ('A','B','F'))
              and (LIL.res_qte is not null and LIL.res_qte <> 0)
              and LIL.lit_ref = ar_lit_ref
              and ORL.ORD_REF = LOG.ORD_REF
              and ORL.FOU_CODE = LOG.FOU_CODE;
    BEGIN
        OPEN     C_ordlog_lit;

        loop
            FETCH     C_ordlog_lit into ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,ls_flag_exped_fournni,ll_pal_nb_sol,ll_pal_nb_pb100x120,ll_pal_nb_pb80x120,ll_pal_nb_pb60x80,ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_LOCUS_TRACE;
            exit when C_ordlog_lit%notfound;


            insert    into geo_admin.geo_ordlog
            (orx_ref, ord_ref, fou_code,instructions,datdep_fou_p,datdep_fou_r,fou_ref_doc,ref_logistique,ref_document,valide,flag_exped_fournni,datdep_fou_p_yyyymmdd, plomb, immatriculation, DETECTEUR_TEMP, certif_controle, certif_phyto, bill_of_lading, container, LOCUS_TRACE)
            VALUES (F_SEQ_ORX_SEQ(), ar_new_ord_ref, ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,'O',ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_LOCUS_TRACE );

            select sum(EXP_NB_PAL) into ll_pal_nb_sol
            from GEO_ORDLIG
            where     ORD_REF   =ar_new_ord_ref and
                    FOU_CODE     =ls_fou_code;

            update geo_ordlog set pal_nb_sol =ll_pal_nb_sol
            where     ORD_REF   =ar_new_ord_ref and
                    FOU_CODE     =ls_fou_code;

        end LOOP;


        CLOSE C_ordlog_lit;
    END;


    /*
    insert
    into geo_admin.geo_ordlig ins_tbl
    (orl_ref, ord_ref, orl_lig,
    pal_code, pan_code, cde_nb_pal,
    cde_nb_col, exp_nb_pal, exp_nb_col, exp_pds_brut,
    exp_pds_net, ach_pu, ach_dev_code, ach_bta_code,
    ach_qte, vte_pu, vte_bta_code, vte_qte,
    fou_code, grp_code, trp_code, valide, lib_dlv, pca_ref, pde_ref, art_ref,propr_code)

    select
    F_SEQ_ORL_SEQ, :ar_new_ord_ref, ORL.orl_lig,
    ORL.pal_code, ORL.pan_code, LIL.cli_nb_pal * -1,
    LIL.res_nb_col * -1, LIL.res_nb_pal * -1, LIL.res_nb_col * -1, LIL.res_pds_net * -1,
    LIL.res_pds_net * -1, LIL.res_pu, ORL.ach_dev_code, LIL.res_bta_code,
    LIL.res_qte * -1, 0, '', 0,
    ORL.fou_code, ORL.grp_code, ORL.trp_code, ORL.valide, ORL.lib_dlv, ORL.pca_ref, ORL.pde_ref, ORL.art_ref,LIL.tie_code
    from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL
    where ORL.orl_ref = LIL.orl_ref
    and LIL.tyt_code = 'F'
    and (LIL.res_qte is not null and LIL.res_qte <> 0)
    and LIL.lit_ref = :ar_lit_ref;



    if SQLCA.SQLCode <> 0 then
        ls_ret    =  '%%% litige ' + ar_lit_ref + ' pb sur insertion geo_ordlig erreur ' + string(SQLCA.SQLCode) + ' ' + SQLCA.SQLErrText
        rollback;
        return ls_ret
    end if
    */

    commit;

    msg := 'OK';
    res := 1;
END;
