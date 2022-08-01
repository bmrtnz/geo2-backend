CREATE OR REPLACE TRIGGER "GEO_ADMIN".GEO_HISTO_MODIF_DETAIL
BEFORE INSERT
ON GEO_HISTO_MODIF_DETAIL
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
  x_user  varchar2(35);
BEGIN
    IF (:NEW.mod_user IS NULL) THEN
	    select sys_context('USERENV','OS_USER') into x_user from dual;
	    :new.mod_user := x_user;
    END IF;
    :new.mod_date := sysdate;
END ;
/
