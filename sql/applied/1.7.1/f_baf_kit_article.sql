CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_BAF_KIT_ARTICLE (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ll_nb_ko number;
    ll_nb_article number;
    ld_pds_brut number;
    ld_pds_net number;

    cursor C_K2000 (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select
            OL.art_ref,
            OL.orl_ref,
            OL.CDE_NB_COL,
            A.PDNET_CLIENT + C.COL_TARE AS PDBRUT,
            A.PDNET_CLIENT,
            OL.EXP_PDS_BRUT,
            OL.EXP_PDS_NET,
            OL.VTE_QTE
        from
            geo_ordlig OL,
            geo_article A,
            geo_colis C
        where
                OL.ORD_REF = ref_ordre AND
                OL.ART_REF = A.ART_REF AND
                C.COL_CODE =  A.Col_CODE AND
                C.ESP_CODE = A.ESP_CODE AND
                C.GEM_CODE = 'KIT';
BEGIN
    -- correspond à f_baf_kit_article.pbl
    res := 0;
    msg := '';

    /*
     *  Si on détecte un article avec groupe embalage = 'KIT' :
     *  Pour tous les article vendus en "gratuit"
     *      - Vente qtt passent à zéro
     *  Pour le kit
     *      - pds net reprend le poids net de l'article si prépesé
     *      - pds brut = somme des poids brut des articles du kit
     *      - palette = 1
	 *      - colis = 1
     *      - vente qtt = 1
     */

    for r in C_K2000(arg_ord_ref)
    loop
        select count(*)
        into ll_nb_ko
        from GEO_ORDLIG
        where ORD_REF  = arg_ord_ref AND
                ART_REF <> r.ART_REF AND
                ART_REF_KIT = r.ART_REF and
                IND_GRATUIT = 'N' ;

        if ll_nb_ko > 0 then
            msg := 'Tous les articles dans le kit doivent être gratuits.';
            return;
        end if;

        select count(*)
        into ll_nb_article
        from GEO_ORDLIG
        where ORD_REF  = arg_ord_ref AND
                ART_REF <> r.ART_REF AND
                ART_REF_KIT = r.ART_REF and
                IND_GRATUIT = 'O' ;

        If ll_nb_article > 0 Then
            -- Kit trouvé

            begin
                select
                    sum(OL.EXP_PDS_BRUT),
                    sum(OL.EXP_PDS_NET)
                into
                    ld_pds_brut,
                    ld_pds_net
                from
                    geo_ordlig OL
                where
                    OL.ORD_REF = arg_ord_ref AND
                    OL.IND_GRATUIT = 'O' and
                    OL.ART_REF <> r.ART_REF AND
                    OL.ART_REF_KIT = r.ART_REF;

                if ld_pds_brut = 0 or ld_pds_net = 0 then
                    msg := 'Erreur sur article en kit : poids net ou brut à 0';
                    return;
                end if;

                update geo_ordlig set VTE_QTE = 0 where ORD_REF = arg_ord_ref and IND_GRATUIT = 'O';
            exception when others then
                msg := 'Erreur sur article en kit : ' || SQLERRM;
                return;
            end;
        else
            ld_pds_net  := r.PDNET_CLIENT * r.CDE_NB_COL;
            ld_pds_brut := r.PDBRUT * r.CDE_NB_COL;
        end if;

        begin
            update geo_ordlig set
                EXP_PDS_BRUT = ld_pds_net,
                EXP_PDS_NET = ld_pds_brut,
                EXP_NB_PAL = CDE_NB_PAL,
                EXP_NB_COL = CDE_NB_COL,
                VTE_QTE = r.CDE_NB_COL
            where ORL_REF = r.ORL_REF;
        exception when others then
            msg := 'Erreur sur article en kit : ' || SQLERRM;
            return;
        end;
    end loop;

    commit;

    res := 1;

END F_BAF_KIT_ARTICLE;
/

