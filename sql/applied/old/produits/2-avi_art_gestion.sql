-- TABLE AVI_ART_GESTION
CREATE TABLE AVI_ART_GESTION
(
    ART_REF           VARCHAR2(6 BYTE),
    ART_ALPHA         VARCHAR2(35 BYTE),
    ART_REF_ASS       VARCHAR2(6 BYTE),
    BWSTOCK           VARCHAR2(1 BYTE) DEFAULT 'N',
    GTIN_COLIS_BW     VARCHAR2(35 BYTE),
    GTIN_PALETTE_BW   VARCHAR2(35 BYTE),
    GTIN_UC_BW        VARCHAR2(35 BYTE),
    MAJ_WMS           NUMBER(1)        DEFAULT 1,
    LF_EAN_ACHETEUR   VARCHAR2(35 BYTE),
    PCA_REF           VARCHAR2(6 BYTE),
    PDE_REF           VARCHAR2(6 BYTE),
    REF_MAT_PREM      VARCHAR2(6 BYTE) NOT NULL,
    REF_EMBALLAGE     VARCHAR2(6 BYTE) NOT NULL,
    REF_CDC           VARCHAR2(6 BYTE) NOT NULL,
    REF_NORMALISATION VARCHAR2(6 BYTE) NOT NULL,
    INS_STATION       VARCHAR2(35 BYTE),
    VALIDE            VARCHAR2(1 BYTE) DEFAULT 'O',
    CRE_DATE          DATE,
    CRE_USER          VARCHAR2(35 BYTE),
    GER_CODE          VARCHAR2(1 BYTE),
    MOD_DATE          DATE,
    MOD_USER          VARCHAR2(35 BYTE),
    DEMANDEUR         VARCHAR2(35 BYTE)
)
    TABLESPACE GEO_DATA
    PCTUSED 0
    PCTFREE 10
    INITRANS 1
    MAXTRANS 255
    STORAGE
(
    INITIAL 3M
    MINEXTENTS 1
    MAXEXTENTS 2147483645
    PCTINCREASE 0
    BUFFER_POOL DEFAULT
)
    LOGGING
    NOCACHE
    NOPARALLEL;

ALTER TABLE AVI_ART_GESTION
    ADD (
        CONSTRAINT PK_AVI_ART_GESTION PRIMARY KEY (ART_REF)
            USING INDEX
                TABLESPACE GEO_INDX
                PCTFREE 10
                INITRANS 2
                MAXTRANS 255
                STORAGE (
                INITIAL 1032 K
                MINEXTENTS 1
                MAXEXTENTS 2147483645
                PCTINCREASE 0
                ));

-- Alex : non c'est pas vrai
/*
CREATE UNIQUE INDEX UNIQUE_ART_GESTION ON AVI_ART_GESTION
(REF_MAT_PREM, REF_EMBALLAGE, REF_CDC, REF_NORMALISATION)
LOGGING
TABLESPACE GEO_INDX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          1032K
            MINEXTENTS       1
            MAXEXTENTS       2147483645
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
*/

CREATE OR REPLACE TRIGGER AVI_ART_GESTION_BEF_INS
    BEFORE INSERT
    ON AVI_ART_GESTION
    FOR EACH ROW
declare
    x_user varchar2(35);
begin
    if (:new.cre_user is null) then
        select sys_context('USERENV', 'OS_USER') into x_user from dual;
        :new.cre_user := x_user;
    end if;
    if (:new.cre_date is null) then
        :new.cre_date := sysdate;
    end if;
end;
/
SHOW ERRORS;


CREATE OR REPLACE TRIGGER AVI_ART_GESTION_BEF_UPD
    BEFORE UPDATE
    ON AVI_ART_GESTION
    FOR EACH ROW
declare
    x_user varchar2(35);
begin
    if (:new.mod_user is null) then
        select sys_context('USERENV', 'OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    end if;
    if (:new.mod_date is null) then
        :new.mod_date := sysdate;
    end if;
end;
/
SHOW ERRORS;

--CREATE SEQUENCE GEO_ADMIN.SEQ_AVI_ART_GEST START WITH 0 NOMAXVALUE MINVALUE 0 NOCYCLE NOCACHE NOORDER;
/*
CREATE OR REPLACE FUNCTION F_SEQ_AVI_ART_GEST
RETURN VARCHAR2 IS result VARCHAR2(6);
BEGIN
 SELECT  TO_CHAR(seq_AVI_ART_GEST.NEXTVAL,'FM0XXXXX') INTO result FROM dual;
 RETURN result;
END;
/
*/

ALTER TABLE GEO_ADMIN.AVI_ART_GESTION ADD PRE_SAISIE VARCHAR2(1) NULL;
ALTER TABLE GEO_ADMIN.AVI_ART_GESTION ADD SYNC_STAMP DATE;