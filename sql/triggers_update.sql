CREATE OR REPLACE TRIGGER "GEO_ADMIN".GEO_ORDRE_BEF_UPD
BEFORE UPDATE ON GEO_ORDRE FOR EACH ROW
DECLARE
 x_user  VARCHAR2(35);
 x_nb_demipal number(3);
BEGIN
 
 
 IF (:NEW.mod_user IS NULL) THEN
	SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
	:NEW.mod_user := x_user;
 END IF;
 :NEW.mod_date := SYSDATE;
 IF NOT (:NEW.version_ordre = :OLD.version_ordre)
  OR (:OLD.version_ordre IS NULL AND :NEW.version_ordre IS NOT NULL) THEN
            :NEW.version_ordre_date := :NEW.mod_date;
 END IF;
 IF NOT (:NEW.version_detail = :OLD.version_detail)
  OR (:OLD.version_detail IS NULL AND :NEW.version_detail IS NOT NULL) THEN
            :NEW.version_detail_date := :NEW.mod_date;
 END IF;

 :NEW.came_code := TO_CHAR(ADD_MONTHS(:NEW.depdatp,-6),'YY');
 :NEW.camf_code := TO_CHAR(ADD_MONTHS(:NEW.datfac,-6),'YY');
 :NEW.depdatp_asc := TO_CHAR(:NEW.depdatp,'yyyymmdd');

 IF (:NEW.REF_CLI <> :OLD.REF_CLI) THEN
     UPDATE GEO_LITIGE SET REF_CLI = :NEW.REF_CLI WHERE ORD_REF_ORIGINE = :NEW.ORD_REF;
 END IF;

  IF (:NEW.PAL_NB_SOL <> :OLD.PAL_NB_SOL) THEN
         select sum(DEMIPAL_IND) into x_nb_demipal
    from GEO_ORDLIG
    where ORD_REF =:NEW.ORD_REF and
          VALIDE = 'O';


        :NEW.PAL_NB_SOLTRANS := round(:NEW.PAL_NB_SOL - (x_nb_demipal * 0.5));

 END IF;



END;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDLIG_BEF_INS
BEFORE INSERT
ON GEO_ADMIN.GEO_ORDLIG
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
 x_num NUMBER;
 x_user  VARCHAR2(35);
 x_bac_code VARCHAR2(12);
BEGIN
-- sequence pour PK fait par PB
-- date et user création
 
 :NEW.pro_ref  := :NEW.pde_ref;
 IF (:NEW.mod_user IS NULL) THEN
	SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
	:NEW.mod_user := x_user;
 END IF;
 :NEW.mod_date := SYSDATE;
 :NEW.valide := 'O';


IF :new.propr_code is  NULL THEN
      :new.propr_code := :new.fou_code;
END IF;

IF :new.IND_CHECK_FRAUDE is  NULL THEN
      :new.IND_CHECK_FRAUDE := 'N';
END IF;

 select bac_code into x_bac_code from geo_fourni where fou_code = :new.propr_code ;
 exception when NO_DATA_FOUND then null;
 :new.bac_code := x_bac_code;


END;
/


CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDLIG_BEF_UPD
BEFORE UPDATE
ON GEO_ADMIN.GEO_ORDLIG
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
 x_machine  VARCHAR2(35);
 x_user  VARCHAR2(35);
 x_bac_code VARCHAR2(12);
 x_count integer;
 x_grp_rgp integer;
 x_nb_grp_rgp integer;
 -- on ne modifie le time stamp que pour les champs accessibles au user
 -- ceci permet de ne pas générer de fausse détection dans le cadre des confirmations de commande stations
 -- pour une gestion la meilleure possible des annule-et-remplace
 -- ceci permet aussi de modifier l'ordre des lignes (orl_lig) sans activer le time stamp
BEGIN

SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;


IF :new.propr_code is  NULL THEN
      :new.propr_code := :new.fou_code;
