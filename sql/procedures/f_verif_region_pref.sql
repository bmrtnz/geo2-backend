CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_VERIF_REGION_PREF(
	arg_region_pref IN varchar2,
    res OUT number,
    msg OUT varchar2,
	region OUT varchar2
)
AS

BEGIN
    res := 0;
    msg := '';

	if arg_region_pref is not null and arg_region_pref <> '' then
		if arg_region_pref = 'SW' then
			region := '''SW'',''UDC''';
		else
			region := '''' || UPPER(arg_region_pref) || '''';
		end if;
	else
		region := '%';
	end if;

	res := 1;

end F_VERIF_REGION_PREF;
/

