-- on_change_vte_pu

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_VTE_PU" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
	res out number,
    msg out varchar2
) AS
    ls_vte_pu number;
begin

    msg := '';
    res := 0;

    select ol.vte_pu
    into ls_vte_pu
    from geo_ordlig ol
    where orl_ref = arg_orl_ref;

    If ls_vte_pu > 0 Then
        update geo_ordlig
        set ind_gratuit = 'N'
        where orl_ref = arg_orl_ref;
        commit;
    End If;

    msg := 'OK';
    res := 1;
    return;

end;