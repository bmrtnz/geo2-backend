CREATE OR REPLACE TRIGGER GEO_ENVOIS_BEF_UPD
    before update
    on GEO_ENVOIS
    for each row
DECLARE
    x_user  VARCHAR2(35);
BEGIN
    if (:new.mod_user is null) then
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :NEW.mod_date := SYSDATE;
    end if;
    if (:new.mod_dated is null) then
        :NEW.mod_dated := SYSDATE;
    end if;
    
    If    :new.TRAIT_EXP ='O'  and :old.TRAIT_EXP = 'N' THEN 
        :NEW.DAT_TRAIT_EXP := SYSDATE;
    end if;
END;
/