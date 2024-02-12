CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_DUPLIQUE_TRACA(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
    arg_ord_ref_new IN GEO_ORDRE.ORD_REF%TYPE,
    arg_orl_ref_new IN GEO_ORDLIG.ORL_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_ref_traca_new GEO_TRACA_DETAIL_PAL.REF_TRACA%TYPE;

    CURSOR CTRACA (ref_ligne GEO_ORDLIG.ORL_REF%TYPE) IS
        SELECT DISTINCT ref_traca
        FROM geo_traca_ligne
        WHERE orl_ref = ref_ligne;
BEGIN
    -- correspond à f_duplique_traca.pbl
    res := 0;
    msg := '';

    for r in CTRACA(arg_orl_ref)
    LOOP
            INSERT INTO GEO_TRACA_DETAIL_PAL
            (SSCC, PALLOX, PAL_CODE, PAL_SOL, VALIDE, ORD_REF, FOU_CODE, PDS_NET, PDS_BRUT, DEMI_PAL, REF_TRACA_SOL, POIDS_PAL)
            select SSCC,
                   PALLOX,
                   PAL_CODE,
                   PAL_SOL,
                   VALIDE,
                   arg_ord_ref_new,
                   FOU_CODE,
                   PDS_NET,
                   PDS_BRUT,
                   DEMI_PAL,
                   REF_TRACA_SOL,
                   POIDS_PAL
            FROM GEO_TRACA_DETAIL_PAL
            where     ORD_REF = arg_ord_ref  and
                    REF_TRACA = r.REF_TRACA;

            begin
                select DP1.REF_TRACA into ls_ref_traca_new
                FROM GEO_TRACA_DETAIL_PAL DP1 , GEO_TRACA_DETAIL_PAL DP2
                where DP2.REF_TRACA = r.REF_TRACA     and
                DP2.ORD_REF         = arg_ord_ref   and
                DP2.SSCC             = DP1.SSCC         and
                DP1.ORD_REF         = arg_ord_ref_new;
                
 

            exception 
            when no_data_found then
                    null;
           when TOO_MANY_ROWS then
                   msg := ' Plus d''une ligne de tracabilité trouvée : ' || SQLERRM || msg;
            end;
            
             insert    into GEO_TRACA_LIGNE
            (REF_TRACA, ORL_REF, PDS_NET, PDS_BRUT, VALIDE, NB_COLIS, ARBO_CODE)
            SELECT  ls_ref_traca_new,
                    arg_orl_ref_new,
                    PDS_NET,
                    PDS_BRUT,
                    VALIDE,
                    NB_COLIS,
                    ARBO_CODE
            FROM GEO_TRACA_LIGNE
            where REF_TRACA = r.REF_TRACA and
             ORL_REF = arg_orl_ref;
            
    end loop;

    res := 1;
    commit;
exception when others then
    msg := 'Erreur sur la duplication de la tracabilité ' || SQLERRM;
end F_DUPLIQUE_TRACA;
/
