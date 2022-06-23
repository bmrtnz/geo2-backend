CREATE OR REPLACE FUNCTION GEO_ORDRE_STATUS_EXPED(REF_ORDRE GEO_ORDRE.ORD_REF%TYPE) RETURN CHAR IS
    status char(1);
BEGIN
    with status as (
        select o.ord_ref
        from geo_ordre o
        where ord_ref = REF_ORDRE
          and exists(
                select 1
                from GEO_ORDLOG
                where GEO_ORDLOG.ORD_REF = o.ORD_REF
                  and GEO_ORDLOG.VALIDE = 'O'
                  and GEO_ORDLOG.FLAG_EXPED_FOURNNI = 'O'
            )
          and not exists(select 1
                         from GEO_ORDLOG
                         where GEO_ORDLOG.ORD_REF = o.ORD_REF
                           and GEO_ORDLOG.VALIDE = 'O'
                           and GEO_ORDLOG.FLAG_EXPED_FOURNNI = 'N'
                           and exists(select 1
                                      from GEO_ORDLIG
                                      where GEO_ORDLOG.ORD_REF = GEO_ORDLIG.ORD_REF
                                        and GEO_ORDLOG.FOU_CODE = GEO_ORDLIG.FOU_CODE))
          and exists(select 1
                     from GEO_ENVOIS
                     WHERE GEO_ENVOIS.FLU_CODE in ('ORDRE', 'ACHMAR')
                       AND GEO_ENVOIS.ORD_REF = o.ORD_REF)
    )

    select 'N' into status from dual where not exists (select 1 from status)
    union
    select 'O' from dual where exists (select 1 from status);

    return status;
end;
/

