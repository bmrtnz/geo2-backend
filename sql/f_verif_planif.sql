CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_VERIF_PLANIF (
    arg_region IN varchar2,
	arg_art_ref IN GEO_ARTICLE_COLIS.ART_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    res IN OUT number,
    msg IN OUT varchar2,
	key_edi_art_planif OUT P_STR_TAB_TYPE
)
AS
	ls_bac_code GEO_FOURNI.BAC_CODE%TYPE;
	ll_key number;
	ls_pal_code GEO_ENTREP.PAL_CODE%TYPE;
	C_PLANIF SYS_REFCURSOR;
	ls_sql varchar2(600);
	
BEGIN
    -- correspond à of_verif_planif.pbl
    res := 0;
    msg := '';
	key_edi_art_planif := p_str_tab_type();
	
    begin
        select E.pal_code 
		into ls_pal_code
		from geo_entrep E
		where E.cen_ref = arg_cen_ref
		and E.valide ='O';
    exception when others then
        msg := '%%%ERREUR récupération code palette entrepôt : ' || arg_cen_ref;
		ls_pal_code := '%';
    end;

	ls_sql := '';
	ls_sql := ls_sql || ' select F.bac_code, P.K_EDI_ART_PLANIF ';
	ls_sql := ls_sql || ' from GEO_FOURNI F , GEO_EDI_ART_PLANIF P ';
	ls_sql := ls_sql || ' where F.FOU_CODE = P.STAT_CODE_EMBAL ';
	ls_sql := ls_sql || ' and F.valide = ''O'' '; 
	ls_sql := ls_sql || ' and P.valide = ''O'' ';
	ls_sql := ls_sql || ' and P.art_ref = ''' || arg_art_ref || '''';
	if arg_region = '%' then
		ls_sql := ls_sql || ' and F.BAC_CODE like ''' || arg_region || '''';
	else
		ls_sql := ls_sql || ' and F.BAC_CODE in (' || arg_region || ')';
	end if;
	ls_sql := ls_sql || ' and P.pal_code like ''' || ls_pal_code || '''';
	
	
	open C_PLANIF FOR ls_sql;
		LOOP
			fetch C_PLANIF into ls_bac_code, ll_key;
			EXIT WHEN C_PLANIF%notfound;
			key_edi_art_planif.extend();
			key_edi_art_planif(key_edi_art_planif.count()) := to_char(ll_key);
		END LOOP;
	CLOSE C_PLANIF;
	
    res := 1;
END F_VERIF_PLANIF;
/
