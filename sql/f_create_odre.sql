CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREATE_ORDRE_V3(
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
    res IN OUT number,
    msg IN OUT varchar2,
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

    select CAM_CODE into ls_soc_cam_code from GEO_SOCIETE where soc_code = arg_soc_code;

    -- Pour la création des commandes EDI dans GEO_ORDRE
    f_nouvel_ordre(arg_soc_code, res, msg, ls_nordre);

    if substr(msg, 1, 3) = '%%%' then
        return;
    end if;

    select seq_ord_num.nextval into ll_ord_ref from dual;
    ls_ord_ref := to_char(ll_ord_ref);

    begin
        select cli_code, fldet_autom into ls_cli_code, ls_fldet_autom  from geo_client where cli_ref = arg_cli_ref and soc_code = arg_soc_code;
    exception when others then
        msg := '%%% Erreur sur code client : ' || arg_cli_ref || ' pour la société : ' || arg_soc_code;
        res := 0;
        return;
    end;

    begin
        select cen_code into ls_cen_code from geo_entrep where cen_ref = arg_cen_ref and cli_ref = arg_cli_ref;
    exception when others then
        msg := '%%% Erreur sur code entrepot : ' || arg_cen_ref || ' pour la société : ' || arg_soc_code;
        res := 0;
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

    if arg_trp_code is not null and length(arg_trp_code) > 0 then
        ls_trp_code := arg_trp_code;
    end if;

    if ls_trp_code is null or ls_trp_code = '' then
        ls_trp_code := '-';
    end if;

    DECLARE
        li_ret number;
    begin
      f_return_forfaits_trp( arg_cen_ref,ls_inc_code,ld_trp_dev_pu,ls_trp_bta_code,ls_trp_dev_code,arg_typ_ordre,res,msg,li_ret);
      If li_ret > 0 Then
            select   dev_tx into  ld_trp_dev_taux
            from  geo_devise_ref
            where dev_code = ls_trp_dev_code and
                    dev_code_ref = ls_soc_dev_code;

            ld_trp_pu := ld_dev_tx * ld_trp_dev_pu;
        else
            begin
                select DEV_CODE into ls_trp_dev_code from geo_transp where trp_code = ls_trp_code;
            exception when no_data_found then
                msg := 'Erreur: Pas de devise pour le transporteur ' || ls_trp_code || ' -> ' || SQLERRM;
                res := 0;
            end;
                    
            
            if ls_trp_dev_code is null or ls_trp_dev_code = '' then
                ls_trp_dev_code := ls_soc_dev_code;
                ld_trp_dev_taux := 1.0;
            else
            
                If ls_trp_dev_code <> ls_soc_dev_code then
                    select   dev_tx_achat into  ld_trp_dev_taux
                    from  geo_devise_ref
                    where     dev_code = ls_trp_dev_code and
                            dev_code_ref = ls_soc_dev_code;

                    if ld_trp_dev_taux is null or ld_trp_dev_taux = 0 then
                        ls_trp_dev_code := ls_soc_dev_code;
                        ld_trp_dev_taux := 1.0;
                    end if;                    

                    
                    ld_trp_dev_pu := ld_trp_pu;
                    ld_trp_pu:=ld_trp_dev_pu*ld_trp_dev_taux;
                else
                    ld_trp_dev_taux:= 1.0;
                    ld_trp_dev_pu := ld_trp_pu;
                end If;
            
            end if;
            
        end if;
    end;

    ls_inst_log := substr(coalesce(ls_instr_logist_client, ' ') || ' ' || coalesce(ls_instr_logist_entrep, ' '), 280);
    ldate_dep := SYSDATE;
    ldate_liv := SYSDATE + 1;
    ldate_version := SYSDATE;

    begin
        if arg_dat_dep is not null then
            ldate_dep := to_date(arg_dat_dep, 'yyyy-mm-dd hh24:mi:ss');
            -- ldate_liv = datetime(RelativeDate(Date(arg_datedep),1))
        end if;
        if arg_dat_liv is not null then
            ldate_liv := to_date(arg_dat_liv, 'yyyy-mm-dd hh24:mi:ss');
        end if;

        ls_DEPDATP_ASC := to_char(ldate_dep, 'yyyymmdd');

    exception when others then
        msg := msg || ' Erreur de formatage de la date';
        res := 0;
        return;
    end;

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
            CRT_CODE, CRT_BTA_CODE, CRT_PU, TRP_BAC_CODE,
            FLAG_GEO2
        ) VALUES (
             ls_ord_ref, arg_soc_code,ls_soc_cam_code,ls_nordre,ls_per_code_ass,ls_per_code_com,arg_cli_ref,ls_cli_code,arg_ref_cli,arg_cen_ref,ls_cen_code,ls_sco_code,ls_pays_code_entrepot,
             ls_dev_code,ld_dev_tx,ls_inc_code,ls_trp_code,ld_trp_pu,'N','N','N',ldate_dep,ldate_liv,ldate_dep,
             ls_tvt_code,ls_tvr_code,ls_mpm_code,ls_bpm_code,ls_echnbj,ls_echle,ld_remsf_tx,ld_remhf_tx,
             ls_tot_vte,'0','0','0',ls_tot_vte,ls_marge,'0','0','0','N','N',ls_flagbaf,ls_flagfac,
             ls_inst_log,ld_frais_pu,ls_frais_unite,'0',ls_nb_colis,ls_pds,ls_pds,ls_DEPDATP_ASC,'F',
             'N','N','N','N','N','N','N','N','N','N',ld_remsf_tx_mdd,'0 ','N', ls_soc_cam_code,'0','0','0',ls_typ_ordre, arg_num_edi, ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_taux, ld_trp_dev_pu, ls_fldet_autom,
             ls_crt_code, ls_crt_bta_code, ld_crt_pu, ls_trp_bac_code,
             'O'
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
CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREATE_ORDRE_V4(
    arg_societe IN varchar2,
    arg_client IN varchar2,
    arg_entrepot IN varchar2,
    arg_transporteur IN varchar2,
    arg_ref_cmd_cli IN varchar2,
    arg_is_baf IN char,
    arg_is_regulation IN char,
    arg_datedep IN timestamp,
    arg_type_ordre IN varchar2,
    arg_date_liv IN timestamp,
    arg_load_ref IN varchar2,
    res OUT number,
    msg OUT varchar2,
    ls_ord_ref OUT varchar2
)
AS
    ls_soc_dev_code GEO_SOCIETE.DEV_CODE%TYPE;
    ls_soc_cam_code GEO_SOCIETE.CAM_CODE%TYPE;

    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
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
    ld_frais_plateforme number;

    ld_dev_tx number;
    ls_dev_code_ref GEO_DEVISE.DEV_CODE%TYPE;
    ls_societe_dev_code varchar2(50);
    ls_ttr_code varchar2(50);

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

    ls_nordre varchar2(50);
BEGIN
    res := 0;
    msg := '';

    select CAM_CODE into ls_soc_cam_code from GEO_SOCIETE where soc_code = arg_societe;

    -- Pour la création des commandes TESCO dans GEO_ORDRE
    f_nouvel_ordre(arg_societe, res, msg, ls_nordre);

    if substr(msg, 1, 3) = '%%%' then
        return;
    end if;

    select seq_ord_num.nextval into ll_ord_ref from dual;
    ls_ord_ref := to_char(ll_ord_ref);

    begin
        select cli_code, fldet_autom into ls_cli_code, ls_fldet_autom  from geo_client where cli_ref = arg_client and soc_code = arg_societe;
    exception when others then
        msg := '%%% Erreur sur ref client : ' || arg_client || ' pour la société : ' || arg_societe;
        res := 0;
        return;
    end;

    begin
        select cen_code into ls_cen_code from geo_entrep where cen_ref = arg_entrepot and cli_ref = arg_client;
    exception when others then
        msg := '%%% Erreur sur ref entrepot : ' || arg_entrepot || ' pour la société : ' || arg_societe;
        res := 0;
        return;
    end;

    ls_typ_ordre := arg_type_ordre;
    If ls_typ_ordre is null OR ls_typ_ordre = '' THEN
        ls_typ_ordre := 'ORD';
    end if;

    -- données héritées du client
    begin
        select
            sco_code, pay_code, dev_code, mpm_code, bpm_code, echnbj, echle, tvt_code,
            crt_code, crt_bta_code, crt_pu, instructions_seccom, instructions_logistique,
            rem_sf_tx, rem_hf_tx, rem_sf_tx_mdd, tvr_code, frais_pu, frais_unite, per_code_com, per_code_ass, dluo, frais_plateforme
        into
            ls_sco_code, ls_pays_code_client, ls_dev_code, ls_mpm_code, ls_bpm_code, ls_echnbj, ls_echle, ls_tvt_code,
            ls_crt_code, ls_crt_bta_code, ld_crt_pu, ls_instr_seccom_client, ls_instr_logist_client,
            ld_remsf_tx, ld_remhf_tx, ld_remsf_tx_mdd, ls_tvr_code_client, ld_frais_pu, ls_frais_unite, ls_per_code_com, ls_per_code_ass, ls_dluo_client, ld_frais_plateforme
        from geo_client where cli_ref = arg_client;
    exception when others then
        msg := '%%% Erreur sur données client : ' || arg_client || ' pour la société : ' || arg_societe;
        return;
    end;

    select dev_code into ls_soc_dev_code from geo_societe where soc_code = arg_societe;
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
            instructions_seccom, instructions_logistique, trp_pu, trs_pu, gest_ref, ttr_code
        into
            ls_pays_code_entrepot, ls_inc_code, ls_tvr_code, ls_trp_code, ls_trp_bta_code, ls_trs_code, ls_trs_bta_code,
            ls_instr_seccom_entrep, ls_instr_logist_entrep, ld_trp_pu, ld_trs_pu, ls_gest_ref, ls_ttr_code
        from geo_entrep where cen_ref = arg_entrepot;
    exception when others then
        msg := '%%% Erreur sur données entrepot : ' || arg_entrepot || ' pour la société : ' || arg_societe;
        return;
    end;

    if arg_transporteur is not null and length(arg_transporteur )>0 then
        ls_trp_code := arg_transporteur;
    end if;

    if ls_trp_code is null or ls_trp_code = '' then
        ls_trp_code := '-';
    end if;

    DECLARE
        li_ret number;
    begin
        f_return_forfaits_trp( arg_entrepot,ls_inc_code,ld_trp_dev_pu,ls_trp_bta_code,ls_trp_dev_code,ls_typ_ordre,res,msg,li_ret);
        If li_ret > 0 Then
            select   dev_tx into  ld_trp_dev_taux
            from  geo_devise_ref
            where dev_code = ls_trp_dev_code and
                    dev_code_ref = ls_soc_dev_code;

            ld_trp_pu := ld_dev_tx * ld_trp_dev_pu;
        else
            begin
                select DEV_CODE into ls_trp_dev_code from geo_transp where trp_code = ls_trp_code;
            exception when no_data_found then
                msg := 'Erreur: Pas de devise pour le transporteur ' || ls_trp_code || ' -> ' || SQLERRM;
                res := 0;
            end;
                    
            
            if ls_trp_dev_code is null or ls_trp_dev_code = '' then
                ls_trp_dev_code := ls_soc_dev_code;
                ld_trp_dev_taux := 1.0;
            else
            
                If ls_trp_dev_code <> ls_soc_dev_code then
                    select   dev_tx_achat into  ld_trp_dev_taux
                    from  geo_devise_ref
                    where     dev_code = ls_trp_dev_code and
                            dev_code_ref = ls_soc_dev_code;

                    if ld_trp_dev_taux is null or ld_trp_dev_taux = 0 then
                        ls_trp_dev_code := ls_soc_dev_code;
                        ld_trp_dev_taux := 1.0;
                    end if;                    

                    
                    ld_trp_dev_pu := ld_trp_pu;
                    ld_trp_pu:=ld_trp_dev_pu*ld_trp_dev_taux;
                else
                    ld_trp_dev_taux:= 1.0;
                    ld_trp_dev_pu := ld_trp_pu;
                end If;
            
            end if;
            
        end if;
    end;

    ls_inst_log := substr(coalesce(ls_instr_logist_client, ' ') || ' ' || coalesce(ls_instr_logist_entrep, ' '),0, 280);
    ldate_dep := SYSDATE;
    ldate_liv := SYSDATE + 1;
    ldate_version := SYSDATE;

    begin
        if arg_datedep is not null then
            ldate_dep := arg_datedep;
            -- ldate_liv = datetime(RelativeDate(Date(arg_datedep),1))
        end if;
        if arg_date_liv is not null then
            ldate_liv := arg_date_liv;
        end if;

        ls_DEPDATP_ASC := to_char(ldate_dep, 'yyyymmdd');

    exception when others then
        msg := msg || ' Erreur de formatage de la date';
        res := 0;
        return;
    end;

    ls_tot_vte := '0';
    ls_marge := '0';
    ls_nb_colis := '0';
    ls_pds := '0';

    -- On crée l'ordre

    ls_flagbaf := 'N';
    if arg_is_baf = 'O' then
        ls_flagbaf := 'O';
    end if;

    ls_flagfac := 'N';
    if arg_is_regulation = 'O' then
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
            CRT_CODE, CRT_BTA_CODE, CRT_PU, TRP_BAC_CODE, CODE_CHARGEMENT, TTR_CODE, FRAIS_PLATEFORME,
            FLAG_GEO2
        ) VALUES (
             ls_ord_ref, arg_societe,ls_soc_cam_code,ls_nordre,ls_per_code_ass,ls_per_code_com,arg_client,ls_cli_code,arg_ref_cmd_cli,arg_entrepot,ls_cen_code,ls_sco_code,ls_pays_code_entrepot,
             ls_dev_code,ld_dev_tx,ls_inc_code,ls_trp_code,ld_trp_pu,'N','N','N',ldate_dep,ldate_liv,ldate_dep,
             ls_tvt_code,ls_tvr_code,ls_mpm_code,ls_bpm_code,ls_echnbj,ls_echle,ld_remsf_tx,ld_remhf_tx,
             ls_tot_vte,'0','0','0',ls_tot_vte,ls_marge,'0','0','0','N','N',ls_flagbaf,ls_flagfac,
             ls_inst_log,ld_frais_pu,ls_frais_unite,'0',ls_nb_colis,ls_pds,ls_pds,ls_DEPDATP_ASC,'F',
             'N','N','N','N','N','N','N','N','N','N',ld_remsf_tx_mdd,'0 ','N', ls_soc_cam_code,'0','0','0',ls_typ_ordre, '', ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_taux, ld_trp_dev_pu, ls_fldet_autom,
             ls_crt_code, ls_crt_bta_code, ld_crt_pu, ls_trp_bac_code, arg_load_ref, ls_ttr_code, ld_frais_plateforme,
             'O'
         );

         commit;
         res := 1;

    exception when others then
        msg := '%%% Erreur à la création de l~''ordre : ' || SQLERRM;
        rollback;
        return;
    end;
