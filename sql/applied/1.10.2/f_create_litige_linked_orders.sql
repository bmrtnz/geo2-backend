CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREATE_LITIGE_LINKED_ORDERS(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    li_nb_ordre number;
    li_nb_litige_cause number;
    li_nb_litige_conseq number;
    li_ret number;
    ll_lit_ref number;
    ll_exp_nb_pal number;
    ll_exp_nb_col number;
    ld_exp_pds_net number;
    ldc_vte_pu number;
    ldc_vte_qte number;
    ldc_ach_pu number;
    ldc_ach_qte number;
    ldc_ach_dev_taux number;
    ldc_ach_dev_pu number;
    li_i  number;
    ldc_res_pu number := 0;
    ldc_res_qte number := 0 ;
    ldc_res_dev_taux number := 0;
    ldc_res_dev_pu number := 0 ;
    ll_res_nb_pal number :=0;
    ll_res_nb_col number := 0;
    ld_res_pds_net number :=0 ;

    ldt_x timestamp;
    ldt_depdatp timestamp;

    ls_ord_ref_lie varchar2(50);
    ls_orl_ref_lie varchar2(50);
    ls_lca_code varchar2(50);
    ls_lcq_code varchar2(50);
    ls_lit_ref_lie varchar2(50);
    ls_ref_cli varchar2(50);
    ls_ord_ref_lie_old varchar2(50) :='hakanumatata';




    ls_vte_bta_code varchar2(50);
    ls_ach_bta_code varchar2(50);
    ls_ach_dev_code varchar2(50);
    ls_lil_ref varchar2(50);
    ls_tyt_code varchar2(50);
    ls_tie_code varchar2(50);
    ls_res_bta_code varchar2(50) :='';
    ls_res_dev_code varchar2(50) :='';

    CURSOR cur_litige IS
        select O1.ORD_REF, O1.DEPDATP,O1.REF_CLI, OL1.ORL_REF
        from GEO_ORDRE O1, GEO_ORDLIG OL1
        where 	O1.ORD_REF = OL1.ORD_REF 				AND
                    O1.CODE_CHARGEMENT IS NOT NULL  	AND
                            exists (select 1
                                from GEO_ORDRE O2
                                where O1.CODE_CHARGEMENT  = O2.CODE_CHARGEMENT and
                                            O1.DEPDATP = O2.DEPDATP and
                                            O2.ORD_REf = is_ord_ref and
                                                O1.ORD_REF <> O2.ORD_REF) and
                    not exists (select 1
                            from GEO_LITIGE L1
                            where L1.ORD_REF_ORIGINE=O1.ORD_REF);
BEGIN
    res := 0;
    msg := '';


    select sysdate into ldt_x from dual;

    select distinct LL.lca_code, LL.lcq_code, LL.tyt_code,LL.tie_code
    into ls_lca_code,ls_lcq_code, ls_tyt_code,ls_tie_code
    from GEO_LITIGE L , GEO_LITLIG LL
    where	 L.LIT_REF = LL.LIT_REF and
                    L.ORD_REF_ORIGINE = is_ord_ref;


    -- open cur_litige;
    for r in cur_litige loop
        If ls_ord_ref_lie_old ='hakanumatata' or ls_ord_ref_lie_old <> r.ord_ref Then

                select seq_lit_num.nextval into ll_lit_ref from dual;
                ls_lit_ref_lie	:= to_char(ll_lit_ref);

                --creation litige
                insert into GEO_LITIGE (LIT_REF,ORD_REF_ORIGINE,FL_FOURNI_CLOS,LIT_FRAIS_ANNEXES,LIT_DATE_CREATION, LIT_DATE_ORIGINE,LIT_DATE_RESOLUTION,FL_ENCOURS,REF_CLI,NUM_VERSION)
                VALUES (ls_lit_ref_lie,r.ord_ref,'N',0,ldt_x,r.DEPDATP,ldt_x,'O',r.REF_CLI,2);

        End IF;
        ls_ord_ref_lie_old  := r.ord_ref;

        select EXP_NB_PAL,EXP_NB_COL,EXP_PDS_NET, VTE_PU,VTE_BTA_CODE,VTE_QTE,ACH_PU,ACH_BTA_CODE,ACH_QTE,ACH_DEV_TAUX,ACH_DEV_CODE,ACH_DEV_PU
        into ll_exp_nb_pal,ll_exp_nb_col,ld_exp_pds_net,ldc_vte_pu, ls_vte_bta_code,ldc_vte_qte,ldc_ach_pu, ls_ach_bta_code,ldc_ach_qte,ldc_ach_dev_taux,ls_ach_dev_code,ldc_ach_dev_pu
        from GEO_ORDLIG
        where ORD_REF= r.ord_ref and
                    ORL_REF = r.ORL_REF;

        select F_SEQ_LIL_NUM() into ls_lil_ref from dual;

        If 	ls_tyt_code = 'F' then
            ldc_res_qte := ldc_ach_qte;


            ll_res_nb_pal := ll_exp_nb_pal;
            ld_res_pds_net:= ld_exp_pds_net;
            ll_res_nb_col := ll_exp_nb_col;
        END IF;
        ldc_res_pu := ldc_ach_pu;
        ldc_res_dev_pu := ldc_ach_dev_pu;
        ls_res_bta_code :=ls_ach_bta_code;
        ls_res_dev_code :=ls_ach_dev_code;
        ldc_res_dev_taux :=ldc_ach_dev_taux;


        insert into GEO_LITLIG (LIL_REF,LIT_REF,ORL_REF,CLI_NB_PAL,CLI_NB_COL,CLI_PDS_NET,CLI_PU,CLI_BTA_CODE,CLI_QTE,RES_NB_PAL,RES_NB_COL,RES_PDS_NET,RES_PU,RES_BTA_CODE,RES_QTE,VALIDE,LCA_CODE,LCQ_CODE,TYT_CODE,TIE_CODE,RES_DEV_TAUX,RES_DEV_PU,RES_DEV_CODE,ORL_LIT,IND_ENV_INC,CLI_IND_FORF,RES_IND_FORF)
        VALUES (ls_lil_ref,ls_lit_ref_lie,r.ORL_REF,ll_exp_nb_pal,ll_exp_nb_col,ld_exp_pds_net,ldc_vte_pu,ls_vte_bta_code,ldc_vte_qte,ll_res_nb_pal,ll_res_nb_col,ld_res_pds_net,ldc_res_pu,ls_res_bta_code,ldc_res_qte,'O',ls_lca_code,ls_lcq_code,ls_tyt_code,ls_tie_code,ldc_res_dev_taux,ldc_res_dev_pu,ls_res_dev_code,'01','N','N','N');

    end loop;

    res := 1;
    commit;
exception when others then
    msg := 'Erreur lors de la création ordre des litiges ordre lié ' || SQLERRM;
end;
/

