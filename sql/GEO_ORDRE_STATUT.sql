CREATE OR REPLACE FUNCTION GEO_ORDRE_STATUT(REF_ORDRE GEO_ORDRE.ORD_REF%TYPE) RETURN varchar2 IS
    statut varchar2(32);
	TYPE Flag IS RECORD(
    flag_public char(1),
    has_detail_pal char(1),
    has_lignes char(1),
	exped_ok char(1),
    flbaf char(1),
    flfac char(1),
    invoic_demat char(1),
    flannul char(1)
    );
	current_ordre Flag;
	CURSOR fetch_ordre(REF_ORDRE GEO_ORDRE.ORD_REF%TYPE) IS
		SELECT
			flag_public,
			(SELECT 'O' FROM dual WHERE exists(
				SELECT 1
				FROM geo_traca_detail_pal
				WHERE ord_ref = REF_ORDRE
			)) AS has_detail_pal,
			(SELECT 'O' FROM dual WHERE exists(
				SELECT 1
				FROM geo_ordlig
				WHERE ord_ref = REF_ORDRE
			)) AS has_lignes,
			GEO_ORDRE_STATUS_EXPED(ord_ref) AS exped_ok,
			flbaf,
			flfac,
			invoic_demat,
			flannul
		FROM geo_ordre
		WHERE ord_ref = REF_ORDRE;
BEGIN

	open fetch_ordre(ref_ordre);
	fetch fetch_ordre into current_ordre;

	-- NON CONFIRME
	statut := 'NCF';

	-- CONFIRME
	if current_ordre.flag_public = 'O' then
 	   statut := 'CFM';
	end if;

	-- EN PREPARATION
	if current_ordre.has_detail_pal = 'O' then
 	   statut := 'EPP';
	end if;

	-- EXPEDIE
	if current_ordre.has_lignes = 'O' and current_ordre.exped_ok = 'O' then
 	   statut := 'EXP';
	end if;

	-- A FACTURER
	if current_ordre.flbaf = 'O' then
 	   statut := 'AFC';
	end if;

	-- FACTURE
	if current_ordre.flfac = 'O' then
 	   statut := 'FCT';
	end if;

	-- FACTURE
	if current_ordre.invoic_demat = 'O' then
 	   statut := 'FCT_EDI';
	end if;

	-- ANNULE
	if current_ordre.flannul = 'O' then
 	   statut := 'ANL';
	end if;

	close fetch_ordre;

    return statut;
end;
/
