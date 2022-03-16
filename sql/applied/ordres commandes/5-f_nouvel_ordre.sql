create or replace procedure "GEO_ADMIN"."F_NOUVEL_ORDRE" (
    arg_soc_code in varchar2,
    res out number,
    msg out varchar2,
    ls_nordre out varchar2
)
AS
    ls_ord_ref varchar2(50);
    ll_nordre number;
BEGIN
    res := 0;
    msg := '';

    case
        WHEN arg_soc_code = 'SA' THEN
            select seq_nordre_sa.nextval into ll_nordre from dual;
        WHEN arg_soc_code = 'UDC' THEN
            select seq_nordre_udc.nextval into ll_nordre from dual;
        WHEN arg_soc_code = 'BWS' THEN
            select seq_nordre_bws.nextval into ll_nordre from dual;
        WHEN arg_soc_code = 'QUP' or arg_soc_code = 'IMP' THEN
            select seq_nordre_qup.nextval into ll_nordre from dual;
        WHEN arg_soc_code = 'SpA' THEN
            select SEQ_NORDRE_SPA.nextval into ll_nordre from dual;
        WHEN arg_soc_code = 'BUK' THEN
            select SEQ_NORDRE_BUK.nextval into ll_nordre from dual;
        WHEN arg_soc_code = 'IUK' THEN
            select SEQ_NORDRE_IUK.nextval into ll_nordre from dual;
        else
            msg := '%%% f_nouvel_ordre : société ' || arg_soc_code || ' inconnue';
            return;
        end case;
    ls_nordre := to_char(ll_nordre, 'FM099999');
    res := 1;

    exception when others then
        msg := '%%% erreur d''attribution de n° d''ordre pour la société ' || arg_soc_code;
end;
