-- catv2 AR 27/06/05
-- AR 14/07/07 article
-- AR 12/10/09 corrige bug seq_lil_num remplacé par seq_orl_num
-- AR 14/11/11 ajout frais_annexes dy litiges dans geo_ord_fra

-- crée un avoir client avec les éléments de l'ordre et du litige passés en paramètres
CREATE OR REPLACE PROCEDURE F_CREE_AVOIR_TOUS_V2 (
    ar_old_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    ar_new_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    ar_lit_ref in GEO_LITLIG.LIT_REF%TYPE,
    ar_new_nordre in GEO_ORDRE.NORDRE%TYPE,
    arg_soc_code in GEO_SOCIETE.SOC_CODE%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_tyt_code varchar2(50);
    ls_ret varchar2(50);
    ls_ref_cli varchar2(50);
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
    ls_comm_interne varchar2(500);
    ls_var_ristourne varchar2(50);

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
    ld_lit_frais_annexes number;
    lnb_frais_annexes number;
    ldc_remhf_tx number;
    ldc_remsf_tx number;

    ld_datdep_fou_p timestamp;
    ld_datdep_fou_r timestamp;
BEGIN
    res := 0;
    msg := '';

   -- on récupère le type de responsable (si fournisseur on remplira la partie achat)
   -- on garde ce select seult pou vérifier que le litige exitse bien (le type de responsable est attaché à la ligne)
    begin
        select tyt_code, ref_cli, lit_frais_annexes into ls_tyt_code, ls_ref_cli, ld_lit_frais_annexes
        from geo_litige where lit_ref = ar_lit_ref;
    exception when others then
        msg := '%%% litige ' || ar_lit_ref || ' introuvable';
        return;
    end;

    -- if ls_ref_cli is null then
    --     ls_ref_cli := '';
    -- else
    --     ls_ref_cli := ls_ref_cli || ' ';
    -- end if;
    -- ls_ref_cli	:= ls_ref_cli ||  'litige ' || ar_lit_ref || '/ordre ';
    -- ls_ref_cli	:= substr(ls_ref_cli,0,63);
    ls_comm_interne	:= 'litige ' || ar_lit_ref || '/ordre ';

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
        lib_dlv, facture_avoir, comm_interne,frais_pu)
        select
        ar_new_ord_ref, soc_code, cam_code, ar_new_nordre,
        per_codeass, per_codecom, cli_ref, cli_code,
        ls_ref_cli, cen_ref, cen_code, sco_code,
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
        lib_dlv, 'A',ls_comm_interne || nordre,li_frais_pu
        from geo_ordre sel_tbl
        where sel_tbl.ord_ref = ar_old_ord_ref;
    exception when others then
        msg	:=  '%%% litige ' || ar_lit_ref || ' pb sur insertion geo_ordre erreur ' || to_char(SQLCode) || ' ' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;
        -- on crée occurences geo_ordlig
    --	select to_char(seq_orl_num.nextval,'FM0XXXX') || 'A ' into :ls_pk_ordlig from dual;
    --ls_pk_ordlig = f_pk_geo_ordlig()


    DECLARE
        cursor C_ordlig_lit is
            select ORL.orl_lig,	ORL.pal_code,ORL.pan_code,LIL.cli_nb_pal * -1,	LIL.cli_nb_col * -1,	LIL.cli_nb_pal * -1,	LIL.cli_nb_col * -1,
                    LIL.cli_pds_net * -1,LIL.cli_pds_net * -1,LIL.res_pu,	LIL.res_dev_code,LIL.res_bta_code,	LIL.res_qte * -1,
                    LIL.cli_pu,	LIL.cli_bta_code,	LIL.cli_qte * -1,	ORL.fou_code,	ORL.grp_code, 	ORL.trp_code,	ORL.valide,	ORL.lib_dlv,
                    ORL.pca_ref,ORL.pde_ref, ORL.art_ref,LIL.tie_code,ORL.propr_code, LIL.tyt_code,LIL.lcq_code, LIL.res_dev_pu,ORL.var_ristourne,ORL.remhf_tx,ORL.remsf_tx
            from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL
            where ORL.orl_ref = LIL.orl_ref
            and  (LIL.cli_qte <> 0 or ((LIL.lcq_code ='A' OR LIL.lcq_code ='B' OR LIL.lcq_code ='F'  OR LIL.tyt_code = 'F') and LIL.res_qte <> 0))
            and LIL.lit_ref = ar_lit_ref;
    begin
        OPEN C_ordlig_lit;
        loop
            FETCH 	C_ordlig_lit into
                ls_ordlig_orl_lig, ls_ordlig_pal_code, ls_ordlig_pan_code,
                ll_ordlig_cde_nb_pal, ll_ordlig_cde_nb_col, ll_ordlig_exp_nb_pal, ll_ordlig_exp_nb_col,ll_ordlig_exp_pds_brut,
                ldc_ordlig_exp_pds_net, ldc_ordlig_ach_pu,ls_ordlig_ach_dev_code, ls_ordlig_ach_bta_code,ldc_ordlig_ach_qte,
                ldc_ordlig_vte_pu,ls_ordlig_vte_bta_code, ldc_ordlig_vte_qte,ls_ordlig_fou_code, ls_ordlig_grp_code,
                ls_ordlig_trp_code, ls_ordlig_valide, ls_ordlig_lib_dlv, ls_ordlig_pca_ref, ls_ordlig_pde_ref, ls_ordlig_art_ref,
                ls_ordlig_tie_code,ls_ordlig_propr_code,ls_tyt_code,ls_lcq_code, ldc_ach_dev_pu, ls_var_ristourne,ldc_remhf_tx,ldc_remsf_tx;
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
                    /*		ldc_ordlig_ach_pu = 0
                            ls_ordlig_ach_bta_code=''
                            ldc_ordlig_ach_qte = 0*/

                    END case;

            END case;

            insert 	into geo_admin.geo_ordlig ins_tbl
            (orl_ref, ord_ref, orl_lig,
            pal_code, pan_code, cde_nb_pal,
            cde_nb_col, exp_nb_pal, exp_nb_col, exp_pds_brut,
            exp_pds_net, ach_pu, ach_dev_code, ach_bta_code,
            ach_qte, vte_pu, vte_bta_code, vte_qte,
            fou_code, grp_code, trp_code, valide, lib_dlv, pca_ref, pde_ref, art_ref,propr_code,ach_dev_pu,var_ristourne,remhf_tx,remsf_tx,frais_pu)
            VALUES (F_SEQ_ORL_SEQ, ar_new_ord_ref, ls_ordlig_orl_lig, ls_ordlig_pal_code, ls_ordlig_pan_code,
                    ll_ordlig_cde_nb_pal, ll_ordlig_cde_nb_col, ll_ordlig_exp_nb_pal, ll_ordlig_exp_nb_col,ll_ordlig_exp_pds_brut,
                    ldc_ordlig_exp_pds_net, ldc_ordlig_ach_pu,ls_ordlig_ach_dev_code, ls_ordlig_ach_bta_code,ldc_ordlig_ach_qte,
                    ldc_ordlig_vte_pu,ls_ordlig_vte_bta_code, ldc_ordlig_vte_qte,ls_ordlig_fou_code, ls_ordlig_grp_code,
                    ls_ordlig_trp_code, ls_ordlig_valide, ls_ordlig_lib_dlv, ls_ordlig_pca_ref, ls_ordlig_pde_ref, ls_ordlig_art_ref,
                    ls_ordlig_propr_code, ldc_ach_dev_pu,ls_var_ristourne,ldc_remhf_tx,ldc_remsf_tx,li_frais_pu);


        end LOOP;



        CLOSE C_ordlig_lit;
    end;





    DECLARE
        cursor C_ordlog_lit is
            select DISTINCT LOG.FOU_CODE,LOG.INSTRUCTIONS,LOG.DATDEP_FOU_P,LOG.DATDEP_FOU_R,LOG.FOU_REF_DOC,LOG.REF_LOGISTIQUE,
                LOG.REF_DOCUMENT,LOG.VALIDE,LOG.FLAG_EXPED_FOURNNI,LOG.PAL_NB_SOL,LOG.PAL_NB_PB100X120,LOG.PAL_NB_PB80X120,LOG.PAL_NB_PB60X80,LOG.DATDEP_FOU_P_YYYYMMDD,
                LOG.PLOMB, LOG.IMMATRICULATION, LOG.DETECTEUR_TEMP, LOG.CERTIF_CONTROLE, LOG.CERTIF_PHYTO, LOG.BILL_OF_LADING, LOG.CONTAINER, LOG.LOCUS_TRACE
            from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL,geo_admin.geo_ordlog  LOG
            where ORL.orl_ref = LIL.orl_ref
            and  (LIL.cli_qte <> 0 or ((LIL.lcq_code ='A' OR LIL.lcq_code ='B'  OR LIL.lcq_code ='F' OR LIL.tyt_code = 'F') and LIL.res_qte <> 0))
            and LIL.lit_ref = ar_lit_ref
            and ORL.ORD_REF = LOG.ORD_REF
            and ORL.FOU_CODE = LOG.FOU_CODE;
    BEGIN
        OPEN	 C_ordlog_lit;

        loop
            FETCH 	C_ordlog_lit into ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,ls_flag_exped_fournni,ll_pal_nb_sol,ll_pal_nb_pb100x120,ll_pal_nb_pb80x120,ll_pal_nb_pb60x80,ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_locus_trace;
            exit when C_ordlog_lit%notfound;


            insert	into geo_admin.geo_ordlog
            (orx_ref, ord_ref, fou_code,instructions,datdep_fou_p,datdep_fou_r,fou_ref_doc,ref_logistique,ref_document,valide,flag_exped_fournni,datdep_fou_p_yyyymmdd, plomb, immatriculation, DETECTEUR_TEMP, certif_controle, certif_phyto, bill_of_lading, container, locus_trace)
            VALUES (F_SEQ_ORX_SEQ(), ar_new_ord_ref, ls_fou_code,ls_instructions,ld_datdep_fou_p,ld_datdep_fou_r,ls_fou_ref_doc,ls_ref_logistique,ls_ref_document,ls_valide,'O',ls_datdep_fou_p_yyyymmdd, ls_plomb, ls_immatriculation, ls_DETECTEUR_TEMP, ls_certif_controle, ls_certif_phyto, ls_bill_of_lading, ls_container, ls_locus_trace );

            select sum(EXP_NB_PAL) into ll_pal_nb_sol
            from GEO_ORDLIG
            where 	ORD_REF   =ar_new_ord_ref and
                        FOU_CODE	 =ls_fou_code;

            update geo_ordlog set pal_nb_sol =ll_pal_nb_sol
            where 	ORD_REF   =ar_new_ord_ref and
                        FOU_CODE	 =ls_fou_code;

        end LOOP;


        CLOSE C_ordlog_lit;
    exception when others then
            msg	:=  '%%% litige ' || ar_lit_ref || ' pb sur insertion geo_ordlig erreur ' || to_char(SQLCode) || ' ' || SQLERRM;
            res := 0;
            rollback;
            return;
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
    LIL.cli_nb_col * -1, LIL.cli_nb_pal * -1, LIL.cli_nb_col * -1, LIL.cli_pds_net * -1,
    LIL.cli_pds_net * -1, decode(LIL.tyt_code,'F',LIL.res_pu,0), ORL.ach_dev_code, decode(LIL.tyt_code,'F',LIL.res_bta_code,''),
    decode(LIL.tyt_code,'F',LIL.res_qte * -1,0), LIL.cli_pu, LIL.cli_bta_code, LIL.cli_qte * -1,
    ORL.fou_code, ORL.grp_code, ORL.trp_code, ORL.valide, ORL.lib_dlv, ORL.pca_ref, ORL.pde_ref, ORL.art_ref,decode(LIL.tyt_code,'F',LIL.tie_code,ORL.propr_code)
    from geo_admin.geo_ordlig ORL, geo_admin.geo_litlig LIL
    where ORL.orl_ref = LIL.orl_ref
    and  (LIL.cli_qte <> 0 or (LIL.tyt_code = 'F' and LIL.res_qte <> 0))
    and LIL.lit_ref = :ar_lit_ref;
    */
    --	and not (LIL.cli_qte = 0 and (LIL.tyt_code <> 'F' or (LIL.tyt_code = 'F' and LIL.res_qte = 0)))
    --	and LIL.lit_ref = :ar_lit_ref;
	-- on ne selectionne pas les lignes qui ont une qté client nulle et (qui ne sont pas des fournisseurs ou qui sont fournisseurs sans qté)
	-- bonjour la syntaxe - amis poètes bonsoir

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
