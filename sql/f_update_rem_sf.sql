CREATE OR REPLACE PROCEDURE F_UPDATE_REM_SF(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ld_rem_tx number;
    ls_flag_fac number;
    ld_totvte number;
    ls_dev number;
    ld_taux number;
    ld_totvte_dev number;
    ld_remise number;
BEGIN
    -- correspond à f_update_rem_sf.pbl
    msg := '';
    res := 0;

    -- Teste si ordre facturé et récupère le taux de remise sur facture
    select remsf_tx, flfac, totvte, dev_code, dev_tx
    into ld_rem_tx, ls_flag_fac, ld_totvte, ls_dev, ld_taux
    from geo_ordre where ord_ref = is_ord_ref;

    if (ls_flag_fac = 'N') then
        if ld_totvte = 0 then
            select sum(round(geo_ordlig.vte_qte * geo_ordlig.vte_pu, 2))
            into ld_totvte
            from geo_ordlig where ord_ref = is_ord_ref;
        end if;
        if ld_taux is null or ld_taux = 0 then
                ld_taux := 1;
        end if;

        ld_totvte_dev := ld_totvte / ld_taux;

        -- On ne fait quelque chose que si l'ordre existe et n'est pas facturé, mise a jour du montant de remise sur facture et mise a jour du montant total net après remise
        ld_remise := round(ld_totvte_dev * ld_rem_tx / 100.0 , 2);
        update geo_ordre
        set tot_remsf = ld_remise, pied_ht_net_1 = (ld_totvte_dev - ld_remise)
        where ord_ref = is_ord_ref;
    end if;

    res := 1;
end F_UPDATE_REM_SF;
