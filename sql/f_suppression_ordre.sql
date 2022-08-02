CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_SUPPRESSION_ORDRE (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_username IN GEO_USER.nom_utilisateur%type,
    arg_commentaire IN GEO_ORDRE_DELETE_LOG.COMMENTAIRE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_noordre GEO_ORDRE.NORDRE%TYPE;
    ls_flfac GEO_ORDRE.FLFAC%TYPE;
    ls_flbaf GEO_ORDRE.FLBAF%TYPE;
    ll_nbenvoie number;
BEGIN
    -- correspond à la suppression d'un ordre (Pas de PBL)
    res := 0;
    msg := '';

    begin
        select count(*)
        into ll_nbenvoie
        from GEO_ENVOIS
        where ORD_REF = arg_ord_ref;

        if (ll_nbenvoie > 0) then
            msg := 'Impossible de supprimer l''ordre car des flux ont été générés';
            return;
        end if;

        SELECT NORDRE, FLFAC, FLBAF INTO ls_noordre, ls_flfac, ls_flbaf FROM GEO_ORDRE where ORD_REF = arg_ord_ref;

        if (ls_flfac = 'O' or ls_flbaf = 'O') then
            msg := 'Impossible de supprimer l''ordre car il est facturé';
            return;
        end if;

        DELETE FROM GEO_ORDRE WHERE ORD_REF = arg_ord_ref;
        INSERT INTO GEO_ORDRE_DELETE_LOG (ORD_REF, NORDRE, COMMENTAIRE, MOD_USER, MOD_DATE)
        VALUES (arg_ord_ref, ls_noordre, arg_commentaire, arg_username, sysdate);
        DELETE FROM GEO_MRU_ORDRE WHERE ORD_REF = arg_ord_ref;

        commit;
        res := 1;
    exception when others then
        msg := 'Erreur lors de la suppression de l''ordre : ' || SQLERRM;
    end;

end F_SUPPRESSION_ORDRE;
/

