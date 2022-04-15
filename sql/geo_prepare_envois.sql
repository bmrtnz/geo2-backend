CREATE OR REPLACE PROCEDURE GEO_PREPARE_ENVOIS (
	is_ord_ref IN  GEO_ORDRE.ORD_REF%TYPE,
	is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
	mode_auto IN char,
	ann_ordre IN char,
	arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
	res OUT number,
	msg OUT varchar2,
	co out SYS_REFCURSOR
)
AS
	ls_typ_ordre GEO_TYPORD.TYP_ORD%TYPE;
	ib_ann_ordre char;
BEGIN
	-- correspond à w_geo_genere_envois_on_open.pbl
    msg := '';
    res := 0;

	IF (mode_auto = 'O') THEN
		CASE is_flu_code
		WHEN 'INCLIT' THEN
			/* TODO MICROTEC
			of_genere_envois_litige_autom();*/
			msg := 'OK';
			res := 1;
			RETURN;
		WHEN 'RESLIT' THEN
			msg := 'OK';
			res := 1;
			RETURN;
		ELSE
			of_genere_envois(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, ib_ann_ordre, res, msg, co);

			select TYP_ORDRE INTO ls_typ_ordre
			FROM GEO_ORDRE
			where ORD_REF = is_ord_ref;

			CASE ls_typ_ordre
	 			WHEN 'RGP' THEN
	 				DELETE FROM geo_envois ge WHERE ge.TRAIT_EXP = 'A' AND ge.ORD_REF = is_ord_ref AND ge.TYT_CODE IN ('C', 'E');
	 				DELETE FROM geo_envois ge WHERE ge.TRAIT_EXP = 'A' AND ge.ORD_REF = is_ord_ref AND ge.TYT_CODE = 'F' AND ge.TIE_CODE = 'STEFLEMANS';
	 			WHEN 'ORI' THEN
	 				DELETE FROM geo_envois ge WHERE ge.TRAIT_EXP = 'A' AND ge.ORD_REF = is_ord_ref AND (ge.TYT_CODE IN ('P', 'C', 'E') OR (ge.TYT_CODE = 'F' AND ge.TIE_CODE <> 'STEFLEMANS'));
                ELSE null;
			end case;
		end case;
	END IF;


	--deb LLEF
	if ann_ordre = 'O' then
		ib_ann_ordre := 'O';
	end if;
	--fin LLEF

	CASE is_flu_code
		WHEN 'INCLIT' THEN
			-- of_genere_envois_litige; TODO MICROTEC
			return; -- à retirer quand procedure précédente ok
		WHEN 'RESLIT' THEN
			-- of_genere_envois_litige; TODO MICROTEC
			return; -- à retirer quand procedure précédente ok
		WHEN 'DETAIM' THEN
			-- of_genere_envoi_detaim; TODO MICROTEC
			return; -- à retirer après
		WHEN 'PROFOR' THEN
			-- of_genere_envois_proform; TODO MICROTEC
			return; --à retirer après
		ELSE
			of_genere_envois(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, ib_ann_ordre, res, msg, co);
	END case;

	res := 1;
	msg := 'OK';
	return;

END GEO_PREPARE_ENVOIS;
/
