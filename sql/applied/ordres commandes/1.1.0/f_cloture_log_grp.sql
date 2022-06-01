CREATE OR REPLACE PROCEDURE F_CLOTURE_LOG_GRP (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_fou_code IN geo_ordlog.FOU_CODE%TYPE,
    arg_exped_fournni IN GEO_ORDLOG.FLAG_EXPED_FOURNNI%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_orx_ref varchar2(50);
    ls_orx_rat varchar2(6);
    ls_orx_rat_suiv varchar2(50);
BEGIN
    msg := '';
    res := 0;

    BEGIN
        select orx_rat
        into ls_orx_rat
        from geo_ordlog
        where ord_ref = arg_ord_ref
        AND fou_code = arg_fou_code;
    EXCEPTION WHEN others then
        res := 1;
        msg := 'OK';
        return;
    end;

    --Vérification s'il existe un ordre logistique de rattachement pour cet ORX_REF
    WHILE ls_orx_rat is not null 
    LOOP
        begin
            select ORX_REF, ORX_RAT into ls_orx_ref, ls_orx_rat_suiv
            from geo_ordlog
            where ORD_REF = arg_ord_ref and ORX_REF=ls_orx_rat;

            begin
                update geo_ordlog set flag_exped_fournni = arg_exped_fournni
                where ord_ref = arg_ord_ref AND orx_ref = ls_orx_rat;
            EXCEPTION when others then
                msg := 'Error: ' || 'Anomalie actualisation en-tête logistique lieu de groupage ' || arg_ord_ref || arg_fou_code || ' : erreur ORA ' || SQLERRM;
            end;
        EXCEPTION when others then
            res := 1;
            msg := 'OK';
            return;
        end;

        commit;
        ls_orx_rat := ls_orx_rat_suiv;
    end loop;

    res := 1;
    msg := 'OK';
END;
/

