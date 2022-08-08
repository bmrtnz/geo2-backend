create or replace trigger TRG_COMM_ORDRE_UP
    before update
    on GEO_COMM_ORDRE
    for each row
DECLARE
    x_num NUMBER;
    x_user  VARCHAR2(35);
BEGIN
    if (:new.mod_user is null) then
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;

        :NEW.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :NEW.mod_date := SYSDATE;
    end if;
END;
/

