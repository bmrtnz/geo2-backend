create or replace trigger TRG_COMM_ORDRE
    before insert
    on GEO_COMM_ORDRE
    for each row
DECLARE
    x_num NUMBER;
    x_user  VARCHAR2(35);
BEGIN
    if (:new.COMM_ORD_REF is null) then
        SELECT SEQ_COMM_ORDRE.NEXTVAL INTO x_num FROM dual;
        :NEW.COMM_ORD_REF   := TO_CHAR(x_num,'FM09999999');
    end if;
    if (:new.mod_user is null) then
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :NEW.mod_date := SYSDATE;
    end if;
END;
/

