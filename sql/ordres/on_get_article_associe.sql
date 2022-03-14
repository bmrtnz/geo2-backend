-- of_get_article_associe
-- permet de créer une ligne (emballage UPS ou IFCO) si l'article passé en paramètre possède un article associé
-- on renvoie le n° article associé
-- AR 14/06/12 création

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_GET_ARTICLE_ASSOCIE" (
    arg_art_ref AVI_ART_GESTION.art_ref%type,
    ls_art_ref_ass out varchar2
) AS
    ls_rc varchar2(50);
begin

    -- y a t'il un article asocié ?
    select art_ref_ass into ls_art_ref_ass
    from avi_art_gestion
    where art_ref = arg_art_ref;

    -- on vérifie que cet article associé existe bien
    select art_ref into ls_rc
    from avi_art_gestion
    where art_ref = ls_art_ref_ass;

    return;

-- pb base : on sort
exception when others then
    ls_art_ref_ass := null;
    return;
end;