CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDFRA_BEFORE_UI
BEFORE INSERT OR UPDATE 
ON GEO_ADMIN.GEO_ORDFRA FOR EACH ROW
-- Trigger servant à imiter le fonctionement de GEO2 dans GEO (flemme de modifier GEO)
BEGIN
    -- règle ach_dev_pu null sur GEO2
    IF :NEW.ACH_DEV_PU IS NULL THEN
        :NEW.ACH_DEV_PU := 0;
    END IF; 

    -- si insert/update par GEO - nvl() car <> ne marche pas si NULL
    IF (:NEW.MONTANT * :NEW.DEV_TX) <> NVL(:NEW.MONTANT_TOT,-1) THEN
        
        -- si insert par GEO
        IF :NEW.ACH_QTE IS NULL THEN
            :NEW.ACH_QTE := 1;
            :NEW.ACH_DEV_PU := :NEW.MONTANT;
        -- si update par GEO
        ELSE
            :NEW.ACH_DEV_PU := :NEW.ACH_DEV_PU + ((:NEW.MONTANT - :OLD.MONTANT) / :OLD.ACH_QTE);
        END IF;

        -- recalcul des autres colonnes avec les nouvelles valeurs (GEO2 le fait déja)
        :NEW.ACH_PU := :NEW.ACH_DEV_PU * :NEW.DEV_TX;
        :NEW.MONTANT := :NEW.ACH_QTE * :NEW.ACH_DEV_PU;
        :NEW.MONTANT_TOT := :NEW.ACH_QTE * :NEW.ACH_PU;
    END IF;
END;
/