CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_DEL_REGROUPEMENT" (
    is_cur_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res in out number,
    msg in out varchar2
) AS

BEGIN
    res := 0;

    delete from GEO_ORDLIG
    where exists (select   1
                    from GEO_GEST_REGROUP
                    where  GEO_GEST_REGROUP.ORD_REF_ORIG =is_cur_ord_ref and
                                GEO_GEST_REGROUP.NUM_VERSION = 2 and
                                GEO_GEST_REGROUP.ORL_REF_RGP = GEO_ORDLIG.ORL_REF);

    delete from GEO_GEST_REGROUP where ORD_REF_ORIG = is_cur_ord_ref;

    commit;
    res := 1;
    msg := 'Suppression du regroupement terminé';

exception when others then
    msg := 'Suppression du regroupement annulé ' || SQLERRM;
    rollback;
    return;
END;
/


