CREATE OR REPLACE PROCEDURE F_CALCUL_MARGE_PREVI (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    result OUT number,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ld_vte_ord_tot number;
    ld_rem_ord_tot number;
    ld_res_ord_tot number;
    ld_frd_ord_tot number;
    ld_ach_ord_tot number;
    ld_trp_ord_tot number;
    ld_trs_ord_tot number;
    ld_crt_ord_tot number;
    ld_fad_ord_tot number;
    ld_totfrd_net number;
    ld_totpereq number;
    ldc_ret number;
BEGIN
    -- correspond à f_calcul_marge_previ.pbl
    msg := '';
    res := 0;

    F_CALCUL_MARGE(is_ord_ref, res, msg);
    if res = 1 then
        f_calcul_perequation(is_ord_ref, is_soc_code, res, msg);
        if res = 1 then
            select totvte, totrem, totres, totfrd, totach, tottrp, tottrs, totcrt, totfad,totfrd_net,totpereq
            into ld_vte_ord_tot, ld_rem_ord_tot, ld_res_ord_tot, ld_frd_ord_tot, ld_ach_ord_tot, ld_trp_ord_tot, ld_trs_ord_tot, ld_crt_ord_tot, ld_fad_ord_tot, ld_totfrd_net, ld_totpereq
            from geo_ordre
            where ord_ref = is_ord_ref;

            If ld_vte_ord_tot = 0 then
                result := 0;
            Else
                If ld_totfrd_net is not null Then
                    If ld_totpereq is not null Then
                        If ld_totpereq < 0 Then
                            ld_frd_ord_tot := ld_totfrd_net;
                        End IF;
                    End If;
                End If;

                result := round( (( ld_vte_ord_tot - ld_rem_ord_tot + ld_res_ord_tot - ld_frd_ord_tot - ld_ach_ord_tot - ld_trp_ord_tot - ld_trs_ord_tot - ld_crt_ord_tot - ld_fad_ord_tot)/ld_vte_ord_tot)*100  ,2);
            End If;
        else
            msg := 'problème sur le calcul de la marge sur variété club : ' || msg;
        end if;
    else
        msg := 'problème sur le calcul de la marge prévisionnelle : ' || msg;
    end if;

    res := 1;
end F_CALCUL_MARGE_PREVI;
/

