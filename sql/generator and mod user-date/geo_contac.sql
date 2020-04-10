-------------------
-- geo_contac
-------------------

create trigger GEO_CONTAC_BEF_INS
    before insert
    on GEO_CONTAC
    for each row
DECLARE
 x_num NUMBER;
 x_user  VARCHAR2(35);
BEGIN
    IF (:NEW.con_ref IS NULL) THEN
        -- sequence pour PK
        SELECT seq_con_num.NEXTVAL INTO x_num FROM dual;
        :NEW.con_ref   := TO_CHAR(x_num,'FM099999');
    END IF;

    IF (:NEW.MOD_USER IS NULL) THEN
        -- date et user création
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.MOD_DATE IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
    :NEW.valide := 'O';
END;
/

create trigger GEO_CONTAC_BEF_UPD
    before update
    on GEO_CONTAC
    for each row
DECLARE
    x_user  VARCHAR2(35);
BEGIN
    IF (:NEW.mod_user is null) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
END;
/



