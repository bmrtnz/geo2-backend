CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_AFFICHE_ORDRE_BAF" (
    gs_soc_code in varchar2,
    is_sco_code in varchar2,
    is_cli_ref in varchar2,
    is_cen_ref in varchar2,
    id_date_min in date,
    id_date_max in date,
    is_per_codeass in varchar2,
    is_per_codecom in varchar2,
    res out number,
    msg out varchar2,
    c_ordre_baf out SYS_REFCURSOR
)
AS
    isw_sco_code varchar2(50)	 := is_sco_code;
    isw_cli_ref varchar2(50)	 := is_cli_ref;
    isw_cen_ref	varchar2(50)	 := is_cen_ref;
    idw_date_min date	         := id_date_min;
    idw_date_max date	         := id_date_max;
    isw_per_codeass varchar2(50) := is_per_codeass;
    isw_per_codecom varchar2(50) := is_per_codecom;

    ls_sql varchar(2000);
    ls_ord_ref varchar2(50);
    ls_client_raisoc varchar2(50);
    ls_entrep_raisoc varchar2(50);
    ls_transp_raisoc varchar2(50);
    ls_ref_cli varchar2(50);
    ls_dt_min varchar2(50);
    ls_dt_max varchar2(50);
    ls_rc varchar2(50);
    ll_row number;
    ll_row_cocher number;
    ll_nb_tot number;
    ls_nordre varchar2(50);

    --type tabString2 is table of varchar2(50) index by number;
    --ls_tab_ord_ref_cocher tabString2;

    idw_dt_min date;
    idw_dt_max date;

    --C_BAF SYS_REFCURSOR;
BEGIN
    If idw_date_min is null then
        idw_dt_min := SYSDATE; -- TODAY
    else
        idw_dt_min := idw_date_min;
    End If;
    ls_dt_min := to_char(idw_dt_min, 'DD/MM/YYYY');

    If idw_date_max is null then
        idw_dt_max := SYSDATE + 1;
    else
        idw_dt_max := idw_date_max + 1;
    End If;
    ls_dt_max := TO_CHAR(idw_dt_max, 'DD/MM/YYYY');

    ls_sql := 'SELECT GEO_ORDRE.ORD_REF,GEO_ORDRE.NORDRE, GEO_CLIENT.RAISOC AS CLIENT, GEO_ENTREP.CEN_CODE || ''-'' || GEO_ENTREP.RAISOC AS ENTREP, GEO_TRANSP.RAISOC AS TRANSP, to_char(GEO_ORDRE.LIVDATP,''dd/mm/yy'') as LIVDATPFR, to_char(GEO_ORDRE.LIVDATP,''yy/mm/dd'') AS LIVDATPEN, GEO_ORDRE.REF_CLI ';
    ls_sql := ls_sql || ' FROM GEO_ORDRE, ';
    ls_sql := ls_sql || ' GEO_CLIENT, ';
    ls_sql := ls_sql || ' GEO_ENTREP, ';
    ls_sql := ls_sql || ' GEO_TRANSP ';
    ls_sql := ls_sql || '   WHERE GEO_ORDRE.SOC_CODE = ''' || gs_soc_code || ''' AND  ';
    ls_sql := ls_sql || '                 GEO_ORDRE.SCO_CODE = ''' || isw_sco_code || ''' AND  ';
    ls_sql := ls_sql || '                 GEO_ORDRE.CEN_CODE  not like ''PREORDRE%'' AND  ';

    IF (isw_cli_ref <> '') OR (isw_cli_ref IS NOT null) Then
        ls_sql := ls_sql || ' GEO_ORDRE.CLI_REF = ''' || isw_cli_ref || ''' AND ';
    End If;
    IF (isw_cen_ref <> '') OR (isw_cen_ref IS NOT null) Then
        ls_sql := ls_sql || ' GEO_ORDRE.CEN_REF = ''' || isw_cen_ref || ''' AND ';
    End If;
    IF (isw_per_codeass <> '') OR (isw_per_codeass IS NOT null) Then
        ls_sql := ls_sql || ' GEO_ORDRE.PER_CODEASS = ''' || isw_per_codeass || ''' AND ';
    End If;
    IF (isw_per_codecom <> '') OR (isw_per_codecom IS NOT null) Then
        ls_sql := ls_sql || ' GEO_ORDRE.PER_CODECOM= ''' || isw_per_codecom || ''' AND ';
    End If;
            
    ls_sql := ls_sql || '                 ( GEO_ENTREP.CEN_REF = GEO_ORDRE.CEN_REF ) and  ';
    ls_sql := ls_sql || '                 ( GEO_TRANSP.TRP_CODE = GEO_ORDRE.TRP_CODE ) and  ';
    ls_sql := ls_sql || '                 ( GEO_CLIENT.CLI_REF = GEO_ORDRE.CLI_REF ) and  ';
    ls_sql := ls_sql || '         ( GEO_ORDRE.FLBAF = ''N'' ) AND  ';
    ls_sql := ls_sql || '         ( GEO_ORDRE.VALIDE = ''O'' ) AND  ';
    ls_sql := ls_sql || '        ( GEO_CLIENT.IND_USINT <> ''O'') AND  ';
    ls_sql := ls_sql || '         GEO_ORDRE.LIVDATP >= to_date(''' || ls_dt_min || ''', ''dd/mm/yy'')  and';
    ls_sql := ls_sql || '         GEO_ORDRE.LIVDATP < to_date(''' || ls_dt_max || ''', ''dd/mm/yy'')';

    OPEN C_ORDRE_BAF FOR ls_sql;
END;
