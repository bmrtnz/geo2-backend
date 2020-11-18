CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_CERTIFS_TIERS_BEF_INS
    BEFORE INSERT
    ON GEO_ADMIN.GEO_CERTIFS_TIERS REFERENCING NEW AS NEW OLD AS OLD
    FOR EACH ROW

BEGIN
    IF (:NEW.K_CERTIFS_TIERS IS NULL) THEN
        :NEW.K_CERTIFS_TIERS := F_SEQ_K_CERTIFS_TIERS;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;