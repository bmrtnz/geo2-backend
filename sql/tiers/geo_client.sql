-------------------
-- geo_client
-------------------

create OR REPLACE trigger GEO_CLIENT_BEF_INS
    before insert
    on GEO_CLIENT
    for each row
declare
    x_num number;
    x_user  varchar2(35);
begin
    -- sequence pou PK
    IF (:NEW.cli_ref IS NULL) THEN
        select seq_cli_num.nextval into x_num from dual;
        :new.cli_ref   := to_char(x_num,'FM099999');
    END IF;

    -- date et user création
    IF (:NEW.mod_user IS NULL) THEN
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :new.mod_date := sysdate;
    END IF;
    IF (:NEW.libelle_ristourne IS NULL) THEN
        :new.libelle_ristourne := 'remise';
    END IF;
end;
/

create trigger GEO_CLIENT_BEF_UPD
    before update
    on GEO_CLIENT
    for each row
DECLARE
    x_user  VARCHAR2(35);
BEGIN

    if (:NEW.maj_wms = '0' and :OLD.maj_wms = '0') or :NEW.maj_wms = '1' then

        -- blocage edi
        UPDATE GEO_CONTAC
        SET NAVOIR_EDI = :NEW.navoir_edi
        WHERE :NEW.navoir_edi <> :OLD.navoir_edi             AND
                CLICEN_REF = :NEW.cli_ref             AND
                MOC_CODE = 'EFT'                 AND
                FLU_CODE = 'FACTUR'                 AND
                CON_TYT = 'E'  AND
                VALIDE = 'O';

        :NEW.maj_wms := '1';

        IF (:NEW.mod_user IS NULL) THEN
            SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
            If (lower(x_user) <> 'ludovic' and lower(x_user) <> 'yoann' and lower(x_user) <> 'isabelle' and  lower(x_user) <> 'jean-pierre' ) then
                :NEW.mod_user := x_user;
                IF (:NEW.mod_date IS NULL) THEN
                    :NEW.mod_date := SYSDATE;
                END IF;
            END If;
        END IF;

    END IF;

END;
/

ALTER TABLE GEO_ADMIN.GEO_CLIENT ADD PRE_SAISIE VARCHAR2(1) NULL;