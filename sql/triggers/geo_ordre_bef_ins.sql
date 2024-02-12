CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDRE_BEF_INS
BEFORE INSERT ON GEO_ADMIN.GEO_ORDRE FOR EACH ROW
DECLARE
 x_num NUMBER;
 x_user  VARCHAR2(35);
BEGIN
-- no chrono géré par programme
-- date et user création
 --SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
 :NEW.nordre := TO_CHAR(TO_NUMBER(:NEW.nordre),'FM099999');
 --:NEW.mod_user := x_user;
 --:NEW.mod_date := SYSDATE;
 :NEW.valide := 'O';
 :NEW.credat := SYSDATE;
 :NEW.depdatp_asc := TO_CHAR(:NEW.depdatp,'yyyymmdd');
 :NEW.came_code := TO_CHAR(ADD_MONTHS(:NEW.depdatp,-6),'YY');
END;
/
SHOW ERRORS;