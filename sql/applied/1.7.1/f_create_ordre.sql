CREATE OR REPLACE PROCEDURE "GEO_ADMIN".F_CREATE_ORDRE(
    arg_soc_code varchar2,
    arg_client varchar2,
    arg_entrepot varchar2,
    arg_transporteur varchar2,
    arg_bon_retour varchar2,
    arg_is_regulation boolean,
    arg_is_baf boolean,
    arg_datedep date,
    res IN OUT number,
    msg IN OUT varchar2,
    ls_ord_ref IN OUT varchar2
)
AS
    ll_ord_ref number;
    ls_cli_ref varchar2(50);
    ls_cen_ref varchar2(50);
    ls_dev_code_ref varchar2(50);
    ld_trp_dev_pu number;
    ls_trp_dev_code varchar2(50);
    ld_trp_dev_taux number;

    -- données héritées du client
    ls_sco_code varchar2(50);
    ls_pays_code_client varchar2(50);
    ls_dev_code varchar2(50);
    ls_mpm_code varchar2(50);
    ls_bpm_code varchar2(50);
    ls_echnbj varchar2(50);
    ls_echle varchar2(50);
    ls_tvt_code varchar2(50);
    ls_crt_code varchar2(50);
    ls_crt_bta_code varchar2(50);
    ld_crt_pu varchar2(50);
    ls_instr_seccom_client varchar2(50);
    ls_instr_logist_client varchar2(50);
    ls_tvr_code_client varchar2(50);
    ls_frais_unite varchar2(50);
    ls_per_code_com varchar2(50);
    ls_per_code_ass varchar2(50);
    ls_dluo_client varchar2(50);

    ld_remsf_tx number;
    ld_remhf_tx number;
    ld_remsf_tx_mdd number;
    ld_frais_pu number;
    ld_dev_tx number;

    -- données héritées de l'entrepot
    ls_inc_code varchar2(50);
    ls_tvr_code varchar2(50);
    ls_pays_code_entrepot varchar2(50);
    ls_instr_seccom_entrep varchar2(50);
    ls_instr_logist_entrep varchar2(50);
    ls_trp_code varchar2(50);
    ls_trp_bta_code varchar2(50);
    ls_trs_code varchar2(50);
    ls_trs_bta_code varchar2(50);
    ls_gest_ref varchar2(50);
    ld_trp_pu number;
    ld_trs_pu number;

    --
    ls_inst_log varchar2(50);
    ldate_dep timestamp;
    ldate_liv timestamp;
    ls_DEPDATP_ASC varchar2(50);
    ls_soc_dev_code varchar2(50);
    ls_tot_vte varchar2(50) := '0';
    ls_marge varchar2(50) := '0';
    ls_nb_colis varchar2(50) := '0';
    ls_pds varchar2(50) := '0';
    ls_flagbaf varchar2(50);
    ls_flagfac varchar2(50);
    gs_cam_code varchar2(50);
    ls_nordre varchar2(50);
	forfait_res number;
