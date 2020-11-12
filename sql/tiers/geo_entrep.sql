-------------------
-- geo_entrep
-------------------

create or replace trigger GEO_ENTREP_BEF_INS
    before insert
    on GEO_ENTREP
    for each row
DECLARE
    x_num NUMBER;
    x_user  VARCHAR2(35);
BEGIN
    -- sequence pou PK
    IF :NEW.cen_ref IS NULL THEN
        SELECT seq_cen_num.NEXTVAL INTO x_num FROM dual;
        :NEW.cen_ref   := TO_CHAR(x_num,'FM099999');
    END IF;

    -- date et user création
    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
    IF (:NEW.valide IS NULL) THEN
        :new.valide := 'O';
    END IF;

    IF (:NEW.gest_code IS NOT NULL) AND (:NEW.gest_ref IS NULL) THEN
        RAISE_APPLICATION_ERROR(-20201,
                                'vous devez saisir une référence pour les gestionnaire de palettes');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;

END;
/

create or replace trigger GEO_ENTREP_BEF_UPD
    before update
    on GEO_ENTREP
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

        IF (:NEW.gest_code IS NOT NULL) AND (:NEW.gest_ref IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20201,
                                    'vous devez saisir une r�f�rence pour les gestionnaire de palettes');
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;

END;
/

ALTER TABLE GEO_ADMIN.GEO_ENTREP ADD PRE_SAISIE VARCHAR2(1) NULL;