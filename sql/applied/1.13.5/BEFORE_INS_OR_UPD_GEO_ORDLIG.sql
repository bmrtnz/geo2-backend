CREATE OR REPLACE TRIGGER GEO_ADMIN.BEFORE_INS_OR_UPD_GEO_ORDLIG
BEFORE INSERT OR UPDATE
ON GEO_ADMIN.GEO_ORDLIG 
REFERENCING NEW AS New OLD AS Old
FOR EACH ROW
DECLARE
strGTIN_COLIS  VARCHAR2(35);
strGTIN_UC  VARCHAR2(35);
strEDI_NUM    NUMBER;
strNB_EDI NUMBER;
strIND_MODIF VARCHAR2(2);
intNB_PIECE INTEGER; 


BEGIN
   SELECT REF_EDI_ORDRE INTO strEDI_NUM FROM GEO_ORDRE WHERE ORD_REF = :NEW.ORD_REF;
   if strEDI_NUM IS NULL OR strEDI_NUM = '' THEN
       if :NEW.ART_REF IS NOT NULL AND (:NEW.ART_REF <> :OLD.ART_REF OR :NEW.ART_REF_KIT IS NULL) THEN
            :NEW.ART_REF_KIT := :NEW.ART_REF;
                
            SELECT 
                CASE WHEN GTIN_COLIS IS NOT NULL THEN GTIN_COLIS ELSE GTIN_COLIS_BW END,
                CASE WHEN GTIN_UC IS NOT NULL THEN GTIN_UC ELSE GTIN_UC_BW END
                 INTO strGTIN_COLIS, strGTIN_UC
            FROM GEO_ARTICLE WHERE ART_REF = :NEW.ART_REF;
            
            
            IF :OLD.GTIN_COLIS_KIT IS NULL THEN
               :NEW.GTIN_COLIS_KIT := strGTIN_COLIS;
           ELSE
               SELECT count(*) into strNB_EDI
             FROM GEO_CONTAC , GEO_ORDRE 
              where  GEO_ORDRE.ORD_REF =:NEW.ORD_REF and
                         CON_FLUVAR = 'AGP' AND 
                         ((CON_TIERS  = CLI_CODE and CON_TYT = 'C') OR (CON_TIERS  = CEN_CODE and CON_TYT = 'E')) and
                         GEO_ORDRE.SOC_CODE = GEO_CONTAC .SOC_CODE;        
            END IF;           
               
               
               
            IF strGTIN_UC IS NOT NULL and ( strNB_EDI IS NULL OR strNB_EDI = 0) THEN
                :NEW.GTIN_COLIS_KIT := strGTIN_UC;
            END IF;
       END IF;
    END IF;
       
    
    begin
    SELECT 'OK' 
    into strIND_MODIF
    from GEO_ORDLOG, GEO_ORDRE
    where GEO_ORDRE.ORD_REF = :NEW.ORD_REF and 
          GEO_ORDRE.FLBAF <> 'O' and
          GEO_ORDLOG.ORD_REF   =:NEW.ORD_REF and 
          GEO_ORDLOG.FOU_CODE  =:NEW.FOU_CODE and
          GEO_ORDLOG.FLAG_EXPED_FOURNNI ='O' and
          :OLD.VTE_BTA_CODE <> :NEW.VTE_BTA_CODE;
          
   If  strIND_MODIF ='OK' THEN

        CASE :NEW.VTE_BTA_CODE 
           WHEN 'COLIS'         THEN :NEW.VTE_QTE := :NEW.EXP_NB_COL; 
            WHEN 'KILO'            THEN :NEW.VTE_QTE := :NEW.EXP_PDS_NET;
            WHEN 'PAL'             THEN :NEW.VTE_QTE := :NEW.EXP_NB_PAL;
            WHEN 'TONNE'        THEN :NEW.VTE_QTE := round(:NEW.EXP_PDS_NET/ 1000, 3);
            WHEN 'CAMION'        THEN :NEW.VTE_QTE :=0;
            ELSE
               begin
           select u_par_colis into intNB_PIECE
                from GEO_ARTICLE
              where ART_REF=:NEW.ART_REF;
              
                  exception when others then
                  intNB_PIECE :=0;
             end;   
            IF  intNB_PIECE IS NULL THen 
                intNB_PIECE :=0;
            END IF;
            :NEW.VTE_QTE := round(:NEW.EXP_NB_COL* intNB_PIECE, 0);
         
      END CASE;
    end if;
          
    exception when others then
        null;
         
    end;
    

END ;
/