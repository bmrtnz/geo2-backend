CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREATE_ORDRE_V3(
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    arg_cli_ref IN GEO_CLIENT.CLI_REF%TYPE,
    arg_cen_ref IN GEO_ENTREP.CEN_REF%TYPE,
    arg_trp_code GEO_TRANSP.TRP_CODE%TYPE,
    arg_ref_cli GEO_ORDRE.REF_CLI%TYPE,
    arg_is_regulation boolean,
    arg_is_baf boolean,
    arg_dat_dep varchar2,
    arg_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE,
    arg_dat_liv varchar2,
    arg_num_edi number,
    res OUT number,
    msg OUT varchar2,
    ls_ord_ref OUT GEO_ORDRE.ORD_REF%TYPE
)
AS
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;
    ls_soc_cam_code GEO_SOCIETE.CAM_CODE%TYPE;

    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    ls_nordre varchar(100);
    ll_ord_ref number;
    ls_cli_code GEO_CLIENT.CLI_CODE%TYPE;
    ls_fldet_autom GEO_CLIENT.FLDET_AUTOM%TYPE;
    ls_cen_code GEO_ENTREP.CEN_CODE%TYPE;

    ls_sco_code GEO_CLIENT.SCO_CODE%TYPE;
    ls_pays_code_client GEO_CLIENT.PAY_CODE%TYPE;
    ls_dev_code GEO_CLIENT.DEV_CODE%TYPE;
    ls_mpm_code GEO_CLIENT.MPM_CODE%TYPE;
    ls_bpm_code GEO_CLIENT.BPM_CODE%TYPE;
    ls_echnbj GEO_CLIENT.ECHNBJ%TYPE;
    ls_echle GEO_CLIENT.ECHLE%TYPE;
    ls_tvt_code GEO_CLIENT.TVT_CODE%TYPE;
    ls_crt_code GEO_CLIENT.CRT_CODE%TYPE;
    ls_crt_bta_code GEO_CLIENT.CRT_BTA_CODE%TYPE;
    ld_crt_pu GEO_CLIENT.CRT_PU%TYPE;
    ls_instr_seccom_client GEO_CLIENT.INSTRUCTIONS_SECCOM%TYPE;
    ls_instr_logist_client GEO_CLIENT.INSTRUCTIONS_LOGISTIQUE%TYPE;
    ld_remsf_tx GEO_CLIENT.REM_SF_TX%TYPE;
    ld_remhf_tx GEO_CLIENT.REM_HF_TX%TYPE;
    ld_remsf_tx_mdd GEO_CLIENT.REM_SF_TX_MDD%TYPE;
    ls_tvr_code_client GEO_CLIENT.TVR_CODE%TYPE;
    ld_frais_pu GEO_CLIENT.FRAIS_PU%TYPE;
    ls_frais_unite GEO_CLIENT.FRAIS_UNITE%TYPE;
    ls_per_code_com GEO_CLIENT.PER_CODE_COM%TYPE;
    ls_per_code_ass GEO_CLIENT.PER_CODE_ASS%TYPE;
    ls_dluo_client GEO_CLIENT.DLUO%TYPE;

    ld_dev_tx number;
    ls_dev_code_ref GEO_DEVISE.DEV_CODE%TYPE;

    ls_pays_code_entrepot GEO_ENTREP.PAY_CODE%TYPE;
    ls_inc_code GEO_ENTREP.INC_CODE%TYPE;
    ls_tvr_code GEO_ENTREP.TVR_CODE%TYPE;
    ls_trp_code GEO_ENTREP.TRP_CODE%TYPE;
    ls_trp_bta_code GEO_ENTREP.TRP_BTA_CODE%TYPE;
    ls_trs_code GEO_ENTREP.TRS_CODE%TYPE;
    ls_trs_bta_code GEO_ENTREP.TRS_BTA_CODE%TYPE;
    ls_instr_seccom_entrep GEO_ENTREP.INSTRUCTIONS_SECCOM%TYPE;
    ls_instr_logist_entrep GEO_ENTREP.INSTRUCTIONS_LOGISTIQUE%TYPE;
    ld_trp_pu GEO_ENTREP.TRP_PU%TYPE;
    ld_trs_pu GEO_ENTREP.TRS_PU%TYPE;
    ls_gest_ref GEO_ENTREP.GEST_REF%TYPE;

    ls_trp_dev_code GEO_TRANSP.DEV_CODE%TYPE;
    ld_trp_dev_taux number;
    ld_trp_dev_pu number;
    ldate_dep GEO_ORDRE.DEPDATP%TYPE;
    ldate_liv GEO_ORDRE.LIVDATP%TYPE;
    ls_inst_log GEO_ORDRE.INSTRUCTIONS_LOGISTIQUE%TYPE;
    ls_DEPDATP_ASC varchar2(20);
    ldate_version date;

    ls_flagbaf char(1);
    ls_flagfac char(1);

    ls_tot_vte GEO_ORDRE.TOTVTE%TYPE := '0';
    ls_marge GEO_ORDRE.TOTMOB%TYPE := '0';
    ls_nb_colis GEO_ORDRE.TOTCOL%TYPE := '0';
    ls_pds GEO_ORDRE.TOTPDSNET%TYPE := '0';
    ls_trp_bac_code GEO_ORDRE.TRP_BAC_CODE%TYPE := '';
