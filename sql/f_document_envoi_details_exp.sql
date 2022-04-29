CREATE OR REPLACE PROCEDURE F_DOCUMENT_ENVOI_DETAILS_EXP(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ll_count number;
    ls_vente_comm varchar(10);
BEGIN
    -- correspond à f_document_envoi_details_exp.pbl
    msg := '';
    res := 0;

    select count(0) into ll_count from geo_ordlig where ord_ref = is_ord_ref and (ach_pu is null);

    if ll_count > 0 then
        select VENTE_COMMISSION into ls_vente_comm from geo_ordre where ord_ref = is_ord_ref;
        if ls_vente_comm <> 'O' then
            msg := 'envoi de détail bloqué, tous les prix d''achat doivent être saisis';
            return;
        end if;
    end if;

    If is_soc_code <>'QUP' and is_soc_code <>'IMP' Then
        select count(0) into ll_count from geo_ordlog where ord_ref = is_ord_ref and flag_exped_fournni <> 'O';
        if ll_count > 0 then
            msg := 'envoi de détail bloqué, un detail n''est pas cloturé';
            return;
        end if;
    End If;

    res := 1;
end F_DOCUMENT_ENVOI_DETAILS_EXP;
/