end;
/

CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_CREATE_LIGNE_ORDRE(
	arg_ord_ref IN varchar2,
	arg_art_ref IN varchar2,
	arg_fou_code IN varchar2,
	arg_cen_ref IN varchar2,
	arg_pal_nb_col IN number,
	arg_nb_pal IN number,
	arg_nb_colis IN number,
	arg_prix_vente IN number,
	arg_prix_mini IN number,
	arg_prog IN varchar2,
	arg_dluo IN varchar2,
    res OUT number,
    msg OUT varchar2,
	ls_orl_ref OUT varchar2
)
AS
    ls_ORD_REF varchar2(50);
    ls_CDE_NB_COL varchar2(50);
    ls_EXP_NB_COL varchar2(50);
    ls_EXP_PDS_BRUT varchar2(50);
    ls_EXP_PDS_NET varchar2(50);
    ls_ACH_DEV_CODE varchar2(50);
    ls_VTE_QTE varchar2(50);
    ls_FOU_CODE varchar2(50);
    ls_ART_REF varchar2(50);
    ls_ESP_CODE varchar2(50);
    ll_ach_dev_taux number;
    ls_ACH_DEV_PU varchar2(50);
    ls_PAL_CODE varchar2(50);
    ls_ACH_BTA_CODE varchar2(50);
    ls_VTE_BTA_CODE varchar2(50);
    ls_PAN_CODE varchar2(50);
    ll_PAL_NB_COL number;
    ll_cde_nb_pal number;
    ls_TOTVTE varchar2(50);
    ls_TOTACH varchar2(50);
    ls_TOTMOB varchar2(50);
    ls_orl_lig varchar2(50);
    ll_EXP_PDS_NET number;
    ll_ACH_PU number;
    ll_VTE_PU number;
    ld_pmb_per_com number;
    ld_pdnet_client number;
    ld_col_tare number;
    ld_ACH_QTE number;
    ld_pds_net number;
    ld_pds_brut number;
    ld_frais_pu number;
    ls_BAC_CODE varchar2(50);
    ls_art_ref_kit varchar2(50);
    ls_sco_code varchar2(50);
    ls_histo_ord varchar2(50);
    ls_PROP_CODE varchar2(50);
    ls_sto_ref varchar2(50);
    ls_desc varchar2(50);
    ll_stm_ref number;
    ls_stm_ref varchar2(50);
    ls_stm_desc varchar2(50);
    ls_nordre varchar2(50);
    ls_cli_code varchar2(50);
    ls_sql varchar2(50);
    ls_frais_unite varchar2(50);
    ll_qte_ini number;
    ll_qte_res number;
    ll_qte_restante number;
    ll_stock_nb_resa number;
    ls_ean_cli varchar2(50);
    ll_qte_art_cde number;
    ld_prix_vente number;

    ls_null varchar2(50);
    ls_trp_code varchar2(50);
    ls_trp_bta_code varchar2(50);
    ls_trp_dev_code varchar2(50);
    ld_trp_pu number;
    ld_dev_tx number;
    lb_affect boolean;
    ls_cat_code varchar2(50);
    ls_ori_code varchar2(50);
    ls_var_code varchar2(50);
    ls_tvt_code varchar2(50);
    ll_mode_culture number;
    ls_perequation varchar2(50);
    ls_frais_unite_mark varchar2(50);
    ll_k_frais number;
    ll_article_mode_culture number;
    ld_accompte number;
    ld_prix_mini number;
    ld_frais_pu_mark number;
    ls_indbloq_ach_dev_pu varchar2(50);
    ls_gtin_uc varchar2(50);
    ls_gtin_colis varchar2(50);
    ls_gtin varchar2(50);
    ls_soc_code varchar2(50);
    ld_remsf_tx number;
    ld_vte_pu_net number;
    ld_vte_mt_net number;
    ls_societe_dev_code varchar2(50);
    ls_cli_ref varchar2(50);
    ls_dev_code varchar2(50);
