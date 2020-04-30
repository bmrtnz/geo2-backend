-------------------
-- avi_art_normalisation
-------------------

create trigger AVI_ART_NORMALISATION_BEF_INS
    before insert
    on AVI_ART_NORMALISATION
    for each row
declare
    x_user  varchar2(35);
begin
    if (:new.cre_user is null) then
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.cre_user := x_user;
    end if;
    if (:new.cre_date is null) then
        :new.cre_date := sysdate;
    end if;
    if (:new.valide is null) then
        :new.valide := 'O';
    end if;
end;
/

create trigger AVI_ART_NORMALISATION_BEF_UPD
    before update
    on AVI_ART_NORMALISATION
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
