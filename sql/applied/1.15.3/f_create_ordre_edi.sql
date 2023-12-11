CREATE OR REPLACE PROCEDURE F_CREATE_ORDRES_EDI (
    arg_edi_ordre IN GEO_STOCK_ART_EDI_BASSIN.edi_ord%TYPE,
    arg_cam_code IN GEO_CAMPAG.CAM_CODE%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_cli_ref IN GEO_CLIENT.CLI_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    arg_ref_cmd IN GEO_ORDRE.REF_CLI%TYPE,
    arg_date_liv IN varchar2,
    arg_username in GEO_USER.NOM_UTILISATEUR%TYPE,
    res out number,
    msg out varchar2,
    ls_nordre_tot out varchar2,
    tab_ordre_cree out P_STR_TAB_TYPE
)
AS
    ls_ord_ref GEO_ORDRE.ORD_REF%TYPE;
    ls_nordre GEO_ORDRE.NORDRE%TYPE;
    ordre_cree_idx number := 0;

    ls_trp_code varchar2(50);
    ls_trp_code_cde varchar2(50);
    ls_bac_code_cde varchar2(50);
    ls_sql clob;
    ls_flag_hors_bassin varchar2(50);
    ls_gtin_client varchar2(50);
    ls_art_ref_client varchar2(50);
    ls_art_ref_ouverture varchar2(50);
    ls_art_ref varchar2(50);
    ls_sw_udc char(1) := 'N'; -- couple bassin 'UDC et' SW'

    C_LIG_EDI_L SYS_REFCURSOR;
    CURSOR C_STOCK_ART_EDI (ref_ordre GEO_STOCK_ART_EDI_BASSIN.edi_ord%TYPE) IS
        select distinct bac_code
        from GEO_STOCK_ART_EDI_BASSIN
        where edi_ord = arg_edi_ordre
		and (bac_code is not null or bac_code <> '' )
		and choix  = 'O';
BEGIN
    -- corresponds à f_create_ordre_edi.pbl
    res := 0;
    msg := '';
    tab_ordre_cree := p_str_tab_type();

    select trp_code, bac_code into ls_trp_code_cde, ls_bac_code_cde
    from geo_edi_ordre
    where ref_edi_ordre = arg_edi_ordre;

    declare
        ls_bac_code clob;
    begin
        for r in C_STOCK_ART_EDI(arg_edi_ordre)
        loop

            ls_bac_code := r.BAC_CODE;

            if ls_bac_code = 'SW' or ls_bac_code = 'UDC' then
                if ls_sw_udc = 'N' then
                    ls_bac_code := '''UDC'', ''SW''';
                    ls_sw_udc := 'O';
                end if;
            else
                ls_bac_code := '''' ||  ls_bac_code || '''';
            end if;

            begin
                if ls_bac_code <> ls_bac_code_cde  then
                    -- Récup du transporteur par défaut GEO_ENT_TRP_BASSIN si bassin de la commande est différent de celui du stock trouvé et sélectionné
                    if ls_bac_code is not null then
                        ls_sql := '';
                        ls_sql := ls_sql || ' select distinct trp_code  ';
                        ls_sql := ls_sql || ' from geo_ent_trp_bassin ';
                        ls_sql := ls_sql || ' where cen_ref = ' || arg_cen_ref;
                        ls_sql := ls_sql || ' and bac_code in (' || ls_bac_code || ')';
                        ls_sql := ls_sql || ' and valide = ''O''';
                        EXECUTE IMMEDIATE to_char(ls_sql);
                    else
                        ls_trp_code := '-'; -- Trasnport à préciser
                    end if;
                else
                    if ls_trp_code_cde is not null then
                        ls_trp_code := ls_trp_code_cde;
                    else
                        ls_trp_code := '-';
                    end if;
                end if;
            exception when others then
                ls_trp_code := '-';
            end;

            F_CREATE_ORDRE_V3(arg_soc_code, arg_cli_ref, arg_cen_ref, ls_trp_code, arg_ref_cmd, false, false, '', 'ORD', arg_date_liv, arg_edi_ordre, res, msg, ls_ord_ref);
            if (res <> 1) then
                msg := 'Erreur lors de la création de l''ordre : ' || msg;
                return;
            end if;

            F_INSERT_MRU_ORDRE(ls_ord_ref, arg_username, res, msg);
            if (res <> 1) then
                msg := 'Erreur lors de la création du mru_ordre : ' || msg;
                return;
            end if;

            ls_sql := '';
            --ls_sql += " select EDI_LIG, FLAG_HORS_BASSIN "
            ls_sql := ls_sql || ' select K_STOCK_ART_EDI_BASSIN, FLAG_HORS_BASSIN, GTIN, ART_REF ';
            ls_sql := ls_sql || ' 	from GEO_STOCK_ART_EDI_BASSIN ';
            ls_sql := ls_sql || ' 	where EDI_ORD = ' || to_char(arg_edi_ordre);
            ls_sql := ls_sql || ' 	and bac_code  in (' || ls_bac_code || ') ';
            ls_sql := ls_sql || ' 	and choix = ''O'' ';
            ls_sql := ls_sql || ' 	order by edi_lig ';

            declare
                k_seb number;
            begin
                OPEN C_LIG_EDI_L FOR to_char(ls_sql);
                loop
                    fetch C_LIG_EDI_L into k_seb;
                    EXIT WHEN C_LIG_EDI_L%notfound;
                    F_CREATE_LIGNE_EDI_2(ls_ord_ref, arg_cli_ref, arg_cen_ref, k_seb, '', arg_soc_code, arg_username, res, msg);
                    if (res <> 1) then
                        msg := 'Erreur lors de la création de la ligne d''ordre : ' || msg;
                        return;
                    end if;
                end loop;
                CLOSE C_LIG_EDI_L;
            end;

            tab_ordre_cree.extend();
            ordre_cree_idx := ordre_cree_idx + 1;
            tab_ordre_cree(ordre_cree_idx) := ls_ord_ref;

            select nordre into ls_nordre from geo_ordre where ord_ref = ls_ord_ref;
            ls_nordre_tot := ls_nordre_tot || ls_nordre || ',';
        end loop;
    end;

    if ls_nordre_tot is not null then
        update GEO_EDI_ORDRE SET STATUS_GEO = 'T' WHERE REF_EDI_ORDRE = arg_edi_ordre;
    end if;

    res := 1;

end;
/

