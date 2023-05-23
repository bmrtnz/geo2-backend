-- en sortie liste des fournisseurs ayant l'article en stock + la quantité disponible
-- AR 21/04/09 création
-- SL 24/05/13 On rajoute le type de palette

CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_GET_STOCK_ARTICLE_FOURNI (
    arg_art_ref IN varchar2,
	res IN OUT NUMBER,
	msg IN OUT clob
)
AS
    ls_rc clob := '';
    ls_fou_code varchar2(50);
    ls_pal_code varchar2(50);
    ls_age varchar2(50);
    ls_fou_stocke_age varchar2(50);
    ls_info_stock clob := '~n ';
    ls_info clob;
    ll_qte_dispo number;
    ll_qte_ini_age number;
    ll_qte_res_age number;

    cursor C1 is
        SELECT geo_stock.fou_code,  (sum(geo_stock.qte_ini) - sum(geo_stock.qte_res)) as qte_dispo, pal_code
        FROM geo_stock
        WHERE geo_stock.art_ref = arg_art_ref
        GROUP BY geo_stock.fou_code, pal_code;
BEGIN
	res := 0;
	msg := '';

    for r in C1 loop

        ls_rc := ls_rc || '~n          ' || r.fou_code || '=' || to_char(r.qte_dispo) || ', palette=' || r.pal_code;

        declare
            cursor C_AGE is
            /*select distinct age , fou_code, qte_ini, qte_res  from geo_stock where age in (
            select max(age) from geo_stock where art_ref= :arg_art_ref
            )  and art_ref = :arg_art_ref and age >=3; */
            select distinct age, fou_code, (sum(qte_ini) - sum( qte_res)) as qte_res_age  from geo_stock where art_ref =arg_art_ref and age >=3 group by age , fou_code;
        begin
            for a in C_AGE loop
                if a.qte_res_age > 0 then
                    CASE to_char(a.age)
                        when '3' then
                            ls_info := ' STOCK DATE DE 9 à 20 JOURS !! ';
                        when '4' then
                            ls_info := ' STOCK DATE DE PLUS DE 21 JOURS !! ';
                        else
                            ls_info := '';
                    end case;
                    if r.fou_code = a.fou_code then
                        ls_info_stock := ls_info_stock ||  '~n  ' || r.fou_code || ' - ' || ls_info;
                        ls_info :='';
                    end if;
                end if;
            end loop;
        end;

    end loop;

    msg := ls_rc || ';' || ls_info_stock;
    res := 1;

END F_GET_STOCK_ARTICLE_FOURNI;
/

