create or replace function GEO_ADMIN.f_geo2_init_blocage_ordre(REF_ORDRE GEO_ORDRE.ORD_REF%TYPE)
    RETURN CHAR IS status char(1);
res number;
msg clob;
begin
    /**
     * Fonction crée uniquement dans le but de pouvoir appeler cette procédure depuis un select.
     * Utilisé pour les ordres EDI.
     */
    F_INIT_BLOCAGE_ORDRE(REF_ORDRE, 'TINA', res, msg, status);

    return status;
end;
/

