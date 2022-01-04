CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CALCUL_COUT_TRANSPORT" (
    arg_ord_ref in GEO_ORDLIG.ORD_REF%TYPE,
    msg out varchar2,
    res out number
)
AS
    ls_trp_bta_code varchar2(50);
    ld_trp_pu number;
    ld_tot_exp_pds_net number;
    ld_tot_exp_nb_pal number;
    ld_tot_exp_nb_col number;
    ld_lig_exp_pds_net number;
    ld_lig_exp_nb_pal number;
    ld_lig_exp_nb_col number;
    ld_prix_trp_kilo number := 0;
BEGIN
    res := 0;
    msg := ''; -- Containing SQL error (Maybe with exception in oracle)

    select trp_bta_code, trp_pu
    into ls_trp_bta_code, ld_trp_pu
    from geo_ordre
    where ord_ref = arg_ord_ref ;

    select sum(exp_nb_pal), sum(exp_nb_col), sum(exp_pds_net)
    into  ld_tot_exp_nb_pal, ld_tot_exp_nb_col, ld_tot_exp_pds_net
    from geo_ordlig where ord_ref = arg_ord_ref
    group by ord_ref;

    CASE ls_trp_bta_code
        WHEN 'CAMION ' THEN
            If ld_tot_exp_pds_net > 0 Then
                ld_prix_trp_kilo := ld_trp_pu / ld_tot_exp_pds_net;
            End If;
        WHEN 'COLIS' THEN
            If ld_tot_exp_pds_net > 0 Then
                ld_prix_trp_kilo := round(ld_tot_exp_nb_col * ld_trp_pu, 5) / ld_tot_exp_pds_net;
            End If;
        WHEN 'PAL' THEN
            If ld_tot_exp_pds_net > 0 Then
                ld_prix_trp_kilo := round(ld_tot_exp_nb_pal * ld_trp_pu, 5) / ld_tot_exp_pds_net;
            End If;
        WHEN 'KILO' THEN
            ld_prix_trp_kilo := ld_trp_pu;
        WHEN 'TONNE' THEN
            ld_prix_trp_kilo := round(ld_trp_pu / 1000, 2);	-- net et non pas brut (...)
        ELSE -- ???
		    ld_prix_trp_kilo := 0;
    END CASE;

    res := ld_prix_trp_kilo;
END;
