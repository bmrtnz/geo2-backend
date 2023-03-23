CREATE OR REPLACE PROCEDURE OF_CLOTURE_LITIGE_GLOBALE (
    is_cur_lit_ref in GEO_LITLIG.LIT_REF%TYPE,
    arg_soc_code in GEO_SOCIETE.SOC_CODE%TYPE,
    arg_username IN GEO_USER.nom_utilisateur%type,
    -- User prompts
    -- Empty value is for blocking procedure with a message
    -- Non null value ('O'/'N') continue the procedure as evaluated
    prompt_frais_annexe in varchar2 := '',
    prompt_avoir_client in varchar2 := '',
    prompt_avoir_global in varchar2 := '',
    prompt_create_avoir_global in varchar2 := '',
    res out number,
    msg out clob,
    triggered_prompt out varchar2
)
AS
    is_cur_ord_ref GEO_ORDRE.ORD_REF%TYPE;
    ll_rc number;
    ll_count number;
    li_row number;
    ldc_frais_annexe number;
    ll_nordre number;
    ll_ord_ref number;
    ll_client_count number;
    ll_fourni_count number;

    ldt_x timestamp;

    ls_nordre_replace varchar2(50);
    ls_old_ord_ref varchar2(50);
    ls_cur_nordre varchar2(50);
    ls_cur_ord_ref varchar2(50);
    ls_rc varchar2(50);
    ls_fl_client_clos varchar2(50);
    ls_fl_fourni_clos varchar2(50);
    ls_flbaf varchar2(1);

    -- s_ordflu par_out
    li_num_version number;
    li_null number;
