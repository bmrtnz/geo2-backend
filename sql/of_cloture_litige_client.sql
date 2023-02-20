CREATE OR REPLACE PROCEDURE GEO_ADMIN.OF_CLOTURE_LITIGE_CLIENT (
    is_cur_lit_ref in GEO_LITLIG.LIT_REF%TYPE,
    arg_soc_code in GEO_SOCIETE.SOC_CODE%TYPE,
    -- User prompts
    -- Empty value is for blocking procedure with a message
    -- Non null value ('O'/'N') continue the procedure as evaluated
    prompt_frais_annexe in varchar2 := '',
    prompt_avoir_client in varchar2 := '',
    prompt_create_avoir_client in varchar2 := '',
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

    ldt_x timestamp;

    ls_nordre_replace varchar2(50);
    ls_old_ord_ref varchar2(50);
    ls_cur_nordre varchar2(50);
    ls_cur_ord_ref varchar2(50);
    ls_rc varchar2(500);
    ls_flbaf varchar2(1);
BEGIN
    res := 0;
    msg := '';

    select ord_ref_origine
    into is_cur_ord_ref
    from geo_litige
    where lit_ref = is_cur_lit_ref;

    of_sauve_litige(is_cur_lit_ref,res,msg);
    if res <> 1 then return; end if;

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

    -- on vérifie que l'avoir n'est pas déja généré
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

    declare
        rowcount number;
    begin
        select count(*)into rowcount from geo_litige where lit_ref = is_cur_lit_ref;
        select lit_frais_annexes into ldc_frais_annexe from geo_litige where lit_ref = is_cur_lit_ref;
        If	rowcount > 0 Then
            If ldc_frais_annexe = 0 or ldc_frais_annexe is null Then
                if prompt_frais_annexe is null or prompt_frais_annexe = '' then
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
        select count(0) into ll_count
        from geo_litlig
        where lit_ref = is_cur_lit_ref
        and cli_qte <> 0;

        if ll_count = 0 then
            if prompt_avoir_client is null or prompt_avoir_client = '' then
                msg := 'clotûre client: aucun avoir client à créer, êtes-vous vraiment sûr(e) ?';
                res := 2;
                triggered_prompt := 'promptAvoirClient';
                return;
            elsif prompt_avoir_client = 'O' then
                update geo_litige set
                fl_client_clos = 'O',
                fl_client_admin = 'O',
                lit_date_avoir_client = sysdate
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



    if prompt_create_avoir_client is null or prompt_create_avoir_client = '' then
        msg := 'création de l''avoir client du litige, si vous acceptez, l''avoir client sera créé, vous ne pourrez plus modifier le litige, êtes-vous vraiment sûr(e) ?';
        res := 2;
        triggered_prompt := 'promptCreateAvoirClient';
        return;
    elsif prompt_create_avoir_client = 'N' then
        res := 1;
        return;
    end if;

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
        f_cree_avoir_client(ls_old_ord_ref, ls_cur_ord_ref, is_cur_lit_ref, ls_cur_nordre, arg_soc_code, res, ls_rc);
        if ls_rc <> 'OK' then
            msg := 'création de l''avoir client ' || ls_rc;
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
        fl_client_clos = 'O',
        fl_client_admin = 'O',
        lit_date_avoir_client = sysdate
    where lit_ref = is_cur_lit_ref;

    msg := 'clotûre du litige client ' || is_cur_lit_ref || 'l''avoir client n° ' || ls_cur_nordre || ' a été créé';

    of_sauve_litige(is_cur_lit_ref,res,msg);
    if res <> 1 then return; end if;
    -- of_eval_litige()
    -- of_litige_ctl_client_update()

    res := 1;
END;
/

