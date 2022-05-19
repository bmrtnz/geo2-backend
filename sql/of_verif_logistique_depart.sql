CREATE OR REPLACE PROCEDURE "OF_VERIF_LOGISTIQUE_DEPART" (
    arg_ord_ref GEO_ORDLIG.orl_ref%type,
	res out number,
    msg out varchar2
) AS
begin

    msg := '';
    res := 0;

    declare
        cursor cur_ols is
            select
                orl_ref,
                trp_code,
                grp_code
            from geo_ordlig
            where ord_ref = arg_ord_ref;
        ls_trp_code varchar2(50);
        ls_grp_code varchar2(50);
    begin
        for r in cur_ols
        loop
            if r.trp_code is null then ls_trp_code := ''; end if;
            if r.grp_code is null then ls_grp_code := ''; end if;
            update geo_ordlig
            set
                trp_code = ls_trp_code,
                grp_code = ls_grp_code
            where orl_ref = r.orl_ref;
            commit;
        end loop;

        If ls_trp_code is not null and ls_grp_code is not null Then
            msg := 'Veuiller saisir un lieu de groupage pour le transport d''approche' || ls_trp_code;
            res := 0;
            return;
        end if;
    end;

    msg := 'OK';
    res := 1;
    return;

end;
/

