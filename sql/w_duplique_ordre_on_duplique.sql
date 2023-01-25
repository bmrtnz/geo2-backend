CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."W_DUPLIQUE_ORDRE_ON_DUPLIQUE" (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_username IN GEO_USER.NOM_UTILISATEUR%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_cen_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_depdatp IN timestamp,
    arg_livdatp IN date,
    arg_code_chargement IN char,
    arg_etd_location IN char,
    arg_eta_location IN char,
    arg_etd_date IN char,
    arg_eta_date IN char,
    arg_inc_code IN char,
    arg_fourni IN char,
    arg_vte_pu IN char,
    arg_ach_pu IN char,
    arg_lib_dlv IN char,
	res out number,
    msg out varchar2,
    nordre out GEO_ORDRE.NORDRE%TYPE
)
AS
    ll_null number;
    lt_time timestamp;
    ls_depdatp varchar2(50);
    ls_livdatp varchar2(50);
    ll_rc timestamp;
    ls_rc varchar2(50);
    ls_ord_ref_new varchar2(50);
    ls_nordre_new varchar2(50);
    ls_instructions_logistique varchar2(280);
    is_ref_cli varchar2(50);
    is_cur_cli_ref varchar2(50);
    is_trp_code varchar2(50);
    is_inc_code varchar2(50);

    is_per_codecom varchar2(50);
    is_per_codeass varchar2(50);
    idc_frais_pu varchar2(50);
    is_ttr_code varchar2(50);
    idc_trp_dev_pu varchar2(50);
    idc_trp_pu varchar2(50);
    is_trp_dev_code varchar2(50);
    is_trp_bta_code varchar2(50);
    idc_frais_plateforme varchar2(50);
    idc_trp_dev_taux varchar2(50);
    is_dev_code varchar2(50);
    idc_dev_tx varchar2(50);
    is_comm_interne varchar2(50);
    is_trp_bac_code varchar2(50);

    val_code_chargement varchar2(280);
    val_etd_location varchar2(50);
    val_eta_location varchar2(50);
    val_ref_etd varchar2(50);
    val_ref_eta varchar2(50);
    val_etd_date varchar2(50);
    val_eta_date varchar2(50);
