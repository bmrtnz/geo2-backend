-------------------
-- geo_histo_fourni
-------------------

create or replace trigger TRG_INS_HISTO_FOURNI
    before insert
    on GEO_HISTO_FOURNI
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

create or replace trigger TRG_UPD_HISTO_FOURNI
    before update
    on GEO_HISTO_FOURNI
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





