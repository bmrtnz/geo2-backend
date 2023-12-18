CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_BON_A_FACTURER_PREPARE (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    li_ret number;
BEGIN
    -- correspond à f_bon_a_facturer.pbl première partie
    res := 0;
    msg := '';

    declare
        cc_previ_result number;
    begin
        f_calcul_marge_previ(arg_ord_ref, arg_soc_code, cc_previ_result, res, msg);
        if res <> 1 then return; end if;
        li_ret := res;
    end;

    F_VERIF_ORDRE_WARNING(arg_ord_ref, arg_soc_code, res, msg);
    if (msg <> 'OK') then
        if (instr(msg, '%%%') <> 0) then
            msg := msg || '\r' || 'La validation est refusée, corrigez les erreurs';
            res := 0;
            return;
        else
            msg := msg || '~rVoulez-vous valider le bon à facturer malgré tout ?';
            res := 2;
            return;
        end if;
    end if;

    res := 1;

end F_BON_A_FACTURER_PREPARE;
/

