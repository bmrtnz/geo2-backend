CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_TAKE_OPTION_STOCK(
	em_qte_res IN number,
	is_sto_ref IN varchar2,
	is_prop_code IN varchar2,
	is_pal_code IN varchar2,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_qte varchar2(50);
    ls_sto_desc varchar2(50);
    ls_sto_ref varchar2(50);
    ls_ord_ref varchar2(50);
    ls_nordre varchar2(50);
    ll_rc number;
    ll_qte number;
    ll_stm_ref number;
    ll_dispo_futur number;
    ll_qte_res_futur number;
    is_fou_code varchar2(50);
    is_art_ref varchar2(50);
    is_sto_desc varchar2(50);
    is_age varchar2(50);
    id_qte_ini number;
    id_qte_res number;
    id_dispo number;
BEGIN
    res := 0;
    msg := '';

    ll_qte		:= em_qte_res;
    if ll_qte = 0 then
        msg := 'La quantité ne peut être 0';
        return;
    end if;

    begin
        select fou_code,art_ref,sto_desc,age,qte_ini,qte_res
        into is_fou_code,is_art_ref,is_sto_desc,is_age,id_qte_ini,id_qte_res
        from geo_stock
        where sto_ref = is_sto_ref;
    exception when no_data_found then
        msg := 'Stock ref' || is_sto_ref || 'n''existe pas';
        return;
    end;

    ll_dispo_futur	:= id_dispo - ll_qte;
    ll_qte_res_futur	:= id_qte_res + ll_qte;

    -- if ll_dispo_futur < 0 then
    --     ll_rc	:= MessageBox("Attention", "le disponible en stock va devenir négatif --> " + string(ll_dispo_futur), Question!,OKCancel!, 2 )
    -- if ll_rc	= 2 then return 1
    -- end if

    ls_sto_desc	:= substr(is_sto_desc, 1, 35);
    BEGIN
        insert into geo_stock (
        fou_code, art_ref,
        sto_desc, qte_ini, qte_res,
        date_fab, sto_statut, date_statut,
        age, sto_ref_from,prop_code,
            pal_code)
        values (
        is_fou_code, is_art_ref,
        ls_sto_desc, ll_qte, 0,
                null, 'O', sysdate,
        is_age, is_sto_ref,is_prop_code,is_pal_code);
    exception when others then
        msg := 'insert into geo_stock ' || SQLERRM;
        rollback;
        return;
    END;

    begin
        update geo_stock set qte_res = ll_qte_res_futur
        where sto_ref = is_sto_ref;
        commit;
    exception when others then
        msg := 'update geo_stock ' || SQLERRM;
        rollback;
        return;
    end;

    res := 1;
end;
/

