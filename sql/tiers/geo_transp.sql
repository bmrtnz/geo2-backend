-------------------
-- geo_transp
-------------------

create or replace trigger GEO_TRANSP_BEF_INS
    before insert
    on GEO_TRANSP
    for each row
declare
 x_num number;
 x_user  varchar2(35);
begin
    -- date et user cr�ation
    IF (:NEW.mod_user IS NULL) THEN
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :new.mod_date := sysdate;
    END IF;
end;
/

create or replace trigger GEO_TRANSP_BEF_UPD
    before update
    on GEO_TRANSP
    for each row
DECLARE
 x_user  VARCHAR2(35);
BEGIN

    IF (:NEW.maj_wms = '0' and :OLD.maj_wms = '0') or :NEW.maj_wms = '1' THEN
        IF (:NEW.mod_user IS NULL) THEN
            SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
            :NEW.mod_user := x_user;
        END IF;
        IF (:NEW.mod_date IS NULL) THEN
            :NEW.mod_date := SYSDATE;
        END IF;
        :NEW.maj_wms := '1';
    END IF;

END;
/

ALTER TABLE GEO_ADMIN.GEO_TRANSP ADD PRE_SAISIE VARCHAR2(1) NULL;