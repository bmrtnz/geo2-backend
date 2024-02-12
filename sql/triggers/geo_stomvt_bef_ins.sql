create or replace trigger GEO_STOMVT_BEF_INS
    before insert
    on GEO_STOMVT
    for each row
declare
    x_user  varchar2(35);
    x_num	 number;
begin
    IF :new.stm_ref IS NULL THEN
        select seq_stm_num.nextval into x_num from dual;
        :new.stm_ref   := to_char(x_num,'FM099999');
    END IF;

    if :new.mod_user is null then
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    end if;
    if :new.mod_date is null then
        :new.mod_date := sysdate;
    end if;

    :new.valide := 'O';
    update geo_stock set qte_res = qte_res + :new.mvt_qte where geo_stock.sto_ref = :new.sto_ref;
end;
/


