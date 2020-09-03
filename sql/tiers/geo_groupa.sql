-------------------
-- geo_groupa
-------------------

create trigger GEO_GROUPA_BEF_INS
    before insert
    on GEO_GROUPA
    for each row
declare
 x_num number;
 x_user  varchar2(35);
begin
    -- date et user cr�ation
    if (:new.mod_user is null) then
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :new.mod_date := sysdate;
    end if;
    if (:new.valide is null) then
        :new.valide := 'O';
    end if;
end;
/

create trigger GEO_GROUPA_BEF_UPD
    before update
    on GEO_GROUPA
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

ALTER TABLE GEO_ADMIN.GEO_GROUPA ADD PRE_SAISIE VARCHAR2(1) NULL;