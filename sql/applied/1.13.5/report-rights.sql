ALTER TABLE GEO_USER     			ADD IND_REPORT_ACH VARCHAR2(1) DEFAULT 'N';

ALTER TABLE GEO_USER                    	     	ADD IND_REPORT_VTE VARCHAR2(1) DEFAULT 'N';

ALTER TABLE GEO_USER                         		ADD IND_REPORT_PROP VARCHAR2(1) DEFAULT 'N';

ALTER TABLE GEO_USER                        		ADD IND_REPORT_EXP VARCHAR2(1) DEFAULT 'N';

ALTER TABLE GEO_USER                        		ADD IND_REPORT_PAL VARCHAR2(1) DEFAULT 'N';





COMMENT ON COLUMN GEO_USER.IND_REPORT_ACH  				IS 'report prix achat sur toute la grille';

COMMENT ON COLUMN GEO_USER.IND_REPORT_VTE  				IS 'report prix vente sur toute la grille';

COMMENT ON COLUMN GEO_USER.IND_REPORT_PROP  				IS 'report proprietaire sur toute la grille';

COMMENT ON COLUMN GEO_USER.IND_REPORT_EXP  				IS 'report expediteur sur toute la grille';

COMMENT ON COLUMN GEO_USER.IND_REPORT_PAL				IS 'report type palette sur toute la grille';





update  GEO_USER SET 				IND_REPORT_ACH = 'O',

                                    IND_REPORT_VTE = 'O',

                                    IND_REPORT_PROP = 'O',

                                    IND_REPORT_EXP ='O',

IND_REPORT_PAL ='O'

where SCO_CODE ='GB'
