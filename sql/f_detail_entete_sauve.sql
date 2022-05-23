-- f_detail_entete_sauve

CREATE OR REPLACE PROCEDURE F_DETAIL_ENTETE_SAUVE (
    arg_ord_ref IN geo_ordre.ORD_REF%TYPE,
    arg_mode IN varchar2,
    arg_check_palette IN varchar2,
    res IN OUT number,
    arg_msg IN OUT varchar2
)
AS
    is_msg varchar2(50):= '<msg>';
    is_msgz varchar2(50) := '</msg>';

    ld_pal_nb_sol number;
    ld_pal_nb number;
    ld_pal_nb_pb100x120 number;
    ld_pal_nb_pb80x120 number;
    ld_pal_nb_pb60x80 number;
    ld_pal_nb_pr100x120 number;
    ld_pal_nb_pr80x120 number;
    ld_pal_nb_pr60x80 number;
    ll_rc number;

    s_pal_nb varchar2(50);
    s_pal_nb_sol varchar2(50);
    s_pal_nb_pb100x120 varchar2(50);
    s_pal_nb_pb80x120 varchar2(50);
    s_pal_nb_pb60x80 varchar2(50);
    s_pal_nb_pr100x120 varchar2(50);
    s_pal_nb_pr80x120 varchar2(50);
    s_pal_nb_pr60x80 varchar2(50);

    ls_gest_ref varchar2(50);
    is_crlf varchar2(50);
    ls_typ_ordre varchar2(50);

    l_fou_code varchar2(50);
    l_imm_num varchar2(50);
    l_container varchar2(50);
    l_plomb varchar2(50);
    l_detecteur_temp varchar2(50);
    l_certificat_controle varchar2(50);
    l_certificat_phyto varchar2(50);
    l_numero_detecteur varchar2(50);
    l_fou_ref_doc varchar2(50);
    l_ref_log varchar2(50);
    l_ref_doc varchar2(50);

    ld_datdep_fou_p timestamp;

    ls_soc_code varchar2(50);
    -- On vérifie pour BWS qu'il n'y ai pas au moins une ligne avec du CHEP
    ll_nb_pal_chep number;
    ll_check_chep number := 1;

    ldt_wrk timestamp;
