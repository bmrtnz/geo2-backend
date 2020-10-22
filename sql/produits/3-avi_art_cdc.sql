-- TABLE AVI_ART_CDC
CREATE TABLE AVI_ART_CDC
(
    REF_CDC     VARCHAR2(6 BYTE),
    ESP_CODE    VARCHAR2(6 BYTE) NOT NULL,
    CAT_CODE    VARCHAR2(6 BYTE) NOT NULL,
    CIR_CODE    VARCHAR2(6 BYTE),
    CLR_CODE    VARCHAR2(6 BYTE),
    PEN_CODE    VARCHAR2(6 BYTE),
    RAN_CODE    VARCHAR2(6 BYTE),
    SUC_CODE    VARCHAR2(6 BYTE),
    INS_SECCOM  VARCHAR2(35 BYTE),
    INS_STATION VARCHAR2(35 BYTE),
    CRE_USER    VARCHAR2(35 BYTE),
    CRE_DATE    DATE,
    MOD_USER    VARCHAR2(35 BYTE),
    MOD_DATE    DATE,
    VALIDE      VARCHAR2(1 BYTE) DEFAULT 'O'
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

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT PK_AVI_ART_CDC PRIMARY KEY (REF_CDC)
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
                )
        );

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_CDC_ESPECE FOREIGN KEY (ESP_CODE)
            REFERENCES GEO_ESPECE (ESP_CODE));

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_ART_CDC_CATEGO FOREIGN KEY (ESP_CODE, CAT_CODE)
            REFERENCES GEO_CATEGO (ESP_CODE, CAT_CODE));

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_ART_CDC_CIRAGE FOREIGN KEY (ESP_CODE, CIR_CODE)
            REFERENCES GEO_CIRAGE (ESP_CODE, CIR_CODE));

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_ART_CDC_COLORA FOREIGN KEY (ESP_CODE, CLR_CODE)
            REFERENCES GEO_COLORA (ESP_CODE, CLR_CODE));

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_ART_CDC_PENETRO FOREIGN KEY (ESP_CODE, PEN_CODE)
            REFERENCES GEO_PENETRO (ESP_CODE, PEN_CODE));

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_ART_CDC_RANGEM FOREIGN KEY (ESP_CODE, RAN_CODE)
            REFERENCES GEO_RANGEM (ESP_CODE, RAN_CODE));

ALTER TABLE AVI_ART_CDC
    ADD (
        CONSTRAINT FK_ART_CDC_SUCRE FOREIGN KEY (ESP_CODE, SUC_CODE)
            REFERENCES GEO_SUCRE (ESP_CODE, SUC_CODE));

ALTER TABLE AVI_ART_GESTION
    ADD (
        CONSTRAINT FK_GESTION_CDC FOREIGN KEY (REF_CDC)
            REFERENCES AVI_ART_CDC (REF_CDC));

COMMENT ON COLUMN AVI_ART_CDC.CAT_CODE IS 'FK catégorie';
COMMENT ON COLUMN AVI_ART_CDC.CLR_CODE IS 'FK coloration';
COMMENT ON COLUMN AVI_ART_CDC.SUC_CODE IS 'FK sucre';
COMMENT ON COLUMN AVI_ART_CDC.PEN_CODE IS 'FK penetro';
COMMENT ON COLUMN AVI_ART_CDC.CIR_CODE IS 'FK cirage';
COMMENT ON COLUMN AVI_ART_CDC.RAN_CODE IS 'FK rangement';
COMMENT ON COLUMN AVI_ART_CDC.INS_SECCOM IS 'instructions sec.com.';
COMMENT ON COLUMN AVI_ART_CDC.INS_STATION IS 'instructions station';

CREATE OR REPLACE TRIGGER AVI_ART_CDC_BEF_INS
    BEFORE INSERT
    ON AVI_ART_CDC
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
    if (:new.valide is null) then
        :new.valide := 'O';
    end if;
end;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER AVI_ART_CDC_BEF_UPD
    BEFORE UPDATE
    ON AVI_ART_CDC
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

CREATE UNIQUE INDEX UNIQUE_ART_CDC ON AVI_ART_CDC
    (ESP_CODE, CAT_CODE, CIR_CODE, CLR_CODE, PEN_CODE, RAN_CODE, SUC_CODE, INS_SECCOM, INS_STATION)
    LOGGING
    TABLESPACE GEO_INDX
    PCTFREE 10
    INITRANS 2
    MAXTRANS 255
    STORAGE (
    INITIAL 1032 K
    MINEXTENTS 1
    MAXEXTENTS 2147483645
    PCTINCREASE 0
    BUFFER_POOL DEFAULT
    )
    NOPARALLEL;

CREATE SEQUENCE GEO_ADMIN.SEQ_AVI_ART_CDC START WITH 0 NOMAXVALUE MINVALUE 0 NOCYCLE NOCACHE NOORDER;
CREATE OR REPLACE FUNCTION F_SEQ_AVI_ART_CDC
    RETURN VARCHAR2 IS result VARCHAR2(6);
BEGIN
    SELECT  TO_CHAR(seq_AVI_ART_CDC.NEXTVAL,'FM0XXXXX') INTO result FROM dual;
    RETURN result;
END;
/
