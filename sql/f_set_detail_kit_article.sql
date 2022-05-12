-- f_set_detail_kit_article

-- Si on détecte un article avec groupe embalage = 'KIT' :
-- Pour tous les article vendus en "gratuit"
--		- Vente qtt passent à zéro
-- Pour le kit
--		- pds net reprend le poids net de l'article si prépesé
--		- pds brut = somme des poids brut des articles du kit
--		- palette = 1
--		- colis = 1
--		- vente qtt = 1

CREATE OR REPLACE PROCEDURE F_SET_DETAIL_KIT_ARTICLE (
    arg_ord_ref IN geo_gest_regroup.ORD_REF_ORIG%TYPE,
    arg_fou_code IN geo_gest_regroup.fou_code_orig%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    -- string array_art_ref[]
    -- string array_prepese[]
    -- string array_orl_ref[]
    -- long array_nb_colis[]
    -- double array_pds_net_article[]

    ll_nb_art_ref number := 0;
    ll_count number;
    ls_art_ref varchar2(50);

    ls_orl_ref varchar2(50);
    ld_pds_brut number;
    ld_pds_net number;
    ld_pds_brut_article number;
    ld_pds_net_article number;
    ll_nb_colis number;
    ls_prepese varchar2(50);
    i number;

    -- On cherche une ligne de kit
    cursor C1 is
    select
        OL.ORL_REF,
        A.ART_REF,
        A.COL_PREPESE,
        OL.CDE_NB_COL,
        A.PDNET_CLIENT
    from
        geo_ordlig OL,
        geo_article A,
        geo_colis C
    where 
        OL.ORD_REF = arg_ord_ref AND
        OL.ART_REF = A.ART_REF AND
        C.COL_CODE =  A.Col_CODE AND
        C.ESP_CODE = A.ESP_CODE AND
        C.GEM_CODE = 'KIT';
BEGIN
    msg := '';
    res := 0;

    -- Pour chaque KIT trouvés on met à jour les lignes associée et le kit
    for r in C1
    loop
        
        select sum(OL.EXP_PDS_BRUT), sum(OL.EXP_PDS_NET), count(OL.ORL_REF)
        into  ld_pds_brut, ld_pds_net, ll_count
        from geo_ordlig OL
        where  OL.ORD_REF = arg_ord_ref AND OL.ART_REF_KIT = r.art_ref and OL.ART_REF <> r.art_ref;

        if r.col_prepese = 'O' then
            ld_pds_net := r.PDNET_CLIENT * r.CDE_NB_COL;
        end if;
        
        if ll_count <> 0 then
            update geo_ordlig set EXP_PDS_BRUT = ld_pds_brut, EXP_PDS_NET = ld_pds_net, EXP_NB_PAL = CDE_NB_PAL, EXP_NB_COL = CDE_NB_COL, VTE_QTE = ll_nb_colis, ACH_QTE = '0' where ORL_REF = r.ORL_REF;
            update geo_ordlig set VTE_QTE = 0 where ORD_REF = arg_ord_ref and ART_REF_KIT = r.art_ref and ART_REF <> r.art_ref;
        end if;
        
    end loop;

    -- Bruno le 24/08/20 ne pas commiter car y a un rollback dans la cloture du détail qui ne fonctionne plus alors
    --commit;
    --

    res := 1;
    msg := 'OK';
END;
/