BEGIN
    res := 0;
    msg := '';

    --Recup du secteur de l'ordre
    begin
        select sco_code, soc_code into ls_sco_code, ls_soc_code from GEO_ORDRE where GEO_ORDRE.ORD_REF = arg_ord_ref;
    exception when no_data_found then
        msg := msg || ' Impossible de recuperer le secteur de l''ordre avec la ref ' || arg_ord_ref || SQLERRM;
        return;
    end;

    --On insert la ligne article générique art_ref = '000000'
    -- Recup de ORL suivante
    select F_SEQ_ORL_SEQ() into ls_ORL_REF FROM DUAL;
    --recherche du prochain numero de ligne dans nouvel ordre
    select TRIM(to_char(count(orl_ref)+1,'00')) into ls_orl_lig from GEO_ORDLIG where GEO_ORDLIG.ORD_REF = arg_ord_ref;
    --Recup du pal_code au niveau de l'entrepôt
    select PAL_CODE, CLI_REF into ls_pal_code, ls_cli_ref from GEO_ENTREP where cen_ref = arg_cen_ref;

    -- détermination du nbre de pal au sol
    ll_qte_art_cde := arg_nb_colis;

    --Tjs COLIS donc ll_qte_art_code
    ls_TOTVTE := to_char(arg_prix_vente * arg_nb_colis);

    --dans le cas de TESCO les ACH_PU = 0
    ll_ACH_PU := arg_prix_mini;
    ls_TOTACH := to_char(ll_ACH_PU * arg_nb_colis);
    ls_TOTMOB := '0';

    if ls_soc_code = 'BUK' and arg_prog = 'TESCO' then
        ls_prop_code := 'BW';
    else
        ls_prop_code := arg_fou_code;
    end if;

    select bac_code into ls_bac_code from geo_fourni where fou_code = ls_prop_code;
    select esp_code, gtin_uc into ls_esp_code, ls_gtin_uc from geo_article_colis where art_ref = arg_art_ref;

    select dev_code into ls_societe_dev_code from geo_societe where soc_code = ls_soc_code;

    select rem_sf_tx, dev_code into ld_remsf_tx, ls_dev_code from geo_client where cli_ref = ls_cli_ref;
    if ld_remsf_tx is null then
        ld_remsf_tx := 0;
    end if;
    if ld_remsf_tx <> 0 then
        ld_vte_pu_net	:= round(arg_prix_vente - arg_prix_vente * ld_remsf_tx * 0.01, 4);
    else
        ld_vte_pu_net	:= round(arg_prix_vente, 4);
    end if;

    ls_vte_bta_code := 'COLIS';
    ls_ach_bta_code := 'COLIS';

    --Alimentation de la devise
    if ls_dev_code = ls_societe_dev_code  then
        ll_ach_dev_taux := 1;
        ls_ach_dev_code := ls_societe_dev_code;
    else
        If ls_societe_dev_code is not null and  ls_societe_dev_code <> '' Then
            ls_ach_dev_code := ls_societe_dev_code;
        else
            ls_ach_dev_code := 'EUR';
        end If;
    end if;

    --fin Alimentation de la devise

    --New gestion des frais marketing
    if ls_soc_code <> 'BUK' then
            select O.tvt_code, O.sco_code into ls_tvt_code, ls_sco_code from geo_ordre O where O.ord_ref = arg_ord_ref;
            select A.mode_culture, A.cat_code, A.ori_code, A.var_code
            into ll_article_mode_culture, ls_cat_code, ls_ori_code, ls_var_code
            from geo_article_colis A
            where A.art_ref  = arg_art_ref;

            f_recup_frais(ls_var_code, ls_cat_code, ls_sco_code, ls_tvt_code, ll_article_mode_culture, ls_ori_code, ll_k_frais, msg);

            begin
                select frais_pu, frais_unite, accompte, perequation
                into ld_frais_pu_mark, ls_frais_unite_mark, ld_accompte, ls_perequation
                from geo_attrib_frais
                where k_frais = ll_k_frais;

                if ls_sco_code <> 'RET' then
                        ld_frais_pu := ld_frais_pu_mark;
                        ls_frais_unite := ls_frais_unite_mark;
                        if ls_perequation ='O' then
                            If ld_accompte is not null and ld_accompte > 0 then
                                ll_ach_pu := ld_accompte;
                                ls_ach_dev_pu	:= to_char(ld_accompte);
                                ls_ach_dev_code	:= 'EUR';
                                ll_ach_dev_taux	:= 1;
                                ls_indbloq_ach_dev_pu := 'O';
                                ls_ach_bta_code	:= 'KILO';
                            end if;
                        end if;
                end if;
            exception when others then
                ld_frais_pu := 0;
                ls_frais_unite := '';
                ls_indbloq_ach_dev_pu := 'N';
            end;

    else
    /*	if ls_dev_code = ls_societe_dev_code  then
            ll_ach_dev_taux = 1
            ls_ach_dev_code = ls_societe_dev_code
        else
            If not(isnull(ls_societe_dev_code)) and  ls_societe_dev_code <> '' Then
                ls_ach_dev_code = ls_societe_dev_code
            else
                ls_ach_dev_code = 'EUR'
            End If
    */
        if ls_dev_code <> ls_societe_dev_code  then
            begin
                select dev_tx into ll_ach_dev_taux
                from geo_devise_ref
                where dev_code = ls_dev_code and
                        dev_code_ref=ls_ach_dev_code;
            exception when others then
                msg := msg || '%%%erreur lecture devise';
                res := 0;
                return;
            end;
            ls_indbloq_ach_dev_pu := 'N';
        end if;
    end if;
    --fin marketing

    select gtin_uc, gtin_colis into ls_gtin_uc, ls_gtin_colis from geo_article_colis where art_ref = arg_art_ref;
    if ls_gtin_uc is not null and length(ls_gtin_uc) > 0 then
        ls_gtin := ls_gtin_uc;
    else
        ls_gtin := ls_gtin_colis;
    end if;

    BEGIN
        INSERT INTO GEO_ORDLIG (
        ORL_REF, ORD_REF, ORL_LIG, PAL_CODE, PAL_NB_COL, CDE_NB_PAL, CDE_NB_COL, EXP_NB_PAL, EXP_NB_COL, EXP_PDS_BRUT, EXP_PDS_NET, ACH_PU, ACH_DEV_CODE, ACH_BTA_CODE, ACH_QTE, VTE_PU,
        VTE_BTA_CODE, VTE_QTE, FOU_CODE, CDE_PDS_BRUT, CDE_PDS_NET, TOTVTE, TOTREM, TOTRES, TOTFRD, TOTACH, TOTMOB, TOTTRP, TOTTRS, TOTCRT, FLEXP, FLLIV, FLBAF, FLFAC, FOU_FLVER, VAR_RISTOURNE, FRAIS_PU,
        FLVERFOU, FLVERTRP, BAC_CODE, REMSF_TX, REMHF_TX, ART_REF
        , ESP_CODE, TOTFAD, ACH_DEV_TAUX, ACH_DEV_PU, ART_REF_KIT, GTIN_COLIS_KIT, REF_EDI_LIGNE, FRAIS_UNITE, PROPR_CODE, INDBLOQ_ACH_DEV_PU, LIB_DLV, vte_pu_net
        ) VALUES (
            ls_ORL_REF, arg_ord_ref, ls_orl_lig , ls_PAL_CODE, arg_pal_nb_col ,arg_nb_pal, ll_qte_art_cde, 0, 0,0, 0, ll_ach_pu, ls_ach_dev_code, ls_ach_bta_code, ll_qte_art_cde, arg_prix_vente,
            ls_vte_bta_code, ll_qte_art_cde, arg_fou_code, ld_pds_brut, ld_pds_net, ls_TOTVTE, 0, 0, 0, ls_TOTACH, ls_TOTMOB, 0, 0, 0, 'N', 'N', 'N', 'N', 'N', 'O', ld_frais_pu, 'N', 'N',ls_BAC_CODE, 0, 0, arg_art_ref,
            ls_ESP_CODE, 0, ll_ach_dev_taux, ll_ach_pu, arg_art_ref, ls_gtin, '', ls_frais_unite, ls_PROP_CODE, ls_indbloq_ach_dev_pu, arg_dluo, ld_vte_pu_net
        );
    exception when others then
        rollback;
        msg := 'Erreur à la création de la ligne d''ordre: ' || SQLERRM;
    END;

	res := 1;
    commit;

end;
/
