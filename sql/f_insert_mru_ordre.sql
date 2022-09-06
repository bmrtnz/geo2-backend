CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_INSERT_MRU_ORDRE(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_username IN GEO_SOCIETE.SOC_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_cen_code GEO_MRU_ORDRE.CEN_CODE%TYPE;
    ls_soc_code GEO_MRU_ORDRE.SOC_CODE%TYPE;
    ls_ord_ref GEO_MRU_ORDRE.ORD_REF%TYPE;
    ls_nordre GEO_MRU_ORDRE.NORDRE%TYPE;
BEGIN
    -- correspond à f_insert_mru_ordre.pbl
    msg := '';
    res := 0;

    --  l'entrepôt existe t'il dans les favoris ?
    begin
        select ord_ref into ls_ord_ref
        from geo_mru_ordre
        where ord_ref = arg_ord_ref
          and nom_utilisateur = arg_username;

        update geo_mru_ordre set nom_utilisateur = arg_username
        where ord_ref = arg_ord_ref
          and nom_utilisateur = arg_username;

        commit;
    exception when others then
        begin
            -- il n'existe pas, on va l'insérer dans la base
            select soc_code, nordre, cen_code, ord_ref
            into ls_soc_code, ls_nordre, ls_cen_code, ls_ord_ref
            from geo_ordre where ord_ref = arg_ord_ref;

            insert into geo_mru_ordre (ord_ref, cen_code, soc_code, nordre, nom_utilisateur)
            values (ls_ord_ref, ls_cen_code, ls_soc_code, ls_nordre, arg_username);

            commit;
        exception when others then
            rollback;
            return;
        end;
    end;

    res := 1;

end F_INSERT_MRU_ORDRE;
/

