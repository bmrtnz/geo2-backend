CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_TEST_ANNULE_ORDRE (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ll_detail_cloturer number;
    ll_cpt_flux number;
BEGIN
    -- correspond à test_annule_ordre.pbl
    res := 0;
    msg := '';

    select nordre into ls_nordre
    from GEO_ORDRE
    where ORD_REF = arg_ord_ref;

    select count(*) into ll_detail_cloturer
    from geo_ordlog
    where flag_exped_fournni = 'O' and ORD_REF = arg_ord_ref;

    if ll_detail_cloturer <> 0 then
        msg := 'ordre ' || ls_nordre || ' détail(s) déjà clôturé(s): annulation impossible';
        return;
    else
        select count(*) into ll_cpt_flux from geo_envois where ord_ref = arg_ord_ref;
        if ll_cpt_flux = 0 then
            msg := 'ordre ' || ls_nordre || ' aucun flux envoyé: annulation impossible';
            return;
        end if;
    end if;

    res := 1;
end F_TEST_ANNULE_ORDRE;
/

