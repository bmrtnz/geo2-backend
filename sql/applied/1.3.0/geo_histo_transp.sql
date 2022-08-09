CREATE TABLE GEO_ADMIN.GEO_HISTO_TRANSP
(
  HISTO_TRP_REF   NUMBER              NOT NULL,
  TRP_CODE        VARCHAR2(12 BYTE)             NOT NULL,
  VALIDE          VARCHAR2(1 BYTE)              NOT NULL,
  MOD_USER        VARCHAR2(35 BYTE),
  MOD_DATE        DATE,
  COMM_HISTO      VARCHAR2(128 BYTE)
)
TABLESPACE GEO_DATA
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING
NOCOMPRESS
NOCACHE
NOPARALLEL
NOMONITORING;

COMMENT ON COLUMN GEO_ADMIN.GEO_HISTO_TRANSP.HISTO_TRP_REF IS 'Référence historique';

COMMENT ON COLUMN GEO_ADMIN.GEO_HISTO_TRANSP.TRP_CODE IS 'FK code TRP';

COMMENT ON COLUMN GEO_ADMIN.GEO_HISTO_TRANSP.VALIDE IS 'valide O/N';

COMMENT ON COLUMN GEO_ADMIN.GEO_HISTO_TRANSP.MOD_USER IS 'modif (user)';

COMMENT ON COLUMN GEO_ADMIN.GEO_HISTO_TRANSP.MOD_DATE IS 'modif (date)';

COMMENT ON COLUMN GEO_ADMIN.GEO_HISTO_TRANSP.COMM_HISTO IS 'Commentaire pour le suivi de validité du TRP';


CREATE OR REPLACE TRIGGER GEO_ADMIN.TRG_INS_HISTO_TRANSP
    before insert
    ON GEO_ADMIN.GEO_HISTO_TRANSP     for each row
DECLARE
    x_num NUMBER;
    x_user  VARCHAR2(35);
BEGIN
    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
END;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.TRG_UPD_HISTO_TRANSP
    before update
    ON GEO_ADMIN.GEO_HISTO_TRANSP     for each row
DECLARE
    x_user  VARCHAR2(35);
BEGIN
    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
END;
/


ALTER TABLE GEO_ADMIN.GEO_HISTO_TRANSP ADD (
  PRIMARY KEY
 (HISTO_TRP_REF)
    USING INDEX
    TABLESPACE GEO_DATA
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          64K
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
               ));
/