BEGIN
    res := 0;
    msg := '';

    f_nouvel_ordre(arg_soc_code, res, msg, ls_nordre);
    if res = 0 then
        return;
    end if;

    select seq_ord_num.nextval into ll_ord_ref from dual;
    ls_ord_ref	:= to_char(ll_ord_ref);

    select CAM_CODE into gs_cam_code from geo_societe where soc_code = arg_soc_code;

    begin
        select cli_ref into ls_cli_ref from geo_client where cli_code = arg_client and soc_code = arg_soc_code;
    exception when others then
        res := 0;
        msg := '%%% Erreur sur code client : ' || arg_client || ' pour la société : ' || arg_soc_code;
        return;
    end;

    begin
        select cen_ref into ls_cen_ref from geo_entrep where cen_code = arg_entrepot and cli_ref = ls_cli_ref;
    exception when others then
        res := 0;
        msg := '%%% Erreur sur code entrepot : ' || arg_entrepot || ' pour la société : ' || arg_soc_code;
        return;
    end;

    begin
        select
            sco_code, pay_code, dev_code, mpm_code, bpm_code, echnbj, echle, tvt_code,
            crt_code, crt_bta_code, crt_pu, instructions_seccom, instructions_logistique,
            rem_sf_tx, rem_hf_tx, rem_sf_tx_mdd, tvr_code, frais_pu, frais_unite, per_code_com, per_code_ass, dluo
        into
            ls_sco_code, ls_pays_code_client, ls_dev_code, ls_mpm_code, ls_bpm_code, ls_echnbj, ls_echle, ls_tvt_code,
            ls_crt_code, ls_crt_bta_code, ld_crt_pu, ls_instr_seccom_client, ls_instr_logist_client,
            ld_remsf_tx, ld_remhf_tx, ld_remsf_tx_mdd, ls_tvr_code_client, ld_frais_pu, ls_frais_unite, ls_per_code_com, ls_per_code_ass, ls_dluo_client
        from geo_client where cli_ref = ls_cli_ref;
    exception when others then
        res := 0;
        msg := '%%% Erreur sur données client : ' || arg_client || ' pour la société : ' || arg_soc_code;
        return;
    end;


    select dev_code into ls_soc_dev_code
    from geo_societe
    where soc_code = arg_soc_code;

    if ls_dev_code = ls_soc_dev_code   then
        ld_dev_tx := 1;
    else
        If arg_soc_code is not null and ls_soc_dev_code is not null and  ls_soc_dev_code <> '' Then
            ls_dev_code_ref := ls_soc_dev_code;
        else
            ls_dev_code_ref := 'EUR';
        End If;

        begin
            select dev_tx into ld_dev_tx
            from geo_devise_ref
            where 	dev_code = ls_dev_code and
                        dev_code_ref=ls_dev_code_ref ;
        exception when others then
            res := 0;
            msg := '%%%erreur lecture devise';
            return;
        end;
    end if;

    ld_trs_pu := 0;

    begin
        select
            pay_code, inc_code, tvr_code, trp_code, trp_bta_code, trs_code, trs_bta_code,
            instructions_seccom, instructions_logistique, trp_pu, trs_pu, gest_ref
        into
            ls_pays_code_entrepot, ls_inc_code, ls_tvr_code, ls_trp_code, ls_trp_bta_code, ls_trs_code, ls_trs_bta_code,
            ls_instr_seccom_entrep, ls_instr_logist_entrep, ld_trp_pu, ld_trs_pu, ls_gest_ref
        from geo_entrep where cen_ref = ls_cen_ref;
    exception when others then
        res := 0;
        msg := '%%% Erreur sur données entrepot : ' || arg_entrepot || ' pour la société : ' || arg_soc_code;
        return;
    end;

    if arg_transporteur is not null and arg_transporteur <> '' then
        ls_trp_code := arg_transporteur;
    end if;

    if ls_trp_code is null or ls_trp_code = '' then
        ls_trp_code := '-';
    end if;

    ls_inst_log := ls_instr_logist_client || ' ' || ls_instr_logist_entrep;
    ldate_dep := CURRENT_TIMESTAMP;
    ldate_liv := CURRENT_TIMESTAMP + 1;

    if arg_datedep is not null then
        ldate_dep := arg_datedep;
        ldate_liv := arg_datedep + 1;
    end if;

    ls_DEPDATP_ASC := to_char(ldate_dep, 'yyyymmdd');

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

    f_return_forfaits_trp( ls_cen_ref,ls_inc_code,ld_trp_dev_pu,ls_trp_bta_code,ls_trp_dev_code,null,res,msg,forfait_res);
    If forfait_res > 0 Then
            select   dev_tx into  ld_trp_dev_taux
            from  geo_devise_ref
            where dev_code = ls_trp_dev_code and
                    dev_code_ref = ls_soc_dev_code;

            ld_trp_pu := ld_dev_tx * ld_trp_dev_pu;
    ENd IF;


    INSERT INTO GEO_ORDRE (
        ORD_REF,SOC_CODE,CAM_CODE,NORDRE,PER_CODEASS,PER_CODECOM,CLI_REF,CLI_CODE,REF_CLI,CEN_REF,CEN_CODE,SCO_CODE,PAY_CODE,
        DEV_CODE,DEV_TX,INC_CODE,TRP_CODE,TRP_PU,TRP_PRIX_VISIBLE,TRS_PRIX_VISIBLE,CRT_PRIX_VISIBLE,DEPDATP,LIVDATP,CREDAT,
        TVT_CODE,TVR_CODE,MPM_CODE,BPM_CODE,ENT_ECHNBJ,ENT_ECHLE,REMSF_TX,REMHF_TX,
        TOTVTE,TOTREM,TOTRES,TOTFRD,TOTACH,TOTMOB,TOTTRP,TOTTRS,TOTCRT,FLEXP,FLLIV,FLBAF,FLFAC,
        INSTRUCTIONS_LOGISTIQUE,FRAIS_PU,FRAIS_UNITE,TOTPAL,TOTCOL,TOTPDSNET,TOTPDSBRUT,DEPDATP_ASC,FACTURE_AVOIR,
        FLAG_QP,FLAG_UDC,ACK_TRANSP,FLAG_PUBLIC,VENTE_COMMISSION,FLBAGQP,FLGENQP,FBAGUDC,FLGENUDC,INVOIC,REM_SF_TX_MDD,PAL_NB_SOL,INVOIC_DEMAT,CAME_CODE,TOTFAD,TOT_CDE_NB_PAL,TOT_EXP_NB_PAL
    ) VALUES (
        ls_ord_ref,arg_soc_code,gs_cam_code,ls_nordre,'GW','GW',ls_cli_ref,arg_client,arg_bon_retour,ls_cen_ref,arg_entrepot,ls_sco_code,ls_pays_code_entrepot,
        ls_dev_code,ld_dev_tx,ls_inc_code,ls_trp_code,'0','N','N','N',ldate_dep,ldate_liv,ldate_dep,
        ls_tvt_code,ls_tvr_code,ls_mpm_code,ls_bpm_code,ls_echnbj,ls_echle,'0','0',
        ls_tot_vte,'0','0','0',ls_tot_vte,ls_marge,'0','0','0','N','N',ls_flagbaf,ls_flagfac,
        ls_inst_log,'0','','0',ls_nb_colis,ls_pds,ls_pds,ls_DEPDATP_ASC,'A',
        'N','N','N','O','N','N','N','N','N','N','0','0 ','N',gs_cam_code,'0','0','0'
    );

    commit;
    res := 1;

exception when others then
    msg := '%%% Erreur à la création de l~''ordre : ' || ' ' || SQLERRM;
    res := 0;
end;
/

