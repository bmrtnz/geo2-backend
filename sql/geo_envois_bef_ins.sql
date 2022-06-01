create or replace trigger GEO_ENVOIS_BEF_INS
    before insert
    on GEO_ENVOIS
    for each row
DECLARE
    x_num NUMBER;
    x_user  VARCHAR2(35);
BEGIN
    IF (:new.mod_user is null) then
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :NEW.mod_date := SYSDATE;
    end if;
    if (:new.demdat is null) then
        :NEW.demdat := SYSDATE;
    end if;
END;
/
