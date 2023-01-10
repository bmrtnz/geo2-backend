CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_SAUVE_LITIGE" (
    arg_lit_ref in GEO_LITIGE.LIT_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
BEGIN
    res := 0;
    msg := '';

    of_verif_litlig(arg_lit_ref,res,msg);
    if res = 0 then return; end if;

    declare
        ach_dev_taux number;
        cursor lignes is
            SELECT ll.lil_ref, ll.res_dev_pu, ll.res_pu, ol.ach_dev_taux
            FROM geo_litlig ll
            JOIN geo_ordlig ol on ll.orl_ref = ol.orl_ref
            WHERE ll.lit_ref = arg_lit_ref;
    begin
        for l in lignes loop
            ach_dev_taux := coalesce(l.ach_dev_taux, 1);

            if l.res_dev_pu is not null and l.res_dev_pu <> 0 then
                UPDATE geo_litlig
                SET res_pu = l.res_dev_pu * ach_dev_taux
                WHERE lil_ref = l.lil_ref;
            Else
                -- B AMADEI le 14/12
                -- Gérer la valeur 0
                If l.res_dev_pu = 0 and l.res_pu <> 0 Then
                    UPDATE geo_litlig
                    SET res_pu = 0
                    WHERE lil_ref = l.lil_ref;
                end if;
            end if;
        end loop;

        commit;

    EXCEPTION when OTHERS then
        msg := 'Echec lors de la sauvegarde du litige ' || arg_lit_ref || ' : ' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;

    res := 1;
END;
/

