CREATE OR REPLACE PROCEDURE F_RESA_AUTO_ORDRE(
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_username IN varchar2,
    res OUT number,
    msg OUT varchar2,
    result OUT p_str_tab_type
)
AS

    -- s_multi_long	ll_return
    ll_rc number;
    ll_instr number;
    ls_rc varchar2(50);
    ls_comment varchar2(500);
    ls_crlf varchar2(50)	 := '~r~n';
    ls_ord_ref varchar2(50);
    ls_nordre varchar2(50);
    ls_cen_code varchar2(50);
    ls_orl_ref varchar2(50);
    ls_orl_lig varchar2(50);
    ls_fou_code varchar2(50);
    ls_prop_code varchar2(50);
    ls_art_ref varchar2(50);
    ls_var_code varchar2(50);
    ls_cat_code varchar2(50);
    ls_cam_code varchar2(50);
    ls_col_code varchar2(50);
    ls_art_desc varchar2(50);
    ls_fou_code_stomvt varchar2(50);
    ls_prop_code_stomvt varchar2(50);

    ll_nb_col number;							-- nbre de colis en commande
    ll_nb_resa number;						-- nbre de resa nouvelles
    ll_nb_deja_resa number;				-- nbre de resa déja effectuées
    ll_nb_resa_upd number;					-- nbre de resa à actualiser dans geo_ordlig (négatif si qté dispo < 0)
    ls_orl_ref_array p_str_tab_type := p_str_tab_type();				-- liste des PK des lignes de l'ordre
    ls_orl_lig_array p_str_tab_type := p_str_tab_type();				-- liste des n° de ligne des lignes de l'ordre
    ls_fou_code_array p_str_tab_type := p_str_tab_type();			-- liste des codes fournisseur des lignes de l'ordre
    ls_prop_code_array p_str_tab_type := p_str_tab_type();			-- liste des codes fournisseur des lignes de l'ordre
    ls_art_ref_array p_str_tab_type := p_str_tab_type();				-- liste des codes article des lignes de l'ordre
    ls_art_desc_array p_str_tab_type := p_str_tab_type();			-- table des desciptions article
    ls_pal_code_client_array p_str_tab_type := p_str_tab_type();		-- liste des palettes du client

    type p_nbr_tab_type is table of number;
    ll_cde_nb_col_array p_nbr_tab_type := p_nbr_tab_type();			-- liste des nbre de lignes réservées
    ll_stock_nb_resa_array p_nbr_tab_type := p_nbr_tab_type();		-- liste des nbre de lignes réservées
    ll_array_ind number := 0;
    ll_ind number;
    ll_qte_dispo number;						-- qté restante après réservation sur une ligne
    ll_qte_deja_resa number;						-- qté déja réservée dans stomvt pour une ligne d'ordre
    ll_nb_row number;						-- row courant dans arg_ds
    ib_warning boolean;					-- indicateur avertissement ou erreur
    ls_ordre_desc varchar2(50);
    ls_pal_code_client varchar2(50);
    ls_info_stock varchar2(50);
    ls_dispo varchar2(50);
BEGIN
    res := 0;
    msg := '';
    result := p_str_tab_type();

    begin
        select ord_ref, nordre, cen_code into ls_ord_ref, ls_nordre, ls_cen_code from geo_ordre where ord_ref = arg_ord_ref;
        ls_ordre_desc	:= 'ordre ' || ls_nordre || '/' || ls_cen_code;
    exception when others then
        msg	:= 'ordre ref=' || arg_ord_ref || ' - erreur lecture ' || SQLERRM;
        return;
    end;

    -- on récupère les PK, code fournisseur, nbre de colis cde et nbre de réservations courantes des lignes de l'ordre
    -- on récupère les données de l'ordre
    declare
        cursor C1 is
            select L.orl_ref, L.orl_lig, L.fou_code, L.propr_code, L.art_ref, L.cde_nb_col, L.stock_nb_resa, A.var_code, A.cat_code, A.cam_code, A.col_code, L.pal_code
            from geo_ordlig L, geo_article A
            where L.ord_ref = arg_ord_ref
            and A.art_ref = L.art_ref;
    begin

        for r in C1 loop
            if r.fou_code is null then r.fou_code := ''; end if;
            if r.propr_code is null then r.propr_code := ''; end if;
            if r.art_ref is null then r.art_ref := ''; end if;
            if r.cde_nb_col is null then r.cde_nb_col := 0; end if;
            if r.stock_nb_resa is null then r.stock_nb_resa := 0; end if;
            ll_array_ind := ll_array_ind + 1;
            ls_orl_ref_array.extend();
            ls_orl_ref_array(ll_array_ind)			:= r.orl_ref;
            ls_orl_lig_array.extend();
            ls_orl_lig_array(ll_array_ind)			:= r.orl_lig;
            ls_fou_code_array.extend();
            ls_fou_code_array(ll_array_ind)		:= r.fou_code;
            ls_prop_code_array.extend();
            ls_prop_code_array(ll_array_ind)		:= r.propr_code;
            ls_art_ref_array.extend();
            ls_art_ref_array(ll_array_ind)			:= r.art_ref;
            ll_cde_nb_col_array.extend();
            ll_cde_nb_col_array(ll_array_ind)		:= r.cde_nb_col;
            ll_stock_nb_resa_array.extend();
            ll_stock_nb_resa_array(ll_array_ind)	:= r.stock_nb_resa;
            ls_art_desc	:= r.var_code || ' cat ' || r.cat_code || ' cal ' ||  r.cam_code || ' emb ' ||  r.col_code;
            ls_art_desc_array.extend();
            ls_art_desc_array(ll_array_ind)			:= ls_art_desc;
            ls_pal_code_client_array.extend();
            ls_pal_code_client_array(ll_array_ind):= r.pal_code;
        end loop;

    end;

        -- on attaque les lignes
    for ll_ind in 1 .. ll_array_ind loop
        ib_warning	:= true;		-- warning par défaut
        ls_comment			:= ls_art_desc_array(ll_ind) || ' -->';
        ls_orl_ref			:= ls_orl_ref_array(ll_ind);
        ls_orl_lig				:= ls_orl_lig_array(ll_ind);
        ls_fou_code			:= ls_fou_code_array(ll_ind);
        ls_prop_code		:= ls_prop_code_array(ll_ind);
        ls_art_ref			:= ls_art_ref_array(ll_ind);
        ll_nb_col				:= ll_cde_nb_col_array(ll_ind);
        ll_nb_deja_resa	:= ll_stock_nb_resa_array(ll_ind);
        ls_pal_code_client := ls_pal_code_client_array(ll_ind);

        if ls_fou_code = '' then
            ls_comment	:= ls_comment || ' fournisseur non précisé';
        end if;
        if ls_prop_code = '' then
            ls_comment	:= ls_comment || ' propriétaire non précisé';
        end if;
        if ls_art_ref = '' then
            ls_comment	:= ls_comment || ' article non précisé';
        end if;
        if ll_nb_col = 0 then
            ls_comment	:= ls_comment || ' aucune quantité en commande';
        end if;

            -- on demande la quantité déja réservée
        select sum(mvt_qte) into ll_qte_deja_resa from geo_stomvt where orl_ref = ls_orl_ref;
        if ll_qte_deja_resa is null then ll_qte_deja_resa := 0; end if;
        if ll_qte_deja_resa = ll_nb_col then
                -- cumul resa = qté en commande
                -- pas besoin de réserver, c'est déja fait
                -- mais on va vérifier qu'il n'y a pas eu de changement de fournisseur entre-temps
            begin
                select distinct fou_code, prop_code into ls_fou_code_stomvt, ls_prop_code_stomvt from geo_stock, geo_stomvt
                where geo_stock.sto_ref = geo_stomvt.sto_ref
                and geo_stomvt.orl_ref = ls_orl_ref;
            exception when others then
                    -- il y a sans doute plusieurs fournisseurs impliqués dans les resa de cette ligne (cas théoriquement iminstrsible)
                ls_comment	:= ls_comment || 'ERREUR : réservations sur plusieurs fournisseurs - gérer le cas manuellement ';
                goto suite;
            end;
            if ls_fou_code_stomvt <> ls_fou_code or ls_prop_code_stomvt <> ls_prop_code then
                    -- il y a différence de fournisseurs entre réservations et ligne d'ordre (pas bon du tout)
                ls_comment	:= ls_comment || 'réservations sur ' || ls_fou_code_stomvt || '/' || ls_prop_code_stomvt || ' MAIS ligne affectée à ' || ls_fou_code || '/' || ls_prop_code || ' - gérer le cas manuellement ';
                goto suite;
            else
                    -- on a passé tous les obstacles - les resa sont déja OK (qté déja réservée = qté en commande)
                ib_warning := false;
                ls_comment	:= 'OK ';
                goto suite;
            end if;
        else
                -- qté déja réservée différente de qté en commande (ll_qte_deja_resa <> ll_nb_col) et fournisseur conforme
                -- on annule les résa existantes pour repartir from scratch (c'est plus simple et utilise les fonctions déja existantes)
                -- s'il n'y a pas de réservations, le delete ne fera aucun mal
            begin
                delete from geo_stomvt where orl_ref = ls_orl_ref;
                commit;
            exception when others then
                ls_comment	:= ls_comment || 'Pb sur supression des réservations pour la ligne ' || ls_orl_ref || ' - ' || SQLERRM;
                rollback;
                goto suite;
            end;
            ll_qte_deja_resa	:= 0;
            ll_nb_deja_resa	:= 0;
                -- on a recherché toutes les erreurs potentielles, on va traiter les reservations
                -- cad qu'il y a qqchose à réserver
                -- on effectue les réservations sur le fournisseur et l'article avec priorité aux options du arg_username
                -- les réservations sont effectuées même si le stock est négatif
                -- les réservations sont faites aussi que si le type de palette correspond
            f_resa_une_ligne(ls_fou_code, ls_prop_code, ls_art_ref, arg_username, ll_nb_col, ls_ord_ref, ls_orl_ref, ls_ordre_desc, ls_pal_code_client, res, msg, ll_nb_resa, ll_qte_dispo);
                -- on récupère le nbre de réservations effectuées et le disponible actualisé du fournisseur pour cet article
            if ll_nb_resa = 0 then
                    -- pas de resa sur le fournisseur, mais on va afficher les autres fourni
                f_get_stock_article_fourni(ls_art_ref, res, ls_rc);
                if ls_rc <> '' then
                    --ls_char	= substr(ls_rc, instr(ls_rc, 'car=') + 4, 1)
                    ls_dispo := substr(ls_rc,1, instr(ls_rc, ';')-1);
                    ls_comment	:= ls_comment || 'article:' || ls_art_ref;
                    if ls_pal_code_client is not null then ls_comment	:= ls_comment || ' palette:' || ls_pal_code_client; end if;
                    --ls_comment = ls_comment + ' pas de stock '  + ls_fou_code + '/' + ls_prop_code + ' --> dispo:' + ls_rc
                    ls_comment := ls_comment || ' pas de stock '  || ls_fou_code || '/' || ls_prop_code || ' --> dispo:' || ls_dispo;
                    ls_info_stock :=substr(ls_rc,instr(ls_rc, ';')+1, length(ls_rc)-instr(ls_rc, ';'));
                else
                    ls_comment	:= ls_comment || 'article:' || ls_art_ref || ' pas de stock nulle part ' || ls_rc;
                    if ls_pal_code_client is not null then ls_comment	:= ls_comment || ' palette:' || ls_pal_code_client; end if;
                    ls_comment	:= ls_comment || ' pas de stock nulle part ' || ls_rc;
                    ls_info_stock := '';
                end if;
                goto suite;

            else
                if ll_nb_resa = 1 then
                    ls_comment	:= ls_comment || to_char(ll_nb_resa) || ' réservation faite sur ' || ls_fou_code || '/' || ls_prop_code;
                else
                    ls_comment	:= ls_comment || to_char(ll_nb_resa) || ' réservations faites sur ' || ls_fou_code || '/' || ls_prop_code;
                end if;
                if ll_qte_dispo < 0 then
                    ls_comment	:= ls_comment || ' - ATTENTION DISPONIBLE NEGATIF = ' || to_char(ll_qte_dispo);
                    ll_nb_resa_upd	:= ll_nb_resa * -1;
                else
                    ib_warning	:= false;
                    ll_nb_resa_upd	:= ll_nb_resa;
                end if;
                    -- actualisation de la ligne d'ordre du nbre de réservations reliées
                begin
                    update geo_ordlig set stock_nb_resa = ll_nb_resa_upd where orl_ref = ls_orl_ref;
                    commit;
                exception when others then
                    ib_warning	:= true;
                    ls_comment	:= ls_comment || 'actualisation du nombre de réservations à échoué : ' || SQLERRM;
                    rollback;
                end;
            end if;
        end if;
    <<suite>>
        -- ll_nb_row	= arg_ds.InsertRow(0)
        result.extend();

		declare
			   l_row varchar2(500);
		begin

	        -- arg_ds.SetItem(ll_nb_row, 'orl_ref', ls_orl_ref)
	        l_row := l_row || ls_orl_ref;

	        -- arg_ds.SetItem(ll_nb_row, 'orl_lig', ls_orl_lig)
	        l_row := l_row || '¤' || ls_orl_lig;

	        -- arg_ds.SetItem(ll_nb_row, 'resa_desc', ls_comment)
	        l_row := l_row || '¤' || ls_comment;

	        -- arg_ds.SetItem(ll_nb_row, 'info_stock', ls_info_stock)
	        l_row := l_row || '¤' || ls_info_stock;

	        if ib_warning =true then
	            l_row := l_row || '¤' || 'O';
	            -- arg_ds.SetItem(ll_nb_row, 'warning', 'O')
	        else
	            l_row := l_row || '¤' || 'N';
	            -- arg_ds.SetItem(ll_nb_row, 'warning', 'N')
	        end if;

	            -- le statut reflète le fait que l'on n'a fait ou pas des resa, independamment des warning
	        if ll_nb_resa <> 0 then
	            l_row := l_row || '¤' || 'O';
	            -- arg_ds.SetItem(ll_nb_row, 'statut', 'O')
	        else
	            l_row := l_row || '¤' || 'N';
	            -- arg_ds.SetItem(ll_nb_row, 'statut', 'N')
	        end if;
	        result(result.count()) := to_char(l_row);
		end;
    end loop;

    res := 1;

end F_RESA_AUTO_ORDRE;
/