begin

	res := 0;
	msg := '';

    select
        cli_ref,
        trp_code,
        CODE_CHARGEMENT,
        PER_CODECOM,
        PER_CODEASS,
        FRAIS_PU,
        TTR_CODE,
        TRP_DEV_PU,
        TRP_PU,
        TRP_DEV_CODE,
        TRP_BTA_CODE,
        FRAIS_PLATEFORME,
        TRP_DEV_TAUX,
        DEV_CODE,
        DEV_TX,
        COMM_INTERNE,
        ETD_LOCATION,
        ETA_LOCATION,
        REF_ETD,
        REF_ETA,
        ETD_DATE,
        ETA_DATE,
        TRP_BAC_CODE,
        INSTRUCTIONS_LOGISTIQUE,
        INC_CODE
    into
        is_cur_cli_ref,
        is_trp_code,
        val_code_chargement,
        is_per_codecom,
        is_per_codeass,
        idc_frais_pu,
        is_ttr_code,
        idc_trp_dev_pu,
        idc_trp_pu,
        is_trp_dev_code,
        is_trp_bta_code,
        idc_frais_plateforme,
        idc_trp_dev_taux,
        is_dev_code,
        idc_dev_tx,
        is_comm_interne,
        val_etd_location,
        val_eta_location,
        val_ref_etd,
        val_ref_eta,
        val_etd_date,
        val_eta_date,
        is_trp_bac_code,
        ls_instructions_logistique,
        is_inc_code
    from GEO_ORDRE
    where ORD_REF = arg_ord_ref;

    ll_rc := arg_depdatp;

    if ll_rc is null then
        msg := 'Date de départ invalide';
        return;
    End IF;

    ll_rc	:= arg_livdatp;

    if ll_rc is null then
        msg := 'Date de livraison invalide';
        return;
    End If;

    If trunc(arg_livdatp) - trunc(arg_depdatp) < 0 Then
        msg := 'Date de livraison antérieure à la date de départ';
        return;
    End If;

    ls_depdatp := to_char(arg_depdatp,'yy/mm/dd hh24:mi:ss');
    ls_livdatp := to_char(arg_livdatp,'yy/mm/dd');

    f_create_ordre_v3(arg_soc_code, is_cur_cli_ref,arg_cen_ref,is_trp_code,is_ref_cli, false, false, ls_depdatp, 'ORD', ls_livdatp, ll_null, res, msg, ls_ord_ref_new);
    if (res <> 1) then
        msg := 'Erreur lors de la création de l''ordre : ' || msg;
        return;
    end if;

    --Prise en compte d'un potentiel retour de code erreur
    if substr(ls_ord_ref_new, 1, 3) = '%%%' then
        msg := 'Anomalie lors de la création de l''ordre';
        return;
    End If;

    If arg_code_chargement ='N' Then
        val_code_chargement := null;
    End IF;

    If arg_etd_location ='N' Then
        val_etd_location := null;
        val_ref_etd := null;
    End IF;

    If arg_eta_location ='N' Then
        val_eta_location := null;
        val_ref_eta := null;
    End IF;

    If arg_etd_date ='N' Then
        val_etd_date := null;
    End IF;

    If arg_eta_date ='N' Then
        val_eta_date := null;
    End IF;

    f_get_instruction_logistique(is_cur_cli_ref,arg_cen_ref, res, msg, ls_instructions_logistique);
    if (res <> 1) then
        msg := 'Erreur lors de la récupération des instructions logistiques : ' || msg;
        return;
    end if;

    update GEO_ORDRE
        set CODE_CHARGEMENT = val_code_chargement,
            PER_CODECOM =is_per_codecom,
            PER_CODEASS= is_per_codeass,
            FRAIS_PU =idc_frais_pu,
            TTR_CODE =is_ttr_code,
            TRP_DEV_PU = idc_trp_dev_pu,
            TRP_PU = idc_trp_pu,
            TRP_DEV_CODE = is_trp_dev_code,
            TRP_BTA_CODE = is_trp_bta_code,
            FRAIS_PLATEFORME = idc_frais_plateforme,
            TRP_DEV_TAUX = idc_trp_dev_taux,
            DEV_CODE=is_dev_code,
            DEV_TX =idc_dev_tx,
            COMM_INTERNE =is_comm_interne,
            ETD_LOCATION =val_etd_location,
            ETA_LOCATION =val_eta_location,
            ETD_DATE = val_etd_date,
            ETA_DATE = val_eta_date,
            REF_ETD = val_ref_etd,
            REF_ETA = val_ref_eta,
            TRP_BAC_CODE = is_trp_bac_code,
            INSTRUCTIONS_LOGISTIQUE =ls_instructions_logistique
        where ORD_REF = ls_ord_ref_new;

    If arg_inc_code = 'O' Then
        update GEO_ORDRE
        SET INC_CODE = is_inc_code
        where ORD_REF = ls_ord_ref_new;
    End IF;

    select NORDRE
    into ls_nordre_new
    from GEO_ORDRE
    where ORD_REF =ls_ord_ref_new;

    declare
        d_ligne varchar2(50);
    begin
        f_cree_ordre_duplique_ligne(arg_ord_ref,ls_ord_ref_new,arg_fourni ,arg_ach_pu,arg_vte_pu,arg_lib_dlv, res, msg, d_ligne);
        if  d_ligne = '%%%' then return; end if;
    end;

    commit;
    msg := 'Nouvel ordre :' || ls_nordre_new;

    f_insert_mru_ordre(ls_ord_ref_new,arg_username, res, msg);
    if (res <> 1) then
        msg := 'Erreur lors de la création de l''ordre MRU : ' || msg;
        return;
    end if;

	res := 1;
	msg := 'OK';
    nordre := ls_nordre_new;
	return;

end;
/

