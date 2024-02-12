-- F_CREATE_LOGISTIQUE_RETOUR_PALOX
CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREATE_LGT_RETOUR_PALOX(
    arg_ord_ref IN varchar2,
    arg_fourni IN varchar2,
    arg_bon_retour IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2,
    ls_orx_ref IN OUT varchar2
)
AS
    ls_ORD_REF					 varchar2(50) := arg_ord_ref;
    ls_FOU_CODE				 varchar2(50);
    ls_DATDEP_FOU_P varchar2(50);
    ls_DATDEP_GRP_P			 varchar2(50);
    ls_DATDEP_FOU_P_YYYYMMDD varchar2(50);
    ls_DATDEP_FOU_R			 varchar2(50);
    ls_FOU_REF_DOC				 varchar2(50);
BEGIN
    res := 0;
    msg := '';

    select F_SEQ_ORX_SEQ() into ls_ORX_REF FROM DUAL;
    --ls_ORX_REF = SQLCA.F_SEQ_ORX_SEQ()
    --ls_ORX_REF = ''
    ls_FOU_CODE := arg_fourni;
    ls_DATDEP_FOU_P := CURRENT_DATE;
    ls_DATDEP_GRP_P := CURRENT_DATE;
    ls_DATDEP_FOU_P_YYYYMMDD	:= to_char(CURRENT_DATE, 'yyyymmdd');
    ls_DATDEP_FOU_R := CURRENT_DATE;
    ls_FOU_REF_DOC := arg_bon_retour;

    INSERT INTO GEO_ORDLOG (
        ORX_REF, ORD_REF, FOU_CODE, DATDEP_FOU_P, DATDEP_GRP_P, DATDEP_FOU_P_YYYYMMDD, DATDEP_FOU_R, FOU_REF_DOC, REF_LOGISTIQUE, VALIDE, FLAG_EXPED_FOURNNI, FLAG_EXPED_GROUPA, GRP_PU_VISIBLE, TRP_PU_VISIBLE, ACK_FOURNI, ACK_GROUPA, ACK_TRANSP, PAL_NB_SOL, TOT_CDE_NB_PAL, TOT_EXP_NB_PAL
    ) VALUES (
        ls_ORX_REF, ls_ORD_REF, ls_FOU_CODE, ls_DATDEP_FOU_P, ls_DATDEP_GRP_P, ls_DATDEP_FOU_P_YYYYMMDD, ls_DATDEP_FOU_R, ls_FOU_REF_DOC, 'Retour palox', 'O', 'O', 'N', 'N', 'N', 'N', 'N', 'N', '0', '0', '0'
    );

    commit;
    res := 1;

exception when others then
    res := 0;
    msg := '%%% Erreur à la création de la ligne logistique : ' || SQLERRM;
end;
/

