CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_LITIGE_BEF_INS
BEFORE INSERT
ON GEO_ADMIN.GEO_LITIGE
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
declare
 x_user  varchar2(35);
begin
-- no chrono géré par programme
-- date et user création
    IF (:NEW.mod_user IS NULL) THEN
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :new.mod_date := sysdate;
    END IF;
    IF (:NEW.valide IS NULL) THEN
        :new.valide := 'O';
    END IF;
end;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_LITLIG_BEF_INS
BEFORE INSERT
ON GEO_ADMIN.GEO_LITLIG
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
declare
 x_user  varchar2(35);
 x_nb_devise integer;
begin

    IF :NEW.RES_DEV_CODE  IS NOT NULL THEN
        select count(*) into x_nb_devise  from GEO_DEVISE_REF
        where DEV_CODE = :NEW.RES_DEV_CODE;

        If x_nb_devise = 0 Then
            RAISE_APPLICATION_ERROR (-20001,'Devise erronee');
        End IF;

    END IF;


  --  UPDATE geo_litige SET FL_ENVOI_SYLEG = '1' WHERE LIT_REF = :new.lit_ref;

-- no chrono géré par programme
-- date et user création
    IF (:NEW.mod_user IS NULL) THEN
        select sys_context('USERENV','OS_USER') into x_user from dual;
        :new.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :new.mod_date := sysdate;
    END IF;
    IF (:NEW.valide IS NULL) THEN
        :new.valide := 'O';
    END IF;



end;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_LITIGE_BEF_UPD
BEFORE UPDATE
ON GEO_ADMIN.GEO_LITIGE
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
declare
 x_user  varchar2(35);
begin

	if :new.fl_client_admin = 'O' and :new.fl_fourni_admin = 'O' and (:old.fl_client_admin = 'N' or :old.fl_fourni_admin = 'N') then
		 :new.FL_ENVOI_SYLEG := '1';
	end if;

    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
end;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_LITLIG_BEF_UPD
BEFORE UPDATE
ON GEO_ADMIN.GEO_LITLIG
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
declare
 x_user  varchar2(35);
   x_nb_devise integer;
begin

IF :NEW.RES_DEV_CODE  IS NOT NULL  THEN
        select count(*) into x_nb_devise  from GEO_DEVISE_REF
        where DEV_CODE = :NEW.RES_DEV_CODE;

        If x_nb_devise = 0 Then
            RAISE_APPLICATION_ERROR (-20001,'Devise erronee');
        End IF;

    END IF;


--     UPDATE geo_litige SET FL_ENVOI_SYLEG = '1' WHERE LIT_REF = :new.lit_ref;

    IF (:NEW.mod_user IS NULL) THEN
        SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
        :NEW.mod_user := x_user;
    END IF;
    IF (:NEW.mod_date IS NULL) THEN
        :NEW.mod_date := SYSDATE;
    END IF;
end;
/

