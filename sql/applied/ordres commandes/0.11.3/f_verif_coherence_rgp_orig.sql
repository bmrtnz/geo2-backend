CREATE OR REPLACE PROCEDURE F_VERIF_COHERENCE_RGP_ORIG (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_fou_code GEO_FOURNI.FOU_CODE%TYPE;
    ll_nb_pal number;
    ll_grp_rgp number;
    ll_grp_orig number;
    ll_cde_nb_pal_orig_tot number;
    ll_cde_nb_pal_orig number;
    ll_ecart number;
    ls_ord_ref_orig GEO_GEST_REGROUP.ORD_REF_ORIG%TYPE;
    ls_art_ref_orig GEO_GEST_REGROUP.ART_REF_ORIG%TYPE;
    li_nb_orl_ref_rgp number;
    li_nb_cal number;
    ll_grp_rgp_old number := 0;
    li_nb_cal_old number;

    CURSOR C_RGP_PAL (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select FOu_CODE, SUM(L.CDE_NB_PAL)
        from GEO_ORDLIG L
        where    ORD_REF = ref_ordre
        group by FOU_CODE;
    CURSOR C_ORIG_PAL (ref_ordre GEO_ORDRE.ORD_REF%type, fou_code GEO_FOURNI.FOU_CODE%TYPE)
    IS
        select  distinct GRP_RGP,GRP_ORIG,ORD_REF_ORIG,CDE_NB_PAL_ORIG
        from        GEO_GEST_REGROUP
        where   ORD_REF_RGP = ref_ordre  and
        FOU_CODE_ORIG = fou_code;
    CURSOR C_RGP_ART (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select GRP_RGP,ART_REF_ORIG,count(distinct ORL_REF_RGP)
        from GEO_GEST_REGROUP
        where ORD_REF_RGP = ref_ordre
        group by   GRP_RGP,ART_REF_ORIG;
    CURSOR C_RGP_CAL (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select grp_rgp,ord_ref_orig,count(ART_REF_ORIG) from GEO_GEST_REGROUP
        where ORD_REF_RGP = ref_ordre
        group by grp_rgp,ord_ref_orig;
BEGIN
    -- correspond à f_verif_coherence_rgp_orig.pbl
    res := 0;
    msg := '';

    OPEN C_RGP_PAL (is_ord_ref);

    LOOP
        FETCH C_RGP_PAL into ls_fou_code, ll_nb_pal;
        EXIT WHEN C_RGP_PAL%notfound;

        OPEN C_ORIG_PAL (is_ord_ref, ls_fou_code);
        LOOP
            FETCH C_ORIG_PAL into ll_grp_rgp, ll_grp_orig, ls_ord_ref_orig, ll_cde_nb_pal_orig;
            EXIT WHEN C_ORIG_PAL%notfound;

            ll_cde_nb_pal_orig_tot := 0;
            If ll_cde_nb_pal_orig is not null Then
                ll_cde_nb_pal_orig_tot := ll_cde_nb_pal_orig_tot + ll_cde_nb_pal_orig;
            end if;

            close C_ORIG_PAL;
        end loop;

        ll_ecart := ll_cde_nb_pal_orig_tot - ll_nb_pal;

        If ll_ecart <> 0 Then
            msg := 'Confirmation impossible, Sur la station ' || ls_fou_code || ', il y a un écart de '  || to_char(ll_ecart) || ' palettes entre l''ordre de regroupement et les ordres regoupés';
            res := -1;
            return;
        End IF;
    end loop;
    CLOSE C_RGP_PAL;

    OPEN C_RGP_ART (is_ord_ref);
    LOOP
        FETCH C_RGP_ART into ll_grp_rgp, ls_art_ref_orig, li_nb_orl_ref_rgp;
        EXIT WHEN C_RGP_ART%NOTFOUND;

        IF li_nb_orl_ref_rgp > 1 Then
            msg := 'Confirmation impossible, L''article ' || ls_art_ref_orig || ' ne devrait apparaitre qu''une seule fois';
            res := -1;
            return;
        End If;
    end loop;
    CLOSE C_RGP_ART;

    OPEN C_RGP_CAL (is_ord_ref);
    LOOP
        FETCH C_RGP_CAL into ll_grp_rgp, ls_ord_ref_orig, li_nb_cal;
        EXIT WHEN C_RGP_CAL%NOTFOUND;

        If ll_grp_rgp_old <> ll_grp_rgp Then
            ll_grp_rgp_old := ll_grp_rgp;
            li_nb_cal_old := li_nb_cal;
        else
            If li_nb_cal_old <> li_nb_cal Then
                msg := 'Confirmation impossible, Un des ordres fils  n''a pas le même nombre d''ouverture de calibre.Veuillez vérifier et le ou tous les dupliquer';
                res := -1;
                return;
            End If;
        End If;
    end loop;
    CLOSE C_RGP_CAL;

    res := 1;
end F_VERIF_COHERENCE_RGP_ORIG;
/
