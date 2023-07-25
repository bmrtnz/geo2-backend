CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_RECUP_REGION_ENTREP(
	arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    res OUT number,
    msg OUT varchar2,
	bassin OUT varchar2
)
AS
	ls_bac_code GEO_DEPT.BAC_CODE%TYPE;
	ls_dept_entrep GEO_ENTREP.ZIP%TYPE;
	ll_dept_ent number;
BEGIN
    res := 0;
    msg := '';

	begin
		select substr(zip,1,2) 
		into ls_dept_entrep 
		from geo_entrep 
		where cen_ref = arg_cen_ref;
	exception when others then
		msg := '%%%Erreur sur récup département pour l''entrepôt: ' || arg_cen_ref || ' ' || SQLERRM;
		res := 0;
		return;
	end;		
	
	ll_dept_ent := to_number(ls_dept_entrep);
	if ll_dept_ent < 10 then
		ls_dept_entrep := to_char(ll_dept_ent);
	end if;
	
	begin
		select D.bac_code 
		into ls_bac_code
		from geo_dept D
		where D.num_dept = ls_dept_entrep;
	exception when others then
		msg := '%%%ERREUR récupération département entrepôt:' || arg_cen_ref  || ' ' || SQLERRM;
		res := 0;
		return;
	end;	
	
	CASE ls_bac_code
		WHEN 'VDL' THEN
			bassin := '''' || ls_bac_code	|| '''';	
		WHEN 'SE' THEN
			bassin := '''' || ls_bac_code	|| '''';	
		WHEN 'SW' THEN
			bassin := '''SW'',''UDC''';
		WHEN 'UDC'  THEN
			bassin := '''SW'',''UDC''';
		WHEN 'INDIVIS' THEN
			bassin := '''%''';
	END CASE;
	
	res := 1;

end F_RECUP_REGION_ENTREP;
/