end if;

SELECT COUNT(*) INTO x_count
FROM geo_fourni WHERE fou_code = :new.propr_code ;


IF  x_count  <> 0 THEN
     select bac_code into x_bac_code from geo_fourni where fou_code = :new.propr_code ;
     :new.bac_code := x_bac_code;
ELSE
     :new.bac_code := NULL;
END IF;


/*
select bac_code into x_bac_code from geo_fourni where fou_code = :new.propr_code ;
  exception when NO_DATA_FOUND then null;
 :new.bac_code := x_bac_code; */


IF :NEW.cde_nb_pal <> :OLD.cde_nb_pal OR
   :NEW.pal_nb_col <> :OLD.pal_nb_col OR
   :NEW.cde_nb_col <> :OLD.cde_nb_col OR
   :NEW.exp_nb_pal <> :OLD.exp_nb_pal OR
   :NEW.exp_nb_col <> :OLD.exp_nb_col OR
   :NEW.exp_pds_net <> :OLD.exp_pds_net OR
   :NEW.fou_code <> :OLD.fou_code OR
   :NEW.vte_pu <> :OLD.vte_pu OR
   :NEW.vte_bta_code <> :OLD.vte_bta_code OR
   :NEW.vte_qte <> :OLD.vte_qte OR
   :NEW.ach_pu <> :OLD.ach_pu OR
   :NEW.ach_bta_code <> :OLD.ach_bta_code OR
   :NEW.ach_qte <> :OLD.ach_qte OR
   :NEW.pal_code <> :OLD.pal_code OR
   :NEW.art_ref <> :OLD.art_ref OR
   :NEW.frais_pu <> :OLD.frais_pu OR
   :NEW.frais_unite <> :OLD.frais_unite OR
   :NEW.lib_dlv <> :OLD.lib_dlv OR
   :NEW.bac_code <> :OLD.bac_code  THEN


 IF (:NEW.mod_user IS NULL) THEN
    SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
	:NEW.mod_user := x_user;
 END IF;
 :NEW.pro_ref  := :NEW.pde_ref;
 :NEW.mod_date := SYSDATE;

END IF;
/*
IF :NEW.cde_nb_pal <> :OLD.cde_nb_pal OR
   :NEW.pal_nb_col <> :OLD.pal_nb_col OR
   :NEW.cde_nb_col <> :OLD.cde_nb_col OR
   :NEW.fou_code <> :OLD.fou_code OR
   :NEW.vte_pu <> :OLD.vte_pu OR
   :NEW.vte_bta_code <> :OLD.vte_bta_code OR
   :NEW.ach_pu <> :OLD.ach_pu OR
   :NEW.ach_bta_code <> :OLD.ach_bta_code OR
   :NEW.pal_code <> :OLD.pal_code OR
   :NEW.art_ref <> :OLD.art_ref OR
   :NEW.lib_dlv <> :OLD.lib_dlv  THEN

    select count(*) into x_nb_grp_rgp from GEO_GEST_REGROUP
        where    ORD_REF_ORIG = :NEW.ORD_REF and 
              ORL_REF_ORIG = :NEW.ORL_REF ;
 
   IF x_nb_grp_rgp > 0  THEN 
    select GRP_RGP into x_grp_rgp 
    from GEO_GEST_REGROUP
    where     ORD_REF_ORIG = :NEW.ORD_REF and 
              ORL_REF_ORIG = :NEW.ORL_REF ;
              
     select count(*) into x_nb_grp_rgp from GEO_GEST_REGROUP
        where ORD_REF_ORIG = :NEW.ORD_REF and 
                GRP_RGP  = x_grp_rgp;
            
    IF x_nb_grp_rgp > 0  THEN 
     DELETE GEO_GEST_REGROUP
        where ORD_REF_ORIG = :NEW.ORD_REF and 
                GRP_RGP  = x_grp_rgp; 
    END IF;                  
              
              
    End If;          

END IF;
*/


