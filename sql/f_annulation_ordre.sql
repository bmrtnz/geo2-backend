CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_ANNULATION_ORDRE (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_motif IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ls_comment GEO_ORDRE.COMM_INTERNE%TYPE;
    ls_ref_client GEO_ORDRE.REF_CLI%TYPE;
BEGIN
    -- correspond à w_annulation_motif_on_b_appliquer.pbl
    res := 0;
    msg := '';

    if (arg_motif is not null and trim(arg_motif) is not null) then
        select nordre into ls_nordre from GEO_ORDRE where ORD_REF = arg_ord_ref;

        ls_comment := 'ANNULE CAUSE ' || arg_motif;
        ls_ref_client := 'ORDRE ANNULE O/' || ls_nordre;

        begin
            UPDATE GEO_ORDRE SET
                 REF_CLI = ls_ref_client,
                 INC_CODE = 'EXW',
                 COMM_INTERNE = ls_comment,
                 TRP_DEV_PU = 0,
                 FRAIS_PLATEFORME =0,
                 TOTFRAIS_PLATEFORME = 0,
                 PAL_NB_SOLTRANS = 0,
                 TOTFOB = 0,
                 TOT_EXP_NB_PAL =0,
                 TOT_CDE_NB_PAL = 0,
                 TOTFAD =0,
                 PIED_HT_INTERFEL_1 =0,
                 PIED_HT_INTERFEL_2 =0,
                 PIED_HT_INTERFEL_0 =0,
                 PIED_HT_INTERFEL =0,
                 PIED_INTERFEL_2 =0,
                 PIED_INTERFEL_1 =0,
                 PIED_INTERFEL_0 =0,
                 PIED_INTERFEL =0,
                 PAL_NB_PB60x80 = 0,
                 PAL_NB_PB80x120 = 0,
                 PAL_NB_PB100x120 = 0,
                 PAL_NB_SOL = 0,
                 REM_SF_TX_MDD = 0,
                 PIED_TVA_2 = 0,
                 PIED_TVA_1 = 0,
                 TAUX_TVA_2 = 0,
                 TAUX_TVA_1 = 0,
                 TAUX_CTIFL = 0,
                 PIED_HT_FINAL_2 = 0,
                 PIED_HT_FINAL_1 = 0,
                 PIED_HT_FINAL_0 = 0,
                 PIED_CTIFL_2 = 0,
                 PIED_CTIFL_1 = 0,
                 PIED_CTIFL_0 = 0,
                 PIED_HT_NET_2 = 0,
                 PIED_HT_NET_1 = 0,
                 PIED_HT_NET_0 = 0,
                 PIED_RIST_2 = 0,
                 PIED_RIST_1 = 0,
                 PIED_RIST_0 = 0,
                 PIED_HT_CTIFL_2 = 0,
                 PIED_HT_CTIFL_1 = 0,
                 PIED_HT_CTIFL_0 = 0,
                 PIED_HT_RIST_2 = 0,
                 PIED_HT_RIST_1 = 0,
                 PIED_HT_RIST_0 = 0,
                 PIED_HT_BRUT_2 = 0,
                 PIED_HT_BRUT_1 = 0,
                 PIED_HT_BRUT_0 = 0,
                 PIED_TTC = 0,
                 PIED_TVA = 0,
                 PIED_HT_FINAL =0,
                 PIED_CTIFL = 0,
                 PIED_HT_NET =0,
                 PIED_RIST = 0,
                 PIED_HT_CTIFL = 0,
                 PIED_HT_RIST =0,
                 PIED_HT_BRUT=0,
                 TOT_REMHF = 0,
                 TOT_REMSF = 0,
                 TOT_HT_BRUT = 0,
                 TOTPDSBRUT = 0,
                 TOTPDSNET = 0,
                 TOTCOL = 0,
                 TOTPAL =0,
                 FLBAF = 'O',
                 TOTCRT = 0,
                 TOTTRS= 0 ,
                 TOTTRP  = 0,
                 TOTMOB = 0,
                 TOTACH = 0,
                 TOTFRD = 0,
                 TOTRES = 0,
                 TOTREM = 0,
                 TOTVTE = 0,
                 REMHF_TX = 0,
                 REMSF_TX = 0,
                 TRP_PU = 0,
                 FLANNUL = 'O'
            WHERE ORD_REF = arg_ord_ref;

            commit;
        exception when others then
            msg := 'erreur sur mise à jour table GEO_ORDRE : ' || SQLERRM;
            rollback;
            return;
        end;

        begin
            update GEO_ORDLIG
            SET PAL_NB_COL = 0,
                CDE_NB_COL = 0,
                CDE_NB_PAL = 0,
                EXP_NB_PAL = 0,
                EXP_NB_COL = 0,
                EXP_PDS_BRUT = 0,
                EXP_PDS_NET = 0,
                ACH_QTE = 0,
                VTE_QTE = 0,
                ACH_PU = 0,
                ACH_DEV_PU = 0,
                VTE_PU = 0,
                CDE_PDS_BRUT = 0,
                CDE_PDS_NET = 0,
                TOTVTE = 0,
                TOTREM = 0,
                TOTRES = 0,
                TOTFRD = 0,
                TOTACH = 0,
                TOTMOB = 0,
                TOTTRP = 0,
                TOTTRS = 0,
                TOTCRT = 0,
                LIG_HT_BRUT_0 = 0,
                LIG_HT_BRUT_1 = 0,
                LIG_HT_BRUT_2 = 0,
                LIG_HT_RIST_0 = 0,
                LIG_HT_RIST_1 = 0,
                LIG_HT_RIST_2 = 0,
                LIG_HT_CTIFL_0 = 0,
                LIG_HT_CTIFL_1 = 0,
                LIG_HT_CTIFL_2 = 0,
                LIG_HT_CTIFLRIST_0 = 0,
                LIG_HT_CTIFLRIST_1 = 0,
                LIG_HT_CTIFLRIST_2 = 0,
                HT_BRUT = 0,
                LIG_REMSF = 0,
                LIG_REMHF = 0,
                EXP_NB_PAL_INTER = 0,
                REMSF_TX = 0,
                REMHF_TX =0,
                TOTREMSF = 0,
                VTE_PU_NET = 0,
                LIG_HT_NET_0 = 0,
                LIG_HT_NET_1 = 0,
                LIG_HT_NET_2 = 0,
                LIG_HT_NET_3 = 0,
                LIG_HT_INTERFEL_0 = 0,
                LIG_HT_INTERFEL_1 = 0,
                LIG_HT_INTERFEL_2 = 0,
                LIG_HT_INTERFELRIST_0 = 0,
                LIG_HT_INTERFELRIST_1 = 0,
                LIG_HT_INTERFELRIST_2 = 0,
                TOTFAD = 0,
                EXP_PAL_NB_COL = 0,
                PAL_NB_PALINTER = 0,
                DEMIPAL_IND = 0,
                TOTFRAIS_PLATEFORME = 0,
                LIG_HT_INTERFEL_IMP_0 = 0,
                LIG_HT_INTERFEL_IMP_1 = 0,
                LIG_HT_INTERFEL_IMP_2 = 0,
                LIG_HT_INTERFELRIST_IMP_0 = 0,
                LIG_HT_INTERFELRIST_IMP_1 = 0,
                LIG_HT_INTERFELRIST_IMP_2 = 0,
                NB_COLIS_MANQUANT = 0
            WHERE ORD_REF = arg_ord_ref;

            commit;
        exception when others then
            msg := 'erreur sur mise à jour table GEO_ORDLIG : ' || SQLERRM;
            rollback;
            return;
        end;

        begin
            update GEO_ORDLOG
            SET FLAG_EXPED_FOURNNI ='O',
                PAL_NB_SOL = 0,
                PAL_NB_PB100x120 = 0,
                PAL_NB_PB80x120 = 0,
                PAL_NB_PB60x80 = 0,
                TOT_CDE_NB_PAL = 0,
                TOT_EXP_NB_PAL =0
            WHERE ORD_REF = arg_ord_ref;

            commit;
        exception when others then
            msg := 'erreur sur mise à jour table GEO_ORDLOG : ' || SQLERRM;
            rollback;
            return;
        end;

        F_CALCUL_MARGE(arg_ord_ref, res, msg);
        if (res <> 1) then
            return;
        end if;

        begin
            UPDATE GEO_ORDRE SET FLANNUL = 'O' 	WHERE ORD_REF = arg_ord_ref;

            commit;
        exception when others then
            msg := 'erreur sur mise à jour table GEO_ORDRE.FLANNUL ' || SQLERRM;
            rollback;
            return;
        end;

        res := 1;
    else
        msg := 'Le motif d''annulation est obligatoire !';
    end if;

end F_ANNULATION_ORDRE;
/

