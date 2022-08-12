CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CREATE_ORDRES_EDI" (
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
    ls_nordre_tot out varchar2
)
AS
    ls_ord_ref GEO_ORDRE.ORD_REF%TYPE;
    ls_nordre GEO_ORDRE.NORDRE%TYPE;

    CURSOR C_STOCK_ART_EDI (ref_ordre GEO_STOCK_ART_EDI_BASSIN.edi_ord%TYPE) IS
        select distinct bac_code
        from GEO_STOCK_ART_EDI_BASSIN
        where edi_ord = ref_ordre;
    CURSOR C_LIG_EDI_L (ref_ordre GEO_STOCK_ART_EDI_BASSIN.edi_ord%TYPE, code_bac GEO_STOCK_ART_EDI_BASSIN.BAC_CODE%TYPE) IS
        select EDI_LIG
        from GEO_STOCK_ART_EDI_BASSIN
        where EDI_ORD = ref_ordre
          and bac_code = code_bac;
BEGIN
    -- corresponds à f_create_ordre_edi.pbl
    res := 0;
    msg := '';

    F_VERIF_STOCK_DISPO(arg_edi_ordre, arg_cen_ref, arg_cam_code, res, msg);
    if (res <> 1) then
        msg := 'Anomalie lors de la création de l''ordre EDI : ' || SQLERRM;
        return;
    end if;

    for r in C_STOCK_ART_EDI(arg_edi_ordre)
    loop

        F_CREATE_ORDRE_V3(arg_soc_code, arg_cli_ref, arg_cen_ref, '', arg_ref_cmd, false, false, '', 'ORD', arg_date_liv, arg_edi_ordre, res, msg, ls_ord_ref);
        if (res <> 1) then
            return;
        end if;

        F_INSERT_MRU_ORDRE(ls_ord_ref, arg_username, res, msg);
        if (res <> 1) then
            return;
        end if;

        for s in C_LIG_EDI_L(arg_edi_ordre, r.BAC_CODE)
        loop
                F_CREATE_LIGNE_EDI(ls_ord_ref, arg_cli_ref, arg_cen_ref, s.EDI_LIG, '', arg_soc_code, arg_username, res, msg);
                if (res <> 1) then
                    return;
                end if;
        end loop;

        select nordre into ls_nordre from geo_ordre where ord_ref = ls_ord_ref;
        ls_nordre_tot := ls_nordre_tot || ls_nordre || ',';
    end loop;

    if ls_nordre_tot is not null and ls_nordre_tot <> '' then
        update GEO_EDI_ORDRE SET STATUS_GEO = 'T' WHERE REF_EDI_ORDRE = arg_edi_ordre;
    end if;

    res := 1;

end F_CREATE_ORDRES_EDI;
/

