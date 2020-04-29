-- TABLE GEO_ARTICLE : ajout des champs pour nouvelles structures (ajout par Alex)
alter table geo_article add (
    REF_EMBALLAGE VARCHAR2(6 BYTE),
    REF_MAT_PREM VARCHAR2(6 BYTE),
    REF_NORMALISATION VARCHAR2(6 BYTE),
    REF_CDC VARCHAR2(6 BYTE)
);
