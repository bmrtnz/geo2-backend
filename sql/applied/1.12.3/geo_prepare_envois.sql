CREATE OR REPLACE PROCEDURE GEO_PREPARE_ENVOIS (
	is_ord_ref IN  GEO_ORDRE.ORD_REF%TYPE,
	is_flu_code IN OUT GEO_FLUX.FLU_CODE%TYPE,
	mode_auto IN char,
	ann_ordre IN char,
	arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
	res OUT number,
	msg OUT varchar2,
	co OUT SYS_REFCURSOR
)
AS
	ls_env_code  p_str_tab_type;
	ls_typ_ordre GEO_TYPORD.TYP_ORD%TYPE;
BEGIN
	-- correspond à w_geo_genere_envois_on_open.pbl
    msg := '';
    res := 0;

	IF (mode_auto = 'O') THEN
		CASE is_flu_code
		WHEN 'INCLIT' THEN
			of_genere_envois_litige_autom(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, res, msg);
            if res <> 1 then return; end if;
			msg := 'OK';
			res := 1;
			RETURN;
		WHEN 'RESLIT' THEN
			msg := 'OK';
			res := 1;
			RETURN;
		ELSE
			of_genere_envois(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, ann_ordre, res, msg, co, ls_env_code);

			select TYP_ORDRE INTO ls_typ_ordre
			FROM GEO_ORDRE
			where ORD_REF = is_ord_ref;

			CASE ls_typ_ordre
	 			WHEN 'RGP' THEN -- SA
	 				DELETE FROM geo_envois ge WHERE ge.TRAIT_EXP = 'A' AND ge.ORD_REF = is_ord_ref AND ge.TYT_CODE IN ('C', 'E');
	 				DELETE FROM geo_envois ge WHERE ge.TRAIT_EXP = 'A' AND ge.ORD_REF = is_ord_ref AND ge.TYT_CODE = 'F' AND get_flux_rgp(is_ord_ref, ge.tie_code) = 'BUK';
	 			WHEN 'ORI' THEN -- BUK
	 				DELETE FROM geo_envois ge WHERE ge.TRAIT_EXP = 'A' AND ge.ORD_REF = is_ord_ref AND (ge.TYT_CODE IN ('P', 'C', 'E') OR (ge.TYT_CODE = 'F' AND get_flux_ori(is_ord_ref, ge.tie_code) = 'SA'));
                ELSE null;
			end case;
		end case;

		res := 1;
		msg := 'OK';
		return;
	END IF;

	CASE is_flu_code
		WHEN 'INCLIT' THEN
			of_genere_envois_litige(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, res, msg);
		WHEN 'RESLIT' THEN
			of_genere_envois_litige(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, res, msg);
		WHEN 'DETAIM' THEN
			-- of_genere_envoi_detaim; TODO MICROTEC
			return; -- à retirer après
		WHEN 'PROFOR' THEN
			-- of_genere_envois_proform; TODO MICROTEC
			return; --à retirer après
		ELSE
			of_genere_envois(is_ord_ref, is_flu_code, mode_auto, arg_nom_utilisateur, ann_ordre, res, msg, co, ls_env_code);
	END case;

	res := 1;
	msg := 'OK';
	return;

END GEO_PREPARE_ENVOIS;
/

