CREATE OR REPLACE TRIGGER "GEO_ADMIN".GEO_HISTO_ORDLOG_DECLO_BEF_INS
BEFORE INSERT ON GEO_HISTO_ORDLOG_DECLO FOR EACH ROW
declare
 x_user  varchar2(35);
  x_num number;
begin

	IF (:NEW.mod_user IS NULL) THEN
	    select sys_context('USERENV','OS_USER') into x_user from dual;
	    :new.mod_user := x_user;
	END IF;
 :new.mod_date := sysdate;

 select SEQ_ORDLOG_DECLO.nextval into x_num from dual;
 :new.HISTO_ORX_REF   := to_char(x_num,'FM099999');


 end;
/
