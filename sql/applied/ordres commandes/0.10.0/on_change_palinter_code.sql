-- on_change_palinter_code

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_PALINTER_CODE" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
	res out number,
    msg out varchar2
) AS
    ls_palinter_code varchar2(50);
    ls_nb_palinter number;
begin

    msg := '';
    res := 0;

    select ol.palinter_code, ol.pal_nb_palinter
    into ls_palinter_code, ls_nb_palinter
    from geo_ordlig ol
    where orl_ref = arg_orl_ref;

    If ls_palinter_code = '-' then 
        update geo_ordlig
        set pal_nb_palinter = 0
        where orl_ref = arg_orl_ref;
        commit;
        msg := 'OK';
        res := 1;
        return;
    End IF;

    If ls_palinter_code is not null then
        If ls_nb_palinter = 0 Then
            update geo_ordlig
            set pal_nb_palinter = 1
            where orl_ref = arg_orl_ref;
            commit;
        End If;
    End If;

    msg := 'OK';
    res := 1;
    return;

end;