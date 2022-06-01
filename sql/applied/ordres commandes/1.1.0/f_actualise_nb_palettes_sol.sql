-- f_actualise_nb_palettes_sol

CREATE OR REPLACE PROCEDURE F_ACTUALISE_NB_PALETTES_SOL (
    arg_ord_ref IN geo_gest_regroup.ORD_REF_ORIG%TYPE,
    arg_fou_code IN geo_gest_regroup.fou_code_orig%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ll_nb_pal_sol number;
BEGIN
    msg := '';
    res := 0;

    select
        CEIL(sum(
        case when OL.PAL_NB_COL <> 0
            then 
                OL.EXP_NB_COL / (case when OL.DEMIPAL_IND = 1 or OL.PAL_NB_PALINTER = 1 then OL.PAL_NB_COL * 2 else OL.PAL_NB_COL end)
            else
                OL.EXP_NB_PAL
        end))  
    into 
        ll_nb_pal_sol
    from
        geo_ordre O,
        geo_ordlig OL
    where
        OL.ORD_REF = O.ORD_REF AND
        O.ORD_REF = arg_ord_ref AND
        OL.FOU_CODE = arg_fou_code;

    if ll_nb_pal_sol <> 0 then
        update geo_ordlog set PAL_NB_SOL = ll_nb_pal_sol where fou_code = arg_fou_code and ord_ref = arg_ord_ref;
    end if;

    res := 1;
    msg := 'OK';
END;
/
