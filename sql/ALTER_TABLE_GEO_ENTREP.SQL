ALTER TABLE GEO_ADMIN.GEO_ENTREP
  ADD EDI_PAL_INTER VARCHAR2(1) DEFAULT 'N'; 

COMMENT ON COLUMN GEO_ADMIN.GEO_ENTREP.EDI_PAL_INTER IS 'Entrepôt nécessitant obligatoirement une palette intermediaire';