BEGIN
    res := 0;
    msg := '';

    of_sauve_litige(is_cur_lit_ref,res,msg);
    if res <> 1 then return; end if;

    select ord_ref_origine
    into is_cur_ord_ref
    from geo_litige
    where lit_ref = is_cur_lit_ref;

    declare
        rowcount number;
    begin
        select count(*) into rowcount
        from geo_ordlig
        where ord_ref = is_cur_ord_ref;
        If	rowcount = 0   Then
            res := 1;
            return;
        end if;
    end;

    begin
        select flbaf
        into ls_flbaf
        from geo_litige, geo_ordre
        where geo_litige.ord_ref_origine = geo_ordre.ord_ref
        and lit_ref = is_cur_lit_ref;
        If	ls_flbaf <> 'O'  Then
            msg	:= 'Avertissement: Ordre non facturé pas de cloture possible';
            res := 0;
            return;
        End If;
    end;

    li_null := null;

    declare
        cursor lit_ligs is
            select lcq_code, nordre_replace
            from geo_litlig
            where lit_ref = is_cur_lit_ref;
    begin
        for r in lit_ligs loop
            If r.lcq_code ='B' then
                ls_nordre_replace := r.nordre_replace;
                If ls_nordre_replace is null or ls_nordre_replace ='' then
                    msg := 'Saisie obligatoire: Veuillez saisir un ordre de replacement';
                    res := 0;
                    return;
                End If;
            End If;
        end loop;
    end;

    declare
        ll_count number;
    begin
        select num_version, lit_frais_annexes into li_num_version, ldc_frais_annexe
        from geo_litige where lit_ref = is_cur_lit_ref;
        select count(*) into ll_count from geo_litlig where lit_ref = is_cur_lit_ref;
        If	ll_count > 0  Then
            If ldc_frais_annexe = 0 or ldc_frais_annexe is null Then
                if prompt_frais_annexe = '' then
                    msg := 'Avertissement: aucun frais annexe sur le litige, êtes-vous vraiment sûr(e) ?';
                    res := 2;
                    triggered_prompt := 'promptFraisAnnexe';
                    return;
                elsif prompt_frais_annexe <> 'O' then
                    res := 1;
                    return;
                end if;
            End If;
        End If;
    end;

    declare
        ls_fl_client_clos varchar2(50);
        ls_fl_fourni_clos varchar2(50);
    begin
        select fl_client_clos, fl_fourni_clos into ls_fl_client_clos, ls_fl_fourni_clos
        from geo_litige where lit_ref = is_cur_lit_ref;
        if ls_fl_client_clos = 'N' then
            select count(0) into ll_client_count from geo_litlig where lit_ref = is_cur_lit_ref and cli_qte <> 0;
            if ll_client_count = 0 then
                if prompt_avoir_client is null or prompt_avoir_client = '' then
                    msg := 'clotûre globale: la partie client ne générera aucun avoir, êtes-vous vraiment sûr(e) ?';
                    res := 2;
                    triggered_prompt := 'promptAvoirClient';
                    return;
                elsif prompt_avoir_client <> 'O' then
                    res := 1;
                    return;
                end if;
            end if;
        else
            ll_client_count	:= ll_client_count + 1;
        end if;
    end;

    -- on vérifie que l'avoir client n'est pas déja généré
    declare
        ls_ord_ref_avoir varchar2(50);
    begin
        select ord_ref_avoir
        into ls_ord_ref_avoir
        from geo_litige, geo_ordre
        where geo_litige.ord_ref_origine = geo_ordre.ord_ref
        and lit_ref = is_cur_lit_ref;
        if ls_ord_ref_avoir is not null or ls_ord_ref_avoir <> '' then
            msg := 'avoir client: l''avoir a déja été généré';
            res := 0;
            return;
        end if;
    end;

    -- on vérifie que l'avoir fournisseur n'est pas déja généré
    declare
        ls_ord_ref_avoir varchar2(50);
    begin
        select ord_ref_avoir_fourni
        into ls_ord_ref_avoir
        from geo_litige, geo_ordre
        where geo_litige.ord_ref_origine = geo_ordre.ord_ref
        and lit_ref = is_cur_lit_ref;
        if ls_ord_ref_avoir is not null or ls_ord_ref_avoir <> '' then
            msg := 'avoir fournisseur: l''avoir a déja été généré';
            res := 0;
            return;
        end if;
    end;

    -- on verifie qu'il y a des lignes d'avoir à générer
    -- soit il y a des lignes clients significatives (qte <> 0)
    -- soit il y a des lignes fournisseurs idem
    -- soit les 2 bien sûr
    declare
        ll_count number;
    begin
        select count(0) into ll_count from geo_litlig
        where lit_ref = is_cur_lit_ref and (cli_qte <> 0 or ((tyt_code = 'F' or  lcq_code = 'A' or  lcq_code = 'B' or lcq_code ='F') and res_qte <> 0));

        if ll_count = 0 then -- il n'y a aucune ligne éligible
            if prompt_avoir_global is null or prompt_avoir_global = '' then
                msg := 'clotûre globale: aucun avoir client / fournisseur à créer, êtes-vous vraiment sûr(e) ?';
                res := 2;
                triggered_prompt := 'promptAvoirGlobal';
                return;
            elsif prompt_avoir_global = 'O' then
                update geo_litige set
                fl_client_clos = 'O',
                fl_client_admin = 'O',
                fl_fourni_clos = 'O',
                fl_fourni_admin = 'O',
                lit_date_avoir_client = sysdate,
                lit_date_avoir_fourni = sysdate
                where lit_ref = is_cur_lit_ref;
                commit;
                of_sauve_litige(is_cur_lit_ref,res,msg);
				if res <> 1 then return; end if;
                -- of_eval_litige()
                -- of_litige_ctl_client_update()
            End if;

            res := 1;
            return;
        end if;
    end;



    if prompt_create_avoir_global is null or prompt_create_avoir_global = '' then
        msg := 'création de l''avoir global du litige, si vous acceptez, l''avoir global sera créé, vous ne pourrez plus modifier le litige, êtes-vous vraiment sûr(e) ?';
        res := 2;
        triggered_prompt := 'promptCreateAvoirGlobal';
        return;
    elsif prompt_create_avoir_global = 'N' then
        res := 1;
        return;
    end if;
    	-- c'est parti
    ls_old_ord_ref	:= is_cur_ord_ref;
        -- on utilise une séquence spéciale pour les avoirs
    select seq_avo_num.nextval into ll_nordre from dual;
    ls_cur_nordre	:= to_char(ll_nordre,'FM099999');
    --BAM separés ORD_REF, NORDRE
    select seq_ord_num.nextval into ll_ord_ref from dual;
    ls_cur_ord_ref	:= to_char(ll_ord_ref);
        -- fonction de création de l'avoir (ordre avec facture_avoir = A)
    declare
        ls_rc varchar2(500);
    begin
        f_cree_avoir_tous_v2(ls_old_ord_ref, ls_cur_ord_ref, is_cur_lit_ref, ls_cur_nordre, arg_soc_code, arg_username, res, ls_rc);
        if ls_rc <> 'OK' then
            msg := 'création de l''avoir global ' || ls_rc;
            return;
        end if;
    end;

    declare
        ls_rc varchar2(500);
    begin
        f_calcul_marge(ls_cur_ord_ref, res, ls_rc); -- actualise avoir : marge
        if res <> 1 then
            msg := ls_rc;
            return;
        end if;
    end;

    update geo_ordre set flbaf = 'O' where ord_ref = ls_cur_ord_ref;	-- actualise avoir : bon à facturer
    commit;

    fn_gen_tesco_factu(ls_cur_ord_ref, res, msg);
    if res <> 1 then return; end if;

    update geo_litige set
        ord_ref_avoir = ls_cur_ord_ref,
        ord_ref_avoir_fourni = ls_cur_ord_ref,
        fl_client_clos = 'O',
        fl_client_admin = 'O',
        fl_fourni_clos = 'O',
        fl_fourni_admin = 'O',
        lit_date_avoir_client = sysdate,
        lit_date_avoir_fourni = sysdate
    where lit_ref = is_cur_lit_ref;

    msg := 'clotûre du litige ' || is_cur_lit_ref || 'l''avoir global n° ' || ls_cur_nordre || ' a été créé';

    of_sauve_litige(is_cur_lit_ref,res,msg);
    if res <> 1 then return; end if;
    -- of_eval_litige()
    -- of_litige_ctl_client_update()

    -- Inutile dans le contexte de GEO2 ?
    -- If  gs_soc_code <> 'UDC'   then
    --     par_out.ord_ref	=  is_cur_ord_ref
    --     par_out.flu_code	= 'RESLIT'
    --     par_out.mode_auto = true
    --     OpenWithParm(w_geo_genere_envois, par_out)
    -- END IF

    res := 1;
END;
/