IF :NEW.FLVERFOU <> :OLD.FLVERFOU THEN
   SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
   :NEW.USER_VERFOU := x_user;
   :NEW.DATE_VERFOU := SYSDATE;
END IF;


IF :NEW.FLVERTRP <> :OLD.FLVERTRP THEN
   SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
   :NEW.USER_VERTRP := x_user;
   :NEW.DATE_VERTRP := SYSDATE;
END IF;

END;
/

CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDLIG_BEF_INS
BEFORE INSERT
ON GEO_ADMIN.GEO_ORDLIG
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
 x_num NUMBER;
 x_user  VARCHAR2(35);
 x_bac_code VARCHAR2(12);
BEGIN
-- sequence pour PK fait par PB
-- date et user création
 IF (:NEW.mod_user IS NULL) THEN
  SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
  :NEW.mod_user := x_user;
 END IF;
 :NEW.pro_ref  := :NEW.pde_ref;
 :NEW.mod_date := SYSDATE;
 :NEW.valide := 'O';


IF :new.propr_code is  NULL THEN
      :new.propr_code := :new.fou_code;
END IF;

IF :new.IND_CHECK_FRAUDE is  NULL THEN
      :new.IND_CHECK_FRAUDE := 'N';
END IF;

 select bac_code into x_bac_code from geo_fourni where fou_code = :new.propr_code ;
 exception when NO_DATA_FOUND then null;
 :new.bac_code := x_bac_code;


END;
/

CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_ORDLOG_BEF_INS_OR_UPD
BEFORE INSERT OR UPDATE
ON GEO_ADMIN.GEO_ORDLOG
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
 x_user  VARCHAR2(35);
BEGIN
 IF (:NEW.mod_user IS NULL) THEN
	SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
	:NEW.mod_user := x_user;
 END IF;
 :NEW.mod_date := SYSDATE;
 :NEW.datdep_fou_p_yyyymmdd := TO_CHAR(:NEW.datdep_fou_p,'YYYYMMDD');
 IF (:NEW.datdep_grp_p IS NULL) THEN
 :NEW.datdep_grp_p := :NEW.datdep_fou_p;
  END IF;

END;
/
CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_TRACA_DETAIL_PAL_BEF_INS
BEFORE INSERT ON GEO_ADMIN.GEO_TRACA_DETAIL_PAL FOR EACH ROW
DECLARE
 x_num NUMBER;
 x_user  VARCHAR2(35);
BEGIN
 IF :NEW.REF_TRACA IS NULL THEN
  SELECT SEQ_TRACA_DETAIL_PAL.NEXTVAL INTO x_num FROM dual;
  :NEW.REF_TRACA := x_num;
 END IF;
 IF (:NEW.mod_user IS NULL) THEN
	SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
	:NEW.mod_user := x_user;
 END IF;	
 :NEW.mod_date := SYSDATE;
 :NEW.valide := 'O';
END;
/

CREATE OR REPLACE TRIGGER GEO_ADMIN.GEO_TRACA_LIGNE_BEF_UPD
BEFORE UPDATE ON GEO_ADMIN.GEO_TRACA_LIGNE FOR EACH ROW
DECLARE
 x_user  VARCHAR2(35);
BEGIN
 IF  :NEW.mod_user IS NULL THEN
 SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;
 :NEW.mod_user := x_user;
 END IF;
 :NEW.mod_date := SYSDATE;
END;
/
CREATE OR REPLACE TRIGGER "GEO_ADMIN".GEO_STOCK_BEF_UPD
BEFORE UPDATE ON GEO_STOCK FOR EACH ROW
declare
 x_user  varchar2(35);
begin
  IF  :NEW.mod_user IS NULL THEN
 select sys_context('USERENV','OS_USER') into x_user from dual;
 :new.mod_user := x_user; 
 END IF;
 
 :new.mod_date := sysdate;
end;
/


