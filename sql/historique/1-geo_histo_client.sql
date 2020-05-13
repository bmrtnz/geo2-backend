-------------------
-- geo_histo_client
-------------------

create trigger TRG_INS_HISTO_CLIENT
    before insert
    on GEO_HISTO_CLIENT
    for each row
DECLARE
    x_num NUMBER;
    x_user  VARCHAR2(35);

BEGIN
    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
END;
/

create trigger TRG_UPD_HISTO_CLIENT
    before update
    on GEO_HISTO_CLIENT
    for each row
DECLARE
    x_user  VARCHAR2(35);
BEGIN
    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;

END;
/
