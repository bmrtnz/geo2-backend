-- ne fait rien à part lancer le trigger d'update
UPDATE GEO_ORDFRA
SET ACH_QTE = ACH_QTE
WHERE ACH_QTE IS NULL;
/

