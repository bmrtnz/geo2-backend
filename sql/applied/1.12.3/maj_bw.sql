DROP TABLE GEO_FRAIS_TYP;

CREATE TABLE GEO_FRAIS_TYP
( K_FRAIS_TYP       NUMBER,
  FRA_CODE        VARCHAR2(6 BYTE),
  TYT_CODE         VARCHAR2(1 BYTE),
  FRA_TIERS_CODE  VARCHAR2(12 BYTE),
  ACH_DEV_PU    NUMBER(12,2),
  ACH_DEV_CODE  VARCHAR2(3 BYTE),
  MOD_USER                 VARCHAR2(35 BYTE),
  MOD_DATE                 DATE,
  VALIDE                   VARCHAR2(1 BYTE)     DEFAULT 'O',
  PRIMARY  KEY (K_FRAIS_TYP));


  DROP SEQUENCE GEO_ADMIN.SEQ_K_FRAIS_TYP;

CREATE SEQUENCE GEO_ADMIN.SEQ_K_FRAIS_TYP
  START WITH 1
  MAXVALUE 999999999
  MINVALUE 1
  CYCLE
  NOCACHE
  NOORDER;

CREATE OR REPLACE FUNCTION GEO_ADMIN.F_SEQ_K_FRAIS_TYP
RETURN NUMBER IS result NUMBER;
BEGIN
        SELECT  SEQ_K_FRAIS_TYP.NEXTVAL INTO result FROM dual;
        RETURN result;
END;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_FRAIS_TYP_INS
BEFORE INSERT
ON GEO_ADMIN.GEO_FRAIS_TYP REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
 x_user          VARCHAR2(35);
BEGIN
-- date et user création
 SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
 :NEW.K_FRAIS_TYP := F_SEQ_K_FRAIS_TYP;
 :NEW.MOD_USER := x_user;
 :NEW.MOD_DATE := SYSDATE;
 :new.valide := 'O';
 EXCEPTION
   WHEN OTHERS THEN
   -- Consider logging the error and then re-raise
   RAISE;
END;
/
Insert into GEO_FRAIS
   (FRA_CODE, FRA_DESC, MOD_USER, MOD_DATE, VALIDE)
 Values
   ('GMVS', 'Good Movement vehicle servic', 'bruno', TO_DATE('07/17/2023 17:04:01', 'MM/DD/YYYY HH24:MI:SS'),
    'O');
COMMIT;
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('GMVS', 'T', 'SITRA', 12.5,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('GMVS', 'T', 'TROTA', 8,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDEXP', 'S', 'LGL CUSTOMS', 58,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDIMP', 'S', 'EUROVISION', 49,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDIMP', 'S', 'LGL CUSTOMS', 84,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDIMP', 'S', 'BOLLORE', 75,'GBP');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDEXP', 'S', 'BOLLORE', 59,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDEXP', 'S', '2FL', 114,'EUR');
Insert into GEO_FRAIS_TYP
   (FRA_CODE, TYT_CODE, FRA_TIERS_CODE, ACH_DEV_PU,ACH_DEV_CODE)
 Values
   ('DEDEXP', 'S', 'EUROVISION', 49,'EUR');
COMMIT;
/
