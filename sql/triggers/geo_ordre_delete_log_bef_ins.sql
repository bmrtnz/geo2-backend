create or replace trigger GEO_ORDRE_DELETE_LOG_BEF_INS
    before insert
    on GEO_ORDRE_DELETE_LOG
    for each row
declare
    x_user  varchar2(35);
begin
    if (:new.mod_user is null) then
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :new.mod_date := sysdate;
    end if;
end;
/