BEGIN
    -- correspond à f_create_ordre_v3.pbl
    msg := '';
    res := 0;

    -- Pour la création des commandes EDI dans GEO_ORDRE
    f_nouvel_ordre(arg_soc_code, res, msg, ls_nordre);

    if substr(msg, 1, 3) = '%%%' then
        return;
    end if;

    select seq_ord_num.nextval into ll_ord_ref from dual;
    ls_ord_ref := to_char(ll_ord_ref, '000000');

    begin
        select cli_code, fldet_autom into ls_cli_code, ls_fldet_autom  from geo_client where cli_ref = arg_cli_ref and soc_code = arg_soc_code;
    exception when others then
        msg := '%%% Erreur sur code client : ' || arg_cli_ref || ' pour la société : ' || arg_soc_code;
        return;
    end;

    begin
        select cen_code into ls_cen_code from geo_entrep where cen_ref = arg_cen_ref and cli_ref = arg_cli_ref;
    exception when others then
        msg := '%%% Erreur sur code entrepot : ' || arg_cen_ref || ' pour la société : ' || arg_soc_code;
        return;
    end;

    ls_typ_ordre := arg_typ_ordre;
    If ls_typ_ordre is null OR ls_typ_ordre = '' THEN
        ls_typ_ordre := 'ORD';
    end if;

    -- données héritées du client
    begin
        select
            sco_code, pay_code, dev_code, mpm_code, bpm_code, echnbj, echle, tvt_code,
            crt_code, crt_bta_code, crt_pu, instructions_seccom, instructions_logistique,
            rem_sf_tx, rem_hf_tx, rem_sf_tx_mdd, tvr_code, frais_pu, frais_unite, per_code_com, per_code_ass, dluo
        into
            ls_sco_code, ls_pays_code_client, ls_dev_code, ls_mpm_code, ls_bpm_code, ls_echnbj, ls_echle, ls_tvt_code,
            ls_crt_code, ls_crt_bta_code, ld_crt_pu, ls_instr_seccom_client, ls_instr_logist_client,
            ld_remsf_tx, ld_remhf_tx, ld_remsf_tx_mdd, ls_tvr_code_client, ld_frais_pu, ls_frais_unite, ls_per_code_com, ls_per_code_ass, ls_dluo_client
        from geo_client where cli_ref = arg_cli_ref;
    exception when others then
        msg := '%%% Erreur sur données client : ' || arg_cli_ref || ' pour la société : ' || arg_soc_code;
        return;
    end;

    select dev_code into ls_soc_dev_code from geo_societe where soc_code = arg_soc_code;
    if ls_dev_code = ls_soc_dev_code then
        ld_dev_tx := 1;
    else
        If ls_soc_dev_code is not null and ls_soc_dev_code <> '' Then
            ls_dev_code_ref := ls_soc_dev_code;
        else
            ls_dev_code_ref := 'EUR';
        End If;

        begin
            select dev_tx into ld_dev_tx
            from geo_devise_ref
            where dev_code = ls_dev_code and
                    dev_code_ref= ls_dev_code_ref;
        exception when others then
            msg := '%%%erreur lecture devise';
            return;
        end;
    end if;

    -- données héritées de l'entrepot
    ld_trs_pu := 0;

    begin
        select
            pay_code, inc_code, tvr_code, trp_code, trp_bta_code, trs_code, trs_bta_code,
            instructions_seccom, instructions_logistique, trp_pu, trs_pu, gest_ref
        into
            ls_pays_code_entrepot, ls_inc_code, ls_tvr_code, ls_trp_code, ls_trp_bta_code, ls_trs_code, ls_trs_bta_code,
            ls_instr_seccom_entrep, ls_instr_logist_entrep, ld_trp_pu, ld_trs_pu, ls_gest_ref
        from geo_entrep where cen_ref = arg_cen_ref;
    exception when others then
        msg := '%%% Erreur sur données entrepot : ' || arg_cen_ref || ' pour la société : ' || arg_soc_code;
        return;
    end;

    if arg_trp_code is not null and arg_trp_code <> '' then
        ls_trp_code := arg_trp_code;
    end if;

    if ls_trp_code is null or ls_trp_code = '' then
        ls_trp_code := '-';
    end if;

    select DEV_CODE into ls_trp_dev_code from geo_transp where trp_code = ls_trp_code;
    if ls_trp_dev_code is null or ls_trp_dev_code = '' then
        ls_trp_dev_code := ls_soc_dev_code;
        ld_trp_dev_taux := 1.0;
    else
        select dev_tx_achat into ld_trp_dev_taux from geo_devise_ref where dev_code = ls_trp_dev_code and dev_code_ref = ls_soc_dev_code;

        if ld_trp_dev_taux is null or ld_trp_dev_taux = 0 then
            ls_trp_dev_code := ls_soc_dev_code;
            ld_trp_dev_taux := 1.0;
        end if;
    end if;

    ld_trp_dev_pu := ld_trp_pu / ld_trp_dev_taux;

    ls_inst_log := substr(coalesce(ls_instr_logist_client, ' ') || ' ' || coalesce(ls_instr_logist_entrep, ' '), 280);
    ldate_dep := SYSDATE;
    ldate_liv := SYSDATE + 1;
    ldate_version := SYSDATE;

    if arg_dat_dep is not null and arg_dat_dep <> '' then
        ldate_dep := to_date(arg_dat_dep);
        -- ldate_liv = datetime(RelativeDate(Date(arg_datedep),1))
    end if;
    if arg_dat_liv is not null and arg_dat_liv <> '' then
        ldate_liv := to_date(arg_dat_liv);
    end if;

    ls_DEPDATP_ASC := to_char(ldate_dep, 'yyyymmdd');

    ls_tot_vte := '0';
    ls_marge := '0';
    ls_nb_colis := '0';
    ls_pds := '0';

    -- On crée l'ordre

    ls_flagbaf := 'N';
    if arg_is_baf = true then
        ls_flagbaf := 'O';
    end if;

    ls_flagfac := 'N';
    if arg_is_regulation = true then
        ls_flagfac := 'O';
        ls_flagbaf := 'O';
    end if;

    begin
        INSERT INTO GEO_ORDRE (
            ORD_REF,SOC_CODE,CAM_CODE,NORDRE,PER_CODECOM,PER_CODEASS,CLI_REF,CLI_CODE,REF_CLI,CEN_REF,CEN_CODE,SCO_CODE,PAY_CODE,
            DEV_CODE,DEV_TX,INC_CODE,TRP_CODE,TRP_PU,TRP_PRIX_VISIBLE,TRS_PRIX_VISIBLE,CRT_PRIX_VISIBLE,DEPDATP,LIVDATP,CREDAT,
            TVT_CODE,TVR_CODE,MPM_CODE,BPM_CODE,ENT_ECHNBJ,ENT_ECHLE,REMSF_TX,REMHF_TX,
            TOTVTE,TOTREM,TOTRES,TOTFRD,TOTACH,TOTMOB,TOTTRP,TOTTRS,TOTCRT,FLEXP,FLLIV,FLBAF,FLFAC,
            INSTRUCTIONS_LOGISTIQUE,FRAIS_PU,FRAIS_UNITE,TOTPAL,TOTCOL,TOTPDSNET,TOTPDSBRUT,DEPDATP_ASC,FACTURE_AVOIR,
            FLAG_QP,FLAG_UDC,ACK_TRANSP,FLAG_PUBLIC,VENTE_COMMISSION,FLBAGQP,FLGENQP,FBAGUDC,FLGENUDC,INVOIC,REM_SF_TX_MDD,PAL_NB_SOL,
            INVOIC_DEMAT,CAME_CODE,TOTFAD,TOT_CDE_NB_PAL,TOT_EXP_NB_PAL,TYP_ORDRE, REF_EDI_ORDRE, TRP_BTA_CODE, TRP_DEV_CODE, TRP_DEV_TAUX, TRP_DEV_PU, FLDET_AUTOM,
            CRT_CODE, CRT_BTA_CODE, CRT_PU, TRP_BAC_CODE
        ) VALUES (
             ls_ord_ref, arg_soc_code,ls_soc_cam_code,ls_nordre,ls_per_code_ass,ls_per_code_com,arg_cli_ref,ls_cli_code,arg_ref_cli,arg_cen_ref,ls_cen_code,ls_sco_code,ls_pays_code_entrepot,
             ls_dev_code,ld_dev_tx,ls_inc_code,ls_trp_code,ld_trp_pu,'N','N','N',ldate_dep,ldate_liv,ldate_dep,
             ls_tvt_code,ls_tvr_code,ls_mpm_code,ls_bpm_code,ls_echnbj,ls_echle,ld_remsf_tx,ld_remhf_tx,
             ls_tot_vte,'0','0','0',ls_tot_vte,ls_marge,'0','0','0','N','N',ls_flagbaf,ls_flagfac,
             ls_inst_log,ld_frais_pu,ls_frais_unite,'0',ls_nb_colis,ls_pds,ls_pds,ls_DEPDATP_ASC,'F',
             'N','N','N','N','N','N','N','N','N','N',ld_remsf_tx_mdd,'0 ','N', ls_soc_cam_code,'0','0','0',ls_typ_ordre, arg_num_edi, ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_taux, ld_trp_dev_pu, ls_fldet_autom,
             ls_crt_code, ls_crt_bta_code, ld_crt_pu, ls_trp_bac_code
         );

        begin
            update GEO_EDI_ORDRE set ORD_REF = ls_ord_ref WHERE REF_EDI_ORDRE = arg_num_edi;
            commit;

            res := 1;
        exception when others then
            msg := '%%% Erreur à la création de la mise à jour de l~''ordre EDI : ' || SQLERRM;
            return;
        end;
    exception when others then
        msg := '%%% Erreur à la création de l~''ordre : ' || SQLERRM;
        return;
    end;

end F_CREATE_ORDRE_V3;
/

