CREATE OR REPLACE PROCEDURE GENERE_LITIGE_AUTOM (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    prompt_continue in varchar2 := '',
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    li_nb_litige_cause number;
    li_nb_litige_conseq number;
    li_nb_ordre number;
    ldt_x timestamp;
    ls_lca_code varchar2(32);
    ls_lcq_code varchar2(32);
    ls_tyt_code varchar2(32);
    ls_tie_code varchar2(32);
BEGIN
    -- correspond à f_bon_a_facturer.pbl deuxième partie
    res := 0;
    msg := '';

    SELECT
        count(DISTINCT LL.lca_code),
        count(DISTINCT LL.lcq_code)
    INTO
        li_nb_litige_cause,
        li_nb_litige_conseq
    FROM
        GEO_LITIGE L ,
        GEO_LITLIG LL
    WHERE
        L.LIT_REF = LL.LIT_REF
        AND
    L.ORD_REF_ORIGINE = is_ord_ref;

    SELECT
        count(*)
    INTO
        li_nb_ordre
    FROM
        GEO_ORDRE O1
    WHERE
        O1.CODE_CHARGEMENT IS NOT NULL
        AND
        EXISTS (
        SELECT
            1
        FROM
            GEO_ORDRE O2
        WHERE
            O1.CODE_CHARGEMENT = O2.CODE_CHARGEMENT
            AND
            O1.DEPDATP = O2.DEPDATP
            AND
            O2.ORD_REf =is_ord_ref
            AND
            O1.ORD_REF <> O2.ORD_REF
            AND

    O1.CLI_REF = O2.CLI_REF)
        AND

    NOT EXISTS (
        SELECT
            1
        FROM
            GEO_LITIGE L1
        WHERE
            L1.ORD_REF_ORIGINE = O1.ORD_REF);

    IF li_nb_ordre > 0
    AND li_nb_litige_cause = 1
    AND li_nb_litige_conseq = 1 THEN

        if prompt_continue is null then
            msg := 'Voulez-vous confimer la création des litiges pour les autres ordres avec le même code chargement ?';
            res := 2;
            return;
        end if;

        IF prompt_continue = 'O' THEN

            SELECT
                sysdate
            INTO
                ldt_x
            FROM
                dual;

            SELECT
                DISTINCT LL.lca_code,
                LL.lcq_code,
                LL.tyt_code,
                LL.tie_code
            INTO
                ls_lca_code,
                ls_lcq_code,
                ls_tyt_code,
                ls_tie_code
            FROM
                GEO_LITIGE L ,
                GEO_LITLIG LL
            WHERE
                L.LIT_REF = LL.LIT_REF
                AND
            L.ORD_REF_ORIGINE = is_ord_ref;

            DECLARE
                cursor cur_litige is

                SELECT
                    O1.ORD_REF,
                    O1.DEPDATP,
                    O1.REF_CLI,
                    OL1.ORL_REF
                FROM
                    GEO_ORDRE O1,
                    GEO_ORDLIG OL1
                WHERE
                    O1.ORD_REF = OL1.ORD_REF
                    AND

                O1.CODE_CHARGEMENT IS NOT NULL
                    AND

                OL1.EXP_NB_COL > 0
                    AND

                EXISTS (
                    SELECT
                        1
                    FROM
                        GEO_ORDRE O2
                    WHERE
                        O1.CODE_CHARGEMENT = O2.CODE_CHARGEMENT
                        AND

                O1.CLI_REF = O2.CLI_REF
                        AND

                O1.DEPDATP = O2.DEPDATP
                        AND

                        O2.ORD_REf =is_ord_ref
                        AND

                O1.ORD_REF <> O2.ORD_REF)
                    AND

                NOT EXISTS (
                    SELECT
                        1
                    FROM
                        GEO_LITIGE L1
                    WHERE
                        L1.ORD_REF_ORIGINE = O1.ORD_REF);
                ls_ord_ref_lie varchar2(32);
                ls_ord_ref_lie_old varchar2(32);
                ldt_depdatp timestamp;
                ls_ref_cli varchar2(32);
                ls_orl_ref_lie varchar2(32);
                ll_lit_ref number;
                ls_lit_ref_lie varchar2(6);

                ll_exp_nb_pal GEO_ORDLIG.EXP_NB_PAL%type;
                ll_exp_nb_col GEO_ORDLIG.EXP_NB_COL%type;
                ld_exp_pds_net GEO_ORDLIG.EXP_PDS_NET%type;
                ldc_vte_pu GEO_ORDLIG.VTE_PU%type;
                ls_vte_bta_code GEO_ORDLIG.VTE_BTA_CODE%type;
                ldc_vte_qte GEO_ORDLIG.VTE_QTE%type;
                ldc_ach_pu GEO_ORDLIG.ACH_PU%type;
                ls_ach_bta_code GEO_ORDLIG.ACH_BTA_CODE%type;
                ldc_ach_qte GEO_ORDLIG.ACH_QTE%type;
                ldc_ach_dev_taux GEO_ORDLIG.ACH_DEV_TAUX%type;
                ls_ach_dev_code GEO_ORDLIG.ACH_DEV_CODE%type;
                ldc_ach_dev_pu GEO_ORDLIG.ACH_DEV_PU%type;
                ls_propr_code GEO_ORDLIG.PROPR_CODE%type;

                ldc_res_qte GEO_ORDLIG.ACH_QTE%type;
                ll_res_nb_pal GEO_ORDLIG.EXP_NB_PAL%type;
                ld_res_pds_net GEO_ORDLIG.EXP_PDS_NET%type;
                ll_res_nb_col GEO_ORDLIG.EXP_NB_COL%type;
                ls_tie_code_all GEO_ORDLIG.PROPR_CODE%type;

                ldc_res_pu GEO_ORDLIG.ACH_PU%type;
                ldc_res_dev_pu GEO_ORDLIG.ACH_DEV_PU%type;
                ls_res_bta_code GEO_ORDLIG.ACH_BTA_CODE%type;
                ls_res_dev_code GEO_ORDLIG.ACH_DEV_CODE%type;
                ldc_res_dev_taux GEO_ORDLIG.ACH_DEV_TAUX%type;

                ls_lil_ref varchar2(32);
            begin

                OPEN cur_litige;
                loop

                    FETCH cur_litige
                    INTO
                        ls_ord_ref_lie,
                        ldt_depdatp,
                        ls_ref_cli,
                        ls_orl_ref_lie;
                    EXIT WHEN cur_litige%notfound;

                    IF ls_ord_ref_lie_old = 'hakanumatata'
                    OR ls_ord_ref_lie_old <> ls_ord_ref_lie THEN

                        SELECT
                            seq_lit_num.nextval
                        INTO
                            ll_lit_ref
                        FROM
                            dual;

                        ls_lit_ref_lie := to_char(ll_lit_ref,'000000');

                        -- creation litige

                        INSERT
                            INTO
                            GEO_LITIGE (LIT_REF,
                            ORD_REF_ORIGINE,
                            FL_FOURNI_CLOS,
                            LIT_FRAIS_ANNEXES,
                            LIT_DATE_CREATION,
                            LIT_DATE_ORIGINE,
                            LIT_DATE_RESOLUTION,
                            FL_ENCOURS,
                            REF_CLI,
                            NUM_VERSION)
                        VALUES (ls_lit_ref_lie,
                        ls_ord_ref_lie,
                        'N',
                        0,
                        ldt_x,
                        ldt_depdatp,
                        ldt_x,
                        'O',
                        ls_ref_cli,
                        2);
                    END IF;

                    ls_ord_ref_lie_old := ls_ord_ref_lie;

                    SELECT
                        EXP_NB_PAL,
                        EXP_NB_COL,
                        EXP_PDS_NET,
                        VTE_PU,
                        VTE_BTA_CODE,
                        VTE_QTE,
                        ACH_PU,
                        ACH_BTA_CODE,
                        ACH_QTE,
                        ACH_DEV_TAUX,
                        ACH_DEV_CODE,
                        ACH_DEV_PU,
                        PROPR_CODE

                    INTO
                        ll_exp_nb_pal,
                        ll_exp_nb_col,
                        ld_exp_pds_net,
                        ldc_vte_pu,
                        ls_vte_bta_code,
                        ldc_vte_qte,
                        ldc_ach_pu,
                        ls_ach_bta_code,
                        ldc_ach_qte,
                        ldc_ach_dev_taux,
                        ls_ach_dev_code,
                        ldc_ach_dev_pu,
                        ls_propr_code
                    FROM
                        GEO_ORDLIG
                    WHERE
                        ORD_REF = ls_ord_ref_lie
                        AND
                    ORL_REF = ls_orl_ref_lie;

                    SELECT
                        F_SEQ_LIL_NUM()
                    INTO
                        ls_lil_ref
                    FROM
                        dual;

                    IF ls_tyt_code = 'F' THEN
                        ldc_res_qte := ldc_ach_qte;
                        ll_res_nb_pal := ll_exp_nb_pal;
                        ld_res_pds_net := ld_exp_pds_net;
                        ll_res_nb_col := ll_exp_nb_col;
                        ls_tie_code_all := ls_propr_code;
                    ELSE
                        ls_tie_code_all := ls_tie_code;
                    END IF;

                    ldc_res_pu := ldc_ach_pu;
                    ldc_res_dev_pu := ldc_ach_dev_pu;
                    ls_res_bta_code := ls_ach_bta_code;
                    ls_res_dev_code := ls_ach_dev_code;
                    ldc_res_dev_taux := ldc_ach_dev_taux;

                    INSERT
                        INTO
                        GEO_LITLIG (LIL_REF,
                        LIT_REF,
                        ORL_REF,
                        CLI_NB_PAL,
                        CLI_NB_COL,
                        CLI_PDS_NET,
                        CLI_PU,
                        CLI_BTA_CODE,
                        CLI_QTE,
                        RES_NB_PAL,
                        RES_NB_COL,
                        RES_PDS_NET,
                        RES_PU,
                        RES_BTA_CODE,
                        RES_QTE,
                        VALIDE,
                        LCA_CODE,
                        LCQ_CODE,
                        TYT_CODE,
                        TIE_CODE,
                        RES_DEV_TAUX,
                        RES_DEV_PU,
                        RES_DEV_CODE,
                        ORL_LIT,
                        IND_ENV_INC,
                        CLI_IND_FORF,
                        RES_IND_FORF)
                    VALUES (ls_lil_ref,
                    ls_lit_ref_lie,
                    ls_orl_ref_lie,
                    ll_exp_nb_pal,
                    ll_exp_nb_col,
                    ld_exp_pds_net,
                    ldc_vte_pu,
                    ls_vte_bta_code,
                    ldc_vte_qte,
                    ll_res_nb_pal,
                    ll_res_nb_col,
                    ld_res_pds_net,
                    ldc_res_pu,
                    ls_res_bta_code,
                    ldc_res_qte,
                    'O',
                    ls_lca_code,
                    ls_lcq_code,
                    ls_tyt_code,
                    ls_tie_code_all,
                    ldc_res_dev_taux,
                    ldc_res_dev_pu,
                    ls_res_dev_code,
                    '01',
                    'N',
                    'N',
                    'N');

                    FETCH cur_litige
                    INTO
                        ls_ord_ref_lie,
                        ldt_depdatp,
                        ls_ref_cli,
                        ls_orl_ref_lie;
                end LOOP;
                CLOSE cur_litige;
            end;
        END IF;
    end if;

    res := 1;
    msg := 'OK';

end GENERE_LITIGE_AUTOM;
/
