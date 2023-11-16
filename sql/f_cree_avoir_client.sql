	-- crée un avoir client avec les éléments de l'ordre et du litige passés en paramètres
	-- on récupère le type de responsable (si fournisseur on remplira la partie achat)
	-- V2 en fait on vérifie que le litige existe (il y a séparation entre client et fournisseur
	-- catv2 AR 28/06/05
	-- AR 24/04/07 implémente article vs catalogue
	-- AR 12/10/09 corrige bug seq_lil_num remplacé par seq_orl_num
	-- AR 09/09/10 modif PK ordlig
	-- AR 14/11/11 ajout frais_annexes dy litiges dans geo_ord_fra
--select to_char(seq_orl_num.nextval,'FM0XXXX') || 'A' into :is_cur_orl_ref from dual;
	-- SL 21/10/2013 On prend la valeur de REF_CLI du litige plutôt que celle de l'ordre pour créer l'ordre avoir pour concerver les modification saisies dans cette partie de l'onglet litige

CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREE_AVOIR_CLIENT (
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
    ls_ref_cli varchar2(50);
    ls_comm_interne clob;
    ls_nordre varchar2(50);
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
    ls_LOCUS_TRACE varchar2(50);

    ld_datdep_fou_p date;
    ld_datdep_fou_r date;

    ld_lit_frais_annexes number;
    ll_pal_nb_sol number;
    ll_pal_nb_pb100x120 number;
    ll_pal_nb_pb80x120 number;
    ll_pal_nb_pb60x80 number;
    lnb_frais_annexes number;
    li_frais_pu number  := 0;
    --string ls_pk_ordlig
BEGIN
    res := 0;
    msg := '';

    -- on récupère le type de responsable (si fournisseur on remplira la partie achat)
    begin
        select tyt_code, ref_cli, lit_frais_annexes into ls_tyt_code, ls_ref_cli, ld_lit_frais_annexes
        from geo_litige where lit_ref = ar_lit_ref;
    exception when others then
        msg := '%%% litige ' || ar_lit_ref || ' introuvable';
        res := 0;
        return;
    end;

    ls_comm_interne	:= 'litige ' || ar_lit_ref;

    declare
        consequences clob := '';
        cursor cons_lit is
            SELECT
                lc.lcq_desc
            FROM
                geo_litlig ll,
                geo_litcon lc
            WHERE
                ll.LCQ_CODE = lc.LCQ_CODE
                AND ll.lit_ref = ar_lit_ref;
    begin
        for c in cons_lit loop
            consequences := consequences || c.lcq_desc || ', ' ;
        end loop;
        ls_comm_interne := ls_comm_interne || ' (' || consequences || ') ';
    end;

        --on crée occurence geo_ordre
    /*
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
    ent_echle, cov_code, remsf_tx, remhf_tx, rem_sf_tx_mdd,
    ord_ref_pere, ent_factcom, instructions_logistique,
    valide, ttr_code,
    lib_dlv, facture_avoir, comm_interne)
    select
    :ar_new_ord_ref, soc_code, cam_code, :ar_new_nordre,
    per_codeass, per_codecom, cli_ref, cli_code,
    ref_cli, cen_ref, cen_code, sco_code,
    pay_code, dev_code, dev_tx, inc_code,
    inc_lieu, trp_code, trp_bta_code, 0,
    trp_prix_visible, ref_logistique, ref_document, trs_code,
    trs_bta_code, 0, trs_prix_visible, trs_ville,
    crt_code, crt_bta_code, 0, crt_prix_visible,
    depdatp, livdatp, tvt_code,
    tvr_code, mpm_code, bpm_code, ent_echnbj,
    ent_echle, cov_code, remsf_tx, remhf_tx, rem_sf_tx_mdd,
    :ar_old_ord_ref, ent_factcom, instructions_logistique,
    valide, ttr_code,
    lib_dlv, 'A', :ls_comm_interne || nordre
    from geo_ordre sel_tbl
    where sel_tbl.ord_ref = :ar_old_ord_ref;
    */
    begin
        insert
        into geo_ordre
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
        ent_echle, cov_code, remsf_tx, remhf_tx, rem_sf_tx_mdd,
        ord_ref_pere, ent_factcom, instructions_logistique,
        valide, ttr_code,
        lib_dlv, facture_avoir, comm_interne,frais_pu, mod_user)
        select
        ar_new_ord_ref, O.soc_code, O.cam_code, ar_new_nordre,
        O.per_codeass, O.per_codecom, O.cli_ref, O.cli_code,
        O.ref_cli, O.cen_ref, O.cen_code, O.sco_code,
        O.pay_code, O.dev_code, O.dev_tx, O.inc_code,
        O.inc_lieu, O.trp_code, O.trp_bta_code, 0,
        O.trp_prix_visible, O.ref_logistique, O.ref_document, O.trs_code,
        O.trs_bta_code, 0, O.trs_prix_visible, O.trs_ville,
        O.crt_code, O.crt_bta_code, 0, O.crt_prix_visible,
        O.depdatp, O.livdatp, O.tvt_code,
        O.tvr_code, O.mpm_code, O.bpm_code, O.ent_echnbj,
        O.ent_echle, O.cov_code, O.remsf_tx, O.remhf_tx, O.rem_sf_tx_mdd,
        ar_old_ord_ref, O.ent_factcom, O.instructions_logistique,
        O.valide, O.ttr_code,
        lib_dlv, 'A', substr(ls_comm_interne || '/ordre ' || O.nordre, 0, 127),li_frais_pu, arg_username
        from geo_ordre O, geo_litige L
        where O.ord_ref = ar_old_ord_ref AND L.ORD_REF_ORIGINE = O.ord_ref;
    exception when others then
        msg	:=  '%%% litige ' || ar_lit_ref || ' pb sur insertion geo_ordre erreur ' || to_char(SQLCODE) || ' ' || SQLERRM;
        rollback;
        return;
    end;

    --ls_pk_ordlig = f_pk_geo_ordlig()

    begin
        insert
        into geo_admin.geo_ordlig ins_tbl
        (orl_ref, ord_ref, orl_lig,
        pal_code, pan_code, cde_nb_pal,
        cde_nb_col, exp_nb_pal, exp_nb_col, exp_pds_brut,
        exp_pds_net, ach_pu, ach_dev_code, ach_bta_code,
        ach_qte, vte_pu, vte_bta_code, vte_qte,
        fou_code, grp_code, trp_code, valide, lib_dlv, pca_ref, pde_ref, art_ref,propr_code,var_ristourne,remhf_tx,remsf_tx,frais_pu,mod_user)
        select
        F_SEQ_ORL_SEQ, ar_new_ord_ref, ORL.orl_lig,
        ORL.pal_code, ORL.pan_code, LIL.cli_nb_pal * -1,
        LIL.cli_nb_col * -1, LIL.cli_nb_pal * -1, LIL.cli_nb_col * -1, LIL.cli_pds_net * -1,
        LIL.cli_pds_net * -1, 0, '', '',
        0, LIL.cli_pu, LIL.cli_bta_code, LIL.cli_qte * -1,
        ORL.fou_code, ORL.grp_code, ORL.trp_code, ORL.valide, ORL.lib_dlv, ORL.pca_ref, ORL.pde_ref, ORL.art_ref,ORL.propr_code,ORL.var_ristourne,ORL.remhf_tx,ORL.remsf_tx,li_frais_pu,arg_username
        from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL
        where ORL.orl_ref = LIL.orl_ref
        and (LIL.cli_qte is not null and LIL.cli_qte <> 0)
        and LIL.lit_ref = ar_lit_ref;
    exception when others then
            msg	:=  '%%% litige ' || ar_lit_ref || ' pb sur insertion geo_ordlig erreur ' || to_char(SQLCODE) || ' ' || SQLERRM;
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
            and (LIL.cli_qte is not null and LIL.cli_qte <> 0)
            and LIL.lit_ref = ar_lit_ref
            and ORL.ORD_REF = LOG.ORD_REF
            and ORL.FOU_CODE = LOG.FOU_CODE;
    begin
        OPEN	 C_ordlog_lit;
        loop
            FETCH 	C_ordlog_lit into ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,ls_flag_exped_fournni,ll_pal_nb_sol,ll_pal_nb_pb100x120,ll_pal_nb_pb80x120,ll_pal_nb_pb60x80,ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_LOCUS_TRACE;
            exit when C_ordlog_lit%notfound;

            insert	into geo_admin.geo_ordlog
            (orx_ref, ord_ref, fou_code,instructions,datdep_fou_p,datdep_fou_r,fou_ref_doc,ref_logistique,ref_document,valide,flag_exped_fournni,datdep_fou_p_yyyymmdd, plomb, immatriculation, DETECTEUR_TEMP, certif_controle, certif_phyto, bill_of_lading, container, locus_trace)
            VALUES (F_SEQ_ORX_SEQ(), ar_new_ord_ref, ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,'O',ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_LOCUS_TRACE );


            select sum(EXP_NB_PAL) into ll_pal_nb_sol
            from GEO_ORDLIG
            where 	ORD_REF   =ar_new_ord_ref and
                        FOU_CODE	 =ls_fou_code;

            update geo_ordlog set pal_nb_sol =ll_pal_nb_sol
            where 	ORD_REF   =ar_new_ord_ref and
                        FOU_CODE	 =ls_fou_code;

            -- FETCH 	C_ordlog_lit into ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,ls_flag_exped_fournni,ll_pal_nb_sol,ll_pal_nb_pb100x120,ll_pal_nb_pb80x120,ll_pal_nb_pb60x80,ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_LOCUS_TRACE;
        end loop;


        CLOSE C_ordlog_lit;
    end;




    if ld_lit_frais_annexes <> 0 then
        select  count(*) into lnb_frais_annexes
        from GEO_ORDFRA_LITIGE
        where  LIT_REF = ar_lit_ref;

        If lnb_frais_annexes > 0 Then
            insert into geo_ordfra (orf_ref, ord_ref, fra_code, montant, dev_code, dev_tx, fra_desc, valide,trp_code_plus)
                select  to_char(seq_orf_num.nextval,'FM0XXXX'), ar_new_ord_ref, fra_code,montant, arg_soc_code, 1, fra_desc, 'O',trp_code_plus
                from geo_ordfra_litige
                where lit_ref =ar_lit_ref;
        Else
            begin
                insert into geo_ordfra (orf_ref, ord_ref, fra_code, montant, dev_code, dev_tx, fra_desc, valide)
                values(to_char(seq_orf_num.nextval,'FM0XXXX'), ar_new_ord_ref, 'DIVERS', ld_lit_frais_annexes,arg_soc_code  , 1, 'frais annexes litige', 'O');
            exception when others then
                msg	:=  '%%% litige ' || ar_lit_ref || ' pb sur insertion frais annexe ' || to_char(SQLCODE) || ' ' || SQLERRM;
                res := 0;
                rollback;
                return;
            end;
        end if;
    end if;
    commit;

    msg := 'OK';
    res := 1;
END;
/

