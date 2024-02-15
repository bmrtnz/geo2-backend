CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDFRA_BEFORE_UI
BEFORE INSERT OR UPDATE
ON GEO_ADMIN.GEO_ORDFRA
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
    -- règle ach_dev_pu null sur GEO2
    IF :NEW.ACH_DEV_PU IS NULL THEN
        :NEW.ACH_DEV_PU := 0;
    END IF;
END;
/
