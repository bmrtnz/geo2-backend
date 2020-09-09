-------------------
-- geo_fourni
-------------------

create or replace trigger GEO_FOURNI_BEF_INS
    before insert
    on GEO_FOURNI
    for each row
declare
 x_num number;
 x_user  varchar2(35);
begin
    IF (:NEW.mod_user IS NULL) THEN
        -- date et user cr�ation
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :new.mod_date := sysdate;
    END IF;

    :new.pref_fact := SUBSTR(:new.compte_compta,1,3);
end;
/

create trigger GEO_FOURNI_BEF_UPD
    before update
    on GEO_FOURNI
    for each row
DECLARE
 x_user  VARCHAR2(35);
BEGIN
if (:NEW.maj_wms = '0' and :OLD.maj_wms = '0') or :NEW.maj_wms = '1' then
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

ALTER TABLE GEO_ADMIN.GEO_FOURNI ADD PRE_SAISIE VARCHAR2(1) NULL;