-- f_detail_ligne_sauve

CREATE OR REPLACE PROCEDURE F_DETAIL_LIGNE_SAUVE (
    s_orl_ref IN OUT geo_ordlig.ORL_REF%TYPE,
    s_exp_nb_col IN OUT varchar2,
    s_pal_nb_col IN OUT varchar2,
    s_exp_pds_brut IN OUT varchar2,
    s_exp_pds_net IN OUT varchar2,
    s_cq_ref IN OUT varchar2,
    s_exp_nb_pal IN OUT varchar2,
    s_ach_qte IN OUT varchar2,
    s_vte_qte IN OUT varchar2,
    arg_index IN varchar2,
    d_tot_exp_nb_pal IN OUT number,
    d_tot_exp_nb_col IN OUT number,
    d_tot_exp_pds_brut IN OUT number,
    d_tot_exp_pds_net IN OUT number,
	arg_check_palette IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    is_msg varchar2(50):= '<msg>';
    is_msgz varchar2(50) := '</msg>';

    ld_exp_nb_pal number;
    ld_exp_nb_col number;
    ld_exp_pds_brut number;
    ld_exp_pds_net number;
    ld_vte_qte number;
    ld_ach_qte number;
    ld_pal_nb_col number;
    ll_rc number;
    ll_ind number;
    is_crlf varchar2(50);
BEGIN
    -- msg := '';
    res := 1;

    if s_exp_nb_pal = '' then s_exp_nb_pal := '0'; end if;
    if s_exp_nb_col = '' then s_exp_nb_col := '0'; end if;
    if s_pal_nb_col = '' then s_pal_nb_col := '0'; end if;
    if s_exp_pds_brut = '' then s_exp_pds_brut := '0'; end if;
    if s_exp_pds_net = '' then s_exp_pds_net := '0'; end if;
    if s_vte_qte= '' then s_vte_qte := '0'; end if;
    if s_ach_qte = '' then s_ach_qte := '0'; end if;
        
    begin
        ld_exp_nb_pal := to_number(s_exp_nb_pal);
        if ld_exp_nb_pal > 66 and arg_check_palette <> 'N' then
            -- ll_rc	:= f_xml_input_status(arg_ds_status, 'M_' || arg_index, false)
            msg := msg || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur nbre palettes trop grand : ' || s_exp_nb_pal || is_msgz || is_crlf;
        /*
        elseif ld_exp_nb_pal <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'M_' || arg_index, false)
            msg = msg || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur nbre palettes invalide : ' || s_exp_nb_pal || is_msgz || is_crlf
        */
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'M_' || arg_index, true)
        end if;
    exception when others then
        ld_exp_nb_pal	:= 0;
	    -- ll_rc	= f_xml_input_status(arg_ds_status, 'M_' || arg_index, false)
	    msg := msg  || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur nbre palettes : ' || s_exp_nb_pal || is_msgz || is_crlf;
        res := 0;
    end;

    begin
        ld_exp_nb_col := to_number(s_exp_nb_col);
        if ld_exp_nb_col > 6700 then
            -- ll_rc	:= f_xml_input_status(arg_ds_status, 'N_' || arg_index, false)
            msg := msg || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' ||  'pb --> nbre colis trop grand : ' || s_exp_nb_col || is_msgz || is_crlf;
        /*
        elseif ld_exp_nb_col <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'N_' || arg_index, false)
            msg = msg || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' ||  'pb --> nbre colis invalide : ' || s_exp_nb_col || is_msgz || is_crlf
        */
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'N_' || arg_index, true)
        end if;
    exception when others then
        res := 0;
        ld_exp_nb_col	:= 0;
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'N_' || arg_index, false)
        msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb --> nbre colis : ' || s_exp_nb_col || is_msgz || is_crlf;
    end;	

    begin
        ld_pal_nb_col := to_number(s_pal_nb_col);
       if ld_pal_nb_col > 500 and arg_check_palette <> 'N' then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'O_' || arg_index, false)
            msg := msg || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' ||  'pb --> nbre colis / palette trop grand : ' || s_pal_nb_col || is_msgz || is_crlf;
        /*
        elseif ld_pal_nb_col <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'O_' || arg_index, false)
            msg = msg || is_msg || 'ligne ' || to_char(ll_ind|| 1) || ' ' ||  'pb --> nbre colis / palette invalide : ' || s_pal_nb_col || is_msgz || is_crlf
        */
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'O_' || arg_index, true)
        end if;
    exception when others then
        res := 0;
        ld_pal_nb_col	:= 0;
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'O_' || arg_index, false)
        msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur nbre colis : ' || s_pal_nb_col || is_msgz || is_crlf;
    end;			

    begin
        ld_exp_pds_brut := to_number(s_exp_pds_brut);   
       if ld_exp_pds_brut > 50000 then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'P_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds brut > 50 tonnes : ' || s_exp_pds_brut || is_msgz || is_crlf;
        /*
        elseif ld_exp_pds_brut <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'P_' || arg_index, false)
            msg = msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds brut invalide : ' || s_exp_pds_brut || is_msgz || is_crlf
        */
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'P_' || arg_index, true)
        end if;
    exception when others then
        res := 0;
        ld_exp_pds_brut	:= 0;
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'O_' || arg_index, false)
        msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds brut : ' || s_exp_pds_brut || is_msgz || is_crlf;
    end;	

    begin
        ld_exp_pds_net := to_number(s_exp_pds_net);
       if ld_exp_pds_net > 50000 then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net > 50 tonnes : ' || s_exp_pds_net || is_msgz || is_crlf;
        /*
        elseif ld_exp_pds_net <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg = msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net invalide : ' || s_exp_pds_net || is_msgz || is_crlf
        */
        elsif ld_exp_pds_net > ld_exp_pds_brut then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net > pds brut : ' || s_exp_pds_net || '>' || s_exp_pds_brut || is_msgz || is_crlf;
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, true)
        end if;
    exception when others then
        res := 0;
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
        ld_exp_pds_net 	:= 0;
        msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net : ' ||s_exp_pds_net || is_msgz || is_crlf;
    end;				

    begin
        ld_vte_qte := to_number(s_vte_qte);
       if ld_vte_qte > 50000 then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net > 50 tonnes : ' || s_exp_pds_net || is_msgz || is_crlf;
        /*
        elseif ld_exp_pds_net <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg = msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net invalide : ' || s_exp_pds_net || is_msgz || is_crlf
        */
        elsif ld_exp_pds_net > ld_exp_pds_brut then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net > pds brut : ' || s_exp_pds_net || '>' || s_exp_pds_brut || is_msgz || is_crlf;
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, true)
        end if;
    exception when others then
        res := 0;
        ld_vte_qte	:= 0;
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'R_' || arg_index, false)
        msg := msg || is_msg || 'pb sur qté vente : ' ||s_vte_qte || is_msgz || is_crlf;
    end;				

    begin
        ld_ach_qte := to_number(s_ach_qte);
       if ld_ach_qte > 50000 then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net > 50 tonnes : ' || s_exp_pds_net || is_msgz || is_crlf;
        /*
        elseif ld_exp_pds_net <= 0 then
            ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg = msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net invalide : ' || s_exp_pds_net || is_msgz || is_crlf
        */
        elsif ld_exp_pds_net > ld_exp_pds_brut then
            -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
            msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur pds net > pds brut : ' || s_exp_pds_net || '>' || s_exp_pds_brut || is_msgz || is_crlf;
        -- else
        --     ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, true)
        end if;
    exception when others then
        res := 0;
        ld_ach_qte	:= 0;
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'S_' || arg_index, false)
        msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pb sur qté achat : ' ||s_ach_qte || is_msgz || is_crlf;
    end;				

    if to_number(s_exp_nb_col) <> 0 and (to_number(s_exp_pds_net) = 0 or to_number(s_exp_pds_brut) = 0) then
        -- ll_rc	= f_xml_input_status(arg_ds_status, 'Q_' || arg_index, false)
        msg := msg || is_msg ||  'ligne ' || to_char(ll_ind|| 1) || ' ' || 'pds net ou pds brut non renseigné : ' || s_exp_pds_net || ' - ' || s_exp_pds_brut || is_msgz || is_crlf;
    end if;

    if ld_pal_nb_col >= 100 then ld_pal_nb_col := 0; end if;

    if msg = '' then
        if ld_pal_nb_col > 0 then
            update geo_ordlig set
            exp_nb_pal = ld_exp_nb_pal,
            exp_nb_col = ld_exp_nb_col,
            exp_pal_nb_col = ld_pal_nb_col,
            exp_pds_brut = ld_exp_pds_brut,
            exp_pds_net = ld_exp_pds_net,
            vte_qte = ld_vte_qte,
            ach_qte = ld_ach_qte,
            cq_ref = s_cq_ref
            where orl_ref = s_orl_ref;
        else
            update geo_ordlig set
            exp_nb_pal = ld_exp_nb_pal,
            exp_nb_col = ld_exp_nb_col,
            exp_pds_brut = ld_exp_pds_brut,
            exp_pds_net = ld_exp_pds_net,
            vte_qte = ld_vte_qte,
            ach_qte = ld_ach_qte,
            cq_ref = s_cq_ref
            where orl_ref = s_orl_ref;
        end if;
        
        d_tot_exp_nb_pal	:= d_tot_exp_nb_pal + ld_exp_nb_pal;
        d_tot_exp_nb_col	:= d_tot_exp_nb_col + ld_exp_nb_col;
        d_tot_exp_pds_brut	:= d_tot_exp_pds_brut + ld_exp_pds_brut;
        d_tot_exp_pds_net	:= d_tot_exp_pds_net + ld_exp_pds_net;

    end if;

    return;
exception when others then
    msg := msg || is_msg || 'actualisation de la ligne ' || s_orl_ref || ' : erreur ORA' || SQLCode || ' ' || SQLERRM || is_msgz || is_crlf;
    return;
END;
/
