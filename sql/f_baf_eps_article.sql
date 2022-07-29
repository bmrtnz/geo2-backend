CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_BAF_EPS_ARTICLE (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_eps_esp_code varchar(20) := 'EMBALC';
    ls_eps_var_code varchar(20) := 'EPS C';

    ll_nb_eps_trouve number := 0;
    ll_nb_eps_ss_art_asso number;
    ll_nb_eps number;

    ls_orl_ref GEO_ORDLIG.ORL_REF%TYPE;
    ll_qte_vte GEO_ORDLIG.VTE_QTE%TYPE;
    ll_ach_qte GEO_ORDLIG.ACH_QTE%TYPE;

    CURSOR CU_EPS(ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select PROPR_CODE, sum(L.EXP_NB_COL) as ll_nb_colis
        from GEO_ORDLIG L,GEO_ARTICLE A
        where L.ORD_REF = ref_ordre and
              L.ART_REF = A.ART_REF and
            exists (select 1
                    FROM GEO_ARTICLE B
                    where  ('0'|| A.ART_REF_ASS = B.ART_REF or A.ART_REF_ASS = B.ART_REF )and
                            B.ESP_CODE = ls_eps_esp_code and
                            B.VAR_CODE = ls_eps_var_code and
                            B.VALIDE ='O')
        group by PROPR_CODE;
BEGIN
    -- correspond à f_baf_eps_article.pbl
    res := 0;
    msg := '';

    select  count(*)  into ll_nb_eps_ss_art_asso
    FROM GEO_ARTICLE A, GEO_COLIS C ,GEO_ORDLIG L
    where
        L.ORD_REF =  arg_ord_ref  	AND
        L.ART_REF = A.ART_REF 		AND
        C.VALIDE ='O' 						AND
        C.GEST_CODE ='EPS' 			AND
        C.COL_CODE = A.COL_CODE 	AND
        A.ART_REF_ASS IS NULL  		AND
        A.VALIDE ='O';

    -- On cherche une ligne de eps - vive le sport à l'ecolec
    select	count(*) into ll_nb_eps
    from
        geo_ordlig OL,
        geo_article A
    where
            OL.ORD_REF = arg_ord_ref AND
            OL.ART_REF = A.ART_REF AND
            A.ESP_CODE = ls_eps_esp_code AND
            A.VAR_CODE = ls_eps_var_code;

    if (ll_nb_eps > 0) then
        -- EPS trouvé
        for r in CU_EPS(arg_ord_ref)
        loop
            ll_nb_eps_trouve := ll_nb_eps_trouve + 1;

            If r.ll_nb_colis > 0 then
                begin
                    select ORL_REF into ls_orl_ref
                    from
                        geo_ordlig OL,
                        geo_article A
                    where
                        OL.ORD_REF = arg_ord_ref 		AND
                        OL.ART_REF = A.ART_REF 			AND
                        OL.propr_code = r.PROPR_CODE 	AND
                        A.ESP_CODE = ls_eps_esp_code 	AND
                        A.VAR_CODE = ls_eps_var_code;

                    update geo_ordlig
                    set VTE_QTE = r.ll_nb_colis,
                        VTE_BTA_CODE = 'UNITE',
                        ACH_QTE = r.ll_nb_colis,
                        ACH_BTA_CODE = 'UNITE',
                        EXP_PDS_BRUT = 0,
                        EXP_PDS_NET = 0
                    where ORD_REF = arg_ord_ref and
                            ORL_REF = ls_orl_ref;
                exception when others then
                    msg := 'Erreur sur article en eps : ' || SQLERRM;
                    return;
                end;
            end if;

            select VTE_QTE, ACH_QTE
            into ll_qte_vte, ll_ach_qte
            FROM GEO_ORDLIG
            where ORD_REF = arg_ord_ref and
                    ORL_REF = ls_orl_ref;

            If COALESCE(ll_qte_vte, 0) = 0 or coalesce(ll_ach_qte, 0) = 0 Then
                msg := 'Manque quantite sur article eps' || SQLERRM;
                return;
            End If;
        end loop;

        IF ll_nb_eps_trouve = 0 Then
            msg := 'Erreur : Un kit EPS sans article associé, contrôler vos articles';
            rollback;
            return;
        End If;

        IF ll_nb_eps_ss_art_asso > 0 Then
            msg := 'Erreur : Un article en colis EPS n a pas d article associé';
            rollback;
            return;
        End If;

        commit;
    end if;

    res := 1;
END F_BAF_EPS_ARTICLE;
/

