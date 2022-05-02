CREATE OR REPLACE PROCEDURE F_CTRL_COHERENCE_ORIG_ART_STA (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_mess CLOB := 'Le(s) point(s) suivant(s) peut/peuvent gêner la confirmation de commande:~r';
    lb_ori_art_stat_ok boolean := true;

    ls_art_ref GEO_ORDLIG.ART_REF%TYPE;
    ls_var_code GEO_ARTICLE.VAR_CODE%TYPE;
    ls_art_ori_code GEO_ARTICLE.ORI_CODE%TYPE;
    ls_propr_code GEO_ORDLIG.PROPR_CODE%TYPE;

    CURSOR C_coh_art_fou (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select
            OL.ART_REF,
            A.VAR_CODE,
            A.ORI_CODE,
            OL.PROPR_CODE
        from
            geo_ordre O,
            geo_ordlig OL,
            geo_article A,
            geo_fourni F
        where
                O.ORD_REF  = ref_ordre AND
                OL.ORD_REF = O.ORD_REF AND
                A.ART_REF = OL.ART_REF AND
                F.FOU_CODE = OL.PROPR_CODE AND
                A.ORI_CODE <> 'F' AND
                F.PAY_CODE = 'FR' AND
                F.FOU_CODE <> '-' AND
            not exists (select 1  from GEO_FOURNI
                        where GEO_FOURNI.COMPTE_COMPTA ='BW' and
                                GEO_FOURNI.VALIDE='O' and
                                OL.PROPR_CODE = GEO_FOURNI.FOU_CODE) and
                A.ESP_CODE not in  ('EMBALL','PRESTA','TRANSP', 'PRUNE') and
                A.VAR_CODE <> 'ROCHAS' and
                A.VAR_CODE <> 'BLACKS';
BEGIN
    -- correspond à f_ctrl_coherence_orig_article_station.pbl
    msg := '';
    res := 0;

    OPEN C_coh_art_fou (is_ord_ref);
    LOOP
        FETCH C_coh_art_fou INTO ls_art_ref, ls_var_code, ls_art_ori_code, ls_propr_code;
        EXIT WHEN C_coh_art_fou%notfound;

        lb_ori_art_stat_ok := False;
        ls_mess := ls_mess || 'l''article ' || ls_art_ref || '(' || ls_var_code || ') d''origine ' || ls_art_ori_code || ' ne peut provenir du propriétaire français (' || ls_propr_code || ') ~r';
    end loop;
    CLOSE C_coh_art_fou;

    If lb_ori_art_stat_ok = False Then
        msg := ls_mess;
        res := 2;
        return;
    End If;

    res := 1;
end F_CTRL_COHERENCE_ORIG_ART_STA;
/

