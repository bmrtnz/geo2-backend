ALTER TABLE GEO_USER            ADD  IND_BAR_DEF_HT             VARCHAR2(1) DEFAULT 'O';
ALTER TABLE GEO_USER            ADD  IND_BAR_DEF_BS             VARCHAR2(1) DEFAULT 'O';
ALTER TABLE GEO_USER            ADD  IND_PLANDP_DIF_EXP     VARCHAR2(1)     DEFAULT 'O';


COMMENT ON COLUMN GEO_USER.IND_BAR_DEF_HT                  IS 'barre de defilement haut';
COMMENT ON COLUMN GEO_USER.IND_BAR_DEF_BS                  IS 'barre de defilement bas';
COMMENT ON COLUMN GEO_USER.IND_PLANDP_DIF_EXP                  IS 'valeur boite à cocher sur le planing départ pour différence sur expedition';
