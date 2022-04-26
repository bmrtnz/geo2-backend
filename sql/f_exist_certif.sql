CREATE OR REPLACE PROCEDURE F_EXIST_CERTIF (
    is_cli_ref IN GEO_ORDRE.CLI_REF%TYPE,
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_list_certifs GEO_ORDLIG.LIST_CERTIFS%TYPE;
    ll_certif_lig number;
    array_list_certifs_ligne p_str_tab_type;
    ls_type GEO_CERTIF_MODCULT.TYPE_CERT%TYPE;
    ll_cpt number;
BEGIN
    -- correspond à f_exist_certif.pbl
    res := 0;
    msg := 'Non';

    begin
        SELECT "GEO_ORDLIG"."LIST_CERTIFS" into ls_list_certifs FROM "GEO_ORDLIG" WHERE ( "GEO_ORDLIG"."ORL_REF" = is_ord_ref);

        if ls_list_certifs is not null and ls_list_certifs <> '' then
            f_split(ls_list_certifs, ',', array_list_certifs_ligne);
            for i in 0..array_list_certifs_ligne.COUNT loop
                    ll_certif_lig := to_number(array_list_certifs_ligne(i));
                    SELECT "GEO_CERTIF_MODCULT"."TYPE_CERT" into ls_type FROM "GEO_CERTIF_MODCULT" WHERE ( "GEO_CERTIF_MODCULT"."K_CERTIF" = ll_certif_lig);
                    if ls_type = 'CERTIF' then
                        msg := 'OUI';
                        res := 1;
                        return;
                    end if;
                end loop;
        end if;

        exception
            when no_data_found then
                SELECT  count(*) into ll_cpt FROM "GEO_CERTIFS_TIERS" WHERE  "GEO_CERTIFS_TIERS"."TIERS" = is_cli_ref AND "GEO_CERTIFS_TIERS"."TYP_TIERS" = 'C';
                If ll_cpt > 0 then
                    msg := 'OUI';
                    res := 1;
                end if;
    end;
end F_EXIST_CERTIF;
