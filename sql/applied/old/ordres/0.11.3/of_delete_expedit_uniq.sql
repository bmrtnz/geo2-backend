CREATE OR REPLACE PROCEDURE GEO_ADMIN.of_delete_expedit_uniq (
	is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	res IN OUT number,
	msg IN OUT varchar2
)
AS
BEGIN
	msg := '';
	res := 0;

	DECLARE cursor cur_exp is
	select FOU_CODE as ls_fou_code, SUM(CASE WHEN FOU_CODE = PROPR_CODE THEN 1 else 0 end) as li_nb_propr
	from GEO_ORDLIG
	where ORD_REf = is_ord_ref
	GROUP BY FOU_CODE
	ORDER BY FOU_CODE;

	begin
		for r in cur_exp
		loop
			If r.li_nb_propr = 0 then
				delete from geo_envois where ord_ref = is_ord_ref and trait_exp = 'A' and tie_code = r.ls_fou_code and tyt_code = 'F';
				commit;
			end if;
		end loop;
		exception when others then
		msg := 'of_delete_expedit_uniq ouverture du curseur';
		res := 0;
		return;
	end;

	msg := '';
	res := 1;
	return;


END OF_DELETE_EXPEDIT_UNIQ;
/

