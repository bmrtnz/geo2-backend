ALTER TABLE GEO_USER ADD IND_BAR_DEF_VISIBLE VARCHAR2(1) DEFAULT 'O';
commit;
COMMENT ON COLUMN GEO_USER.IND_BAR_DEF_VISIBLE IS 'indicateur de visibilité de la barre de défilement';
commit;
/
