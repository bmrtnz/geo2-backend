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
 x_col_pumo NUMBER(8,3);
 x_col_pump NUMBER(8,3);

 -- on ne modifie le time stamp que pour les champs accessibles au user
 -- ceci permet de ne pas générer de fausse détection dans le cadre des confirmations de commande stations
 -- pour une gestion la meilleure possible des annule-et-remplace
 -- ceci permet aussi de modifier l'ordre des lignes (orl_lig) sans activer le time stamp
BEGIN

SELECT sys_context('USERENV','OS_USER') INTO x_user FROM dual;

IF   ((:old.art_ref IS NULL ) OR (:new.art_ref <> :old.art_ref))  THEN
      select col_pumo, col_pump into x_col_pumo,x_col_pump
      from GEO_COLIS,GEO_ARTICLE
      where     GEO_ARTICLE.ART_REF =   :new.art_ref and
                GEO_ARTICLE.ESP_CODE = GEO_COLIS.ESP_CODE and
                GEO_ARTICLE.COL_CODE = GEO_COLIS.COL_CODE;

    :new.col_pumo := x_col_pumo;
    :new.col_pump := x_col_pump;

end if;




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
 ELSIF (:NEW.mod_user <> :OLD.mod_user) THEN
     INSERT INTO geo_ordre_save_log(ord_ref, geo_user)
     VALUES(:NEW.ord_ref, :NEW.mod_user);
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