BEGIN
    -- msg := '';
    res := 0;

    SELECT
        fou_code,
        immatriculation,
        container,
        plomb,
        detecteur_temp,
        certif_controle,
        certif_phyto,
        locus_trace,
        fou_ref_doc,
        orx_ref,
        fou_ref_doc
    INTO
        l_fou_code,
        l_imm_num,
        l_container,
        l_plomb,
        l_detecteur_temp,
        l_certificat_controle,
        l_certificat_phyto,
        l_numero_detecteur,
        l_fou_ref_doc,
        l_ref_log,
        l_ref_doc
    from geo_ordlog
    where ord_ref = arg_ord_ref;

     -- Nonbre total de palettes
    select count(SSCC) into ld_pal_nb from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code AND VALIDE = 'O' AND PAL_CODE <> 'DEPAL';

    -- Nonbre total de palettes sol
    select count(SSCC) into ld_pal_nb_sol from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_SOL = '1' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 100x120
    select count(SSCC) into ld_pal_nb_pb100x120 from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_CODE = 'PB' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 80x120
    select count(SSCC) into ld_pal_nb_pb80x120 from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_CODE = 'PB812' AND VALIDE = 'O';

    -- Nonbre total de palettes Bleues 60x80
    select count(SSCC) into ld_pal_nb_pb60x80 from geo_traca_detail_pal where ord_ref = arg_ord_ref AND fou_code = l_fou_code and PAL_CODE = 'PB68' AND VALIDE = 'O';

    s_pal_nb_sol				:= to_char(ld_pal_nb_sol);
    s_pal_nb_pb100x120	:= to_char(ld_pal_nb_pb100x120);
    s_pal_nb_pb80x120		:= to_char(ld_pal_nb_pb80x120);
    s_pal_nb_pb60x80		:= to_char(ld_pal_nb_pb60x80);
    s_pal_nb					:= to_char(ld_pal_nb);

    if s_pal_nb_sol = '' then s_pal_nb_sol := '0'; end if;
    if s_pal_nb_pb100x120 = '' then s_pal_nb_pb100x120 := '0'; end if;
    if s_pal_nb_pb80x120 = '' then s_pal_nb_pb80x120 := '0'; end if;
    if s_pal_nb_pb60x80 = '' then s_pal_nb_pb60x80 := '0'; end if;
    if s_pal_nb_pr100x120 = '' then s_pal_nb_pr100x120 := '0'; end if;
    if s_pal_nb_pr80x120 = '' then s_pal_nb_pr80x120 := '0'; end if;
    if s_pal_nb_pr60x80 = '' then s_pal_nb_pr60x80 := '0'; end if;
    
    --
    begin
        ld_pal_nb_sol	:= to_number(s_pal_nb_sol);
        ld_pal_nb		:= to_number(s_pal_nb);
        
        if ld_pal_nb > 0 and ld_pal_nb_sol < 1 and arg_check_palette = 'O' then
            -- ll_rc	:= f_xml_input_status(arg_ds_status, 'D', false);
            res := 0;
            arg_msg := arg_msg || is_msg || 'nbre de palettes au sol OBLIGATOIRE !' || is_msgz || is_crlf;
        -- else
        --     ll_rc	:= f_xml_input_status(arg_ds_status, 'D', true);
        end if;
    exception when others then
        ld_pal_nb_sol	:= 0;
        -- ll_rc	:= f_xml_input_status(arg_ds_status, 'D', false);
        res := 0;
        arg_msg := arg_msg  || is_msg || ' ' || 'pb sur nbre palettes au sol : ' || s_pal_nb_sol || is_msgz || is_crlf;
    end;

    select geo_entrep.gest_ref, geo_ordre.SOC_CODE,geo_ordre.typ_ordre
    into ls_gest_ref, ls_soc_code ,ls_typ_ordre
    from geo_entrep, geo_ordre
    where geo_ordre.ord_ref = arg_ord_ref and geo_entrep.cen_ref = geo_ordre.cen_ref;

    if ls_soc_code = 'BWS' then
        select count(*) 
        into ll_nb_pal_chep
        from geo_ordlig OL, geo_ordre O, geo_palett P
        where
            O.ord_ref = arg_ord_ref and
            OL.ORD_REF = O.ORD_REF AND
            OL.FOU_CODE = l_fou_code AND
            OL.PAL_CODE = P.PAL_CODE AND
            P.GEST_CODE = 'CHEP';
            
        if ll_nb_pal_chep = 0 then
            -- Dans ce cas on vérifie pas
            ll_check_chep := 0;
        end if;
    end if;

    IF ls_typ_ordre ='ORI' Then

        SELECT COUNT(PAL_CODE) into ld_pal_nb_pb80x120 FROM GEO_TRACA_DETAIL_PAL P WHERE  P.ORD_REF = arg_ord_ref  AND P.FOU_CODE = l_fou_code AND P.PAL_CODE = 'PB812' and P.VALIDE = 'O';	
        SELECT COUNT(PAL_CODE) into ld_pal_nb_pb60x80 FROM GEO_TRACA_DETAIL_PAL P WHERE  P.ORD_REF = arg_ord_ref  AND P.FOU_CODE = l_fou_code AND P.PAL_CODE = 'PB68' and P.VALIDE = 'O';	
        SELECT COUNT(PAL_CODE) into ld_pal_nb_pb100x120 FROM GEO_TRACA_DETAIL_PAL P WHERE  P.ORD_REF = arg_ord_ref  AND P.FOU_CODE = l_fou_code AND P.PAL_CODE = 'PB' and P.VALIDE = 'O';	
        
    End IF;

    begin
        ls_gest_ref	:= trim(ls_gest_ref);
        if ls_gest_ref is not null and arg_check_palette = 'O' and ll_check_chep = 1 then

            begin
                ld_pal_nb_pb100x120 := to_number(s_pal_nb_pb100x120);
            exception when others then
                ld_pal_nb_pb100x120 := 0;
                res := 0;
                arg_msg := arg_msg  || is_msg || ' ' || 'pb sur nbre palettes bleues 100x120 : ' || s_pal_nb_pb100x120 || is_msgz || is_crlf;
            end;

            begin
                ld_pal_nb_pr100x120 := to_number(s_pal_nb_pr100x120);
            exception when others then
                ld_pal_nb_pr100x120 := 0;
            end;
            
            If ld_pal_nb_pr100x120	= 0 Then
                SELECT COUNT(PAL_CODE) into ld_pal_nb_pr100x120 FROM GEO_TRACA_DETAIL_PAL P WHERE  P.ORD_REF = arg_ord_ref  AND P.FOU_CODE = l_fou_code AND P.PAL_CODE in ('PR1012','PR1012NI') and P.VALIDE = 'O';
                -- If  ld_pal_nb_pr100x120	>  0 Then ll_rc	= f_xml_input_status(arg_ds_status, 'E', true)
            End if;
                
            begin
                s_pal_nb_pb80x120 := to_number(s_pal_nb_pb80x120);
            exception when others then
                s_pal_nb_pb80x120 := 0;
                res := 0;
                arg_msg := arg_msg  || is_msg || ' ' || 'pb sur nbre palettes bleues 80x120 : ' || s_pal_nb_pb80x120 || is_msgz || is_crlf;
            end;
            
            begin
                s_pal_nb_pr80x120 := to_number(s_pal_nb_pr80x120);
            exception when others then
                s_pal_nb_pr80x120 := 0;
            end;
            
            If ld_pal_nb_pr80x120	= 0 Then
                SELECT COUNT(PAL_CODE) into ld_pal_nb_pr80x120 FROM GEO_TRACA_DETAIL_PAL P WHERE  P.ORD_REF = arg_ord_ref  AND P.FOU_CODE = l_fou_code AND P.PAL_CODE in('PR812','PR812NI') and P.VALIDE = 'O';
                -- If  ld_pal_nb_pr80x120	>  0 Then ll_rc	= f_xml_input_status(arg_ds_status, 'F', true)
            End if;		
            
            begin
                s_pal_nb_pb60x80 := to_number(s_pal_nb_pb60x80);
            exception when others then
                s_pal_nb_pb60x80 := 0;
                res := 0;
                arg_msg := arg_msg  || is_msg || ' ' || 'pb sur nbre palettes bleues 60x80 : ' || s_pal_nb_pb60x80 || is_msgz || is_crlf;
            end;
            
            begin
                s_pal_nb_pr60x80 := to_number(s_pal_nb_pr60x80);
            exception when others then
                s_pal_nb_pr60x80 := 0;
            end;
            
            If ld_pal_nb_pr60x80	= 0 Then
                SELECT COUNT(PAL_CODE) into ld_pal_nb_pr60x80 FROM GEO_TRACA_DETAIL_PAL P WHERE  P.ORD_REF = arg_ord_ref  AND P.FOU_CODE = l_fou_code AND P.PAL_CODE in('PR68','PR68NI') and P.VALIDE = 'O';
                -- If  ld_pal_nb_pr60x80	>  0 Then ll_rc	= f_xml_input_status(arg_ds_status, 'G', true)
            End if;		
            
            
            if ld_pal_nb_pb100x120 = 0 and ld_pal_nb_pb80x120 = 0 and ld_pal_nb_pb60x80 = 0 and ld_pal_nb_pr100x120 = 0 and ld_pal_nb_pr80x120 = 0 and ld_pal_nb_pr60x80 = 0   then
                arg_msg := arg_msg  || is_msg || ' ' || 'vous devez saisir la quantité de palettes bleues ou rouges expédiées par type de palettes (y compris les palettes intermédiaires)' || is_msgz || is_crlf;
            end if;
            
            
            if ls_soc_code <> 'BWS' AND ls_typ_ordre <> 'RGP' AND (ld_pal_nb_pb100x120  || ld_pal_nb_pb80x120 || ld_pal_nb_pb60x80 || ld_pal_nb_pr100x120 || ld_pal_nb_pr80x120 ||  ld_pal_nb_pr60x80  < ld_pal_nb_sol) then
                arg_msg := arg_msg  || is_msg || ' ' || 'quantité de palettes bleues ou rouges incohérente par rapport au nombre de palettes au sol' || is_msgz || is_crlf;
            end if;
        end if;
    exception when others then
        null;
        -- when ls_gest_ref is null do continue
    end;

    
    if arg_mode = 'B_CLOTURER'  then
            -- on actualise aussi datdep_fou_r et le flag
            -- si erreur au cours du traitement, on  fera un rollback (voir plus bas)
        ldt_wrk	:= current_timestamp;
            -- la reference interne fournissur ne doit pas ête vide (elle sert de flag obsolescent pour expedié oui/non)
            -- la bonne méthode consistera à utiliser flag_exped_fournni 
        if l_fou_ref_doc = '' then l_fou_ref_doc := l_fou_code; end if;
        
        update geo_ordlog set 
            datdep_fou_r =  sysdate,
            flag_exped_fournni = 'O',
            fou_ref_doc = l_fou_ref_doc,
            ref_logistique = l_ref_log,
            ref_document = l_ref_doc,
            pal_nb_sol = ld_pal_nb_sol,
            pal_nb_pb100x120 = ld_pal_nb_pb100x120,
            pal_nb_pb80x120 = ld_pal_nb_pb80x120,
            pal_nb_pb60x80 = ld_pal_nb_pb60x80,
            plomb = l_plomb,
            immatriculation = l_imm_num,
            detecteur_temp = l_detecteur_temp,
            certif_controle = l_certificat_controle,
            certif_phyto = l_certificat_phyto,
            locus_trace = l_numero_detecteur,
            container = l_container
        where ord_ref = arg_ord_ref AND fou_code = l_fou_code;
        
        --Deb LLEF
        declare
            tmp_msg varchar2(200) := '';
        begin
            f_cloture_log_grp(arg_ord_ref, l_fou_code, 'O', res, tmp_msg);
            arg_msg := arg_msg || tmp_msg;
        end;
        --Fin LLEF
    else
            -- on actualise sans clotûrer (c'est B_SAUVER implicitement)
        update geo_ordlog set 
            fou_ref_doc = l_fou_ref_doc,
            ref_logistique = l_ref_log,
            ref_document = l_ref_doc,
            pal_nb_sol = ld_pal_nb_sol,
            pal_nb_pb100x120 = ld_pal_nb_pb100x120,
            pal_nb_pb80x120 = ld_pal_nb_pb80x120,
            pal_nb_pb60x80 = ld_pal_nb_pb60x80,
            plomb = l_plomb,
            immatriculation = l_imm_num,
            detecteur_temp = l_detecteur_temp,
            certif_controle = l_certificat_controle,
            certif_phyto = l_certificat_phyto,
            locus_trace = l_numero_detecteur,
            container = l_container
        where ord_ref = arg_ord_ref and fou_code = l_fou_code;
        
    end if;

    res := 1;
exception when others then
        arg_msg := arg_msg || is_msg || 'actualisation en-tête logistique ' || arg_ord_ref || l_fou_code || ' : erreur ORA' || SQLCODE || ' ' || SQLERRM || is_msgz || is_crlf;
END;
/
