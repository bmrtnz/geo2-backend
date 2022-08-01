create or replace trigger GEO_STOMVT_BEF_UPD
    before update
    on GEO_STOMVT
    for each row
declare
    x_user  varchar2(35);
begin
    if :new.mod_user is null then
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    end if;
    if :new.mod_date is null then
        :new.mod_date := sysdate;
    end if;
end;
/


