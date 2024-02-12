-- f_get_info_resa

-- retourne des infos sur les réservations actives liées à une ligne d'ordre
-- totaux de : quantité départ (toutes lignes), quantité réservée (toutes lignes), quantité réservée pour la ligne, nbre de réservations pour la ligne
-- on utilise un curseur group by S.QTE_INI, S.QTE_RES, S.STO_REF; pour éviter le produit cartésien sur les qtés ini et res du stock si plusieurs mouvements
-- AR 04/05/09 création
-- permet de détecter si l'algo de réservation se solde par un dispo négatif (utilisé pour actualiser la ligne d'ordre du nbre de réservations avec signe plus ou moins selon dispo ou non)

CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_GET_INFO_RESA (
    arg_orl_ref IN varchar2,
	res OUT NUMBER,
	msg OUT varchar2,
    ll_tot_qte_ini OUT number,
    ll_tot_qte_res OUT number,
    ll_tot_mvt_qte OUT number,
    ll_tot_nb_resa OUT number
)
AS
    ll_qte_ini number;
    ll_qte_res number;
    ll_mvt_qte number;
    ll_nb_resa number;
    cursor C1 is
        select S.QTE_INI, S.QTE_RES, sum(M.MVT_QTE) as mvt_qte, count(M.STM_REF) as nb_resa
        from geo_stomvt M, geo_stock S
        where M.orl_ref = arg_orl_ref
        and S.sto_ref = M.sto_ref
        group by S.QTE_INI, S.QTE_RES, S.STO_REF;
BEGIN
	res := 1;
	msg := '';

    ll_tot_qte_ini := 0;
    ll_tot_qte_res := 0;
    ll_tot_mvt_qte := 0;
    ll_tot_nb_resa := 0;

    for r in C1
    loop
        ll_tot_qte_ini	:= ll_tot_qte_ini + r.qte_ini;
        ll_tot_qte_res	:= ll_tot_qte_res + r.qte_res;
        ll_tot_mvt_qte	:= ll_tot_mvt_qte + r.mvt_qte;
        ll_tot_nb_resa	:= ll_tot_nb_resa + r.nb_resa;
    end loop;

    res := 1;
    msg := 'OK';

END F_GET_INFO_RESA;
/

