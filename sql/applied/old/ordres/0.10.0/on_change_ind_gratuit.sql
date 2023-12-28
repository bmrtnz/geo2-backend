-- on_change_ind_gratuit

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."ON_CHANGE_IND_GRATUIT" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
	res out number,
    msg out varchar2
) AS
    ls_ind_gratuit varchar2(1);
    ld_ach_dev_pu number;
begin

    msg := '';
    res := 0;

    select ol.ind_gratuit, ol.ach_dev_pu
    into ls_ind_gratuit, ld_ach_dev_pu
    from geo_ordlig ol
    where orl_ref = arg_orl_ref;

    If ls_ind_gratuit = 'O' Then
        update geo_ordlig
        set vte_pu = 0
        where orl_ref = arg_orl_ref;
        If ld_ach_dev_pu is null Then 
            update geo_ordlig
            set ach_dev_pu = 0, ach_pu = 0
            where orl_ref = arg_orl_ref;
        End If;
        commit;
    End If;               

    msg := 'OK';
    res := 1;
    return;

end;