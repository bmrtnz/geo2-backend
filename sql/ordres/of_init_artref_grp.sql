-- of_init_artref_grp(arg_row)

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_INIT_ARTREF_GRP" (
    cur_orl_ref GEO_ORDLIG.orl_ref%type,
	res out number,
    msg out varchar2
)
AS
	ls_artref varchar2(50);
	ls_artref_row varchar2(50);
	ls_gtin_colis_grp varchar2(50);
	ls_gtin_uc_grp varchar2(50);
	ll_ind number;
	lb_ya_gtin char;
	ls_gtin_row varchar2(50);
	ls_gtin_encours varchar2(50);
	ls_art_encours varchar2(50);

	cur_ord_ref GEO_ORDRE.ORD_REF%TYPE;
	is_edi_ord varchar2(50);
	is_gtin_colis varchar2(50);
begin

	res := 0;
	msg := '';

	select ord_ref, ref_edi_ordre
	into cur_ord_ref, is_edi_ord
	from geo_ordre
	where ord_ref = cur_ord_ref;

	select ol.art_ref, ol.art_ref_kit, ol.gtin_colis_kit, aan.gtin_colis
	into ls_artref, ls_artref_row, ls_gtin_row, is_gtin_colis
	from geo_ordlig ol
	left join avi_art_gestion aag on aag.art_ref = ol.art_ref
	left join avi_art_normalisation aan on aag.ref_normalisation = aan.ref_normalisation
	where orl_ref = cur_orl_ref;

	if ls_artref is not null and ls_artref <> '' THEN

		if is_edi_ord is null then 
			SELECT 
				CASE WHEN GTIN_COLIS IS NOT NULL THEN GTIN_COLIS ELSE GTIN_COLIS_BW END,
				CASE WHEN GTIN_UC IS NOT NULL THEN GTIN_UC ELSE GTIN_UC_BW END
			INTO ls_gtin_colis_grp, ls_gtin_uc_grp
			FROM GEO_ARTICLE WHERE ART_REF = ls_artref;
				
			IF ls_gtin_uc_grp is not null and ls_gtin_uc_grp <> '' THEN
				ls_gtin_colis_grp := ls_gtin_uc_grp;
			END IF;
				
			-- On met les valeurs par défaut que si pas déjà remplit par l'historique par exemple
			--LLEF: rajout de la condition dans le cas de modification de la ref article par la zone n° de la ligne article
			if ls_artref_row is null  or ls_artref <> ls_artref_row then
				update geo_ordlig set
					art_ref_kit = ls_artref,
					gtin_colis_kit = ls_gtin_colis_grp
				where orl_ref = cur_orl_ref;
				commit;
			end if;
		else
			--deb llef
			ls_artref_row 		:= ls_artref ;
			-- lb_ya_gtin      		:= 'N';
			
			select ol.art_ref_kit into ls_artref_row
			from geo_ordlig ol
			left join avi_art_gestion aag on aag.art_ref = ol.art_ref
			left join avi_art_normalisation aan on aag.ref_normalisation = aan.ref_normalisation
			where ol.ord_ref = cur_ord_ref
			and (ol.gtin_colis_kit = aan.gtin_colis or ol.gtin_colis_kit = is_gtin_colis)
			and ol.art_ref <> ls_artref
			and rownum = 1
			order by mod_date;

			update geo_ordlig set art_ref_kit = ls_artref_row where orl_ref = cur_orl_ref;
			
			if is_gtin_colis is not null and is_gtin_colis <> '' then --Pour ne pas altérer le gtin lors de l'ajout par ref article
				update geo_ordlig set gtin_colis_kit = is_gtin_colis where orl_ref = cur_orl_ref;
			end if;

			commit;
			
		end if; 
			
	END IF;

	msg := 'OK';
	return;
end;