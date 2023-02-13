CREATE OR REPLACE PROCEDURE OF_GENERE_ENVOIS_LITIGE_AUTOM(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
	mode_auto IN char,
    arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ll_ind number;
    ll_rc number;
    ll_count number;
    ll_ind2 number;
    li_row number;
    li_num_version number;
    ls_ref_logistique varchar2(50);
    ls_ref_document varchar2(50);
    ls_ref_logistique_bw varchar2(50);
    ls_ref_document_bw varchar2(50);
    ls_fou_code varchar2(50);
    ls_fou_ref_doc varchar2(50);
    ls_instructions varchar2(50);
    ls_grp_code varchar2(50);
    ls_trp_code varchar2(50);
    ls_chg_code varchar2(50);
    ls_ar_fou_code varchar2(50);
    ls_contact varchar2(50);
    ls_debug varchar2(50);
    ls_ar_desc varchar2(50);
    ls_ar_fou_code_en_trop varchar2(50);	-- ceux qui ne devraient pas recevoir d'annule-et-remplace
    ls_commentaire varchar2(50);
    ls_comment varchar2(50);
    lsa_ar_fou_code p_str_tab_type := p_str_tab_type();
    lsa_ar_desc p_str_tab_type := p_str_tab_type();
    ldt_datdep_fou_p timestamp;
    ldt_datdep_fou_r timestamp;
    ldt_datdep_grp_p timestamp;
    ldt_datdep_grp_r timestamp;
    ldt_datdep_chg_p timestamp;
    ldt_datdep_chg_r timestamp;
    ldt_ordre_mod_date timestamp;
    ldt_ordlig_mod_date timestamp;
    ldt_envois_mod_date timestamp;

    is_soc_code varchar2(50);
    is_cam_code varchar2(50);
    is_nordre varchar2(50);
    is_per_codeass varchar2(50);
    is_per_codecom varchar2(50);
    is_cli_ref varchar2(50);
    is_cli_code varchar2(50);
    is_cen_ref varchar2(50);
    is_cen_code varchar2(50);
    is_sco_code varchar2(50);
    is_trp_code varchar2(50);
    is_trs_code varchar2(50);
    is_crt_code varchar2(50);
    is_dev_code varchar2(50);
    id_dev_tx number;
    is_tvr_code varchar2(50);
    is_inc_code varchar2(50);
    is_inc_desc varchar2(50);
    is_inc_lieu varchar2(50);
    is_instructions_logistique varchar2(50);
    is_trs_ville varchar2(50);
    is_crt_bta_code varchar2(50);
    is_ref_cli varchar2(50);
    is_per_codeass_prenom varchar2(50);
    is_per_codecom_prenom varchar2(50);
    idt_depdatp varchar2(50);
    idt_datlivp varchar2(50);
    id_remsf_tx number;
    id_remhf_tx number;
    id_crt_tx number;
    is_ttr_code varchar2(50);
    is_ttr_desc varchar2(50);
    is_con_tyt varchar2(50);
    is_con_tiers varchar2(50);
    is_con_ref varchar2(50);
    is_moc_code varchar2(50);
    is_con_acces1 varchar2(50);
    is_con_fluvar varchar2(50);
    is_con_prenom varchar2(50);
    is_con_nom varchar2(50);

    is_ref_logistique varchar2(50);
    is_ref_document varchar2(50);
    is_imp_full varchar2(50);
    is_imprimante varchar2(35);
    ls_env_code geo_envois.env_code%TYPE;
BEGIN
    res := 0;
    msg := '';

    begin
        -- on récupère les différents intervenants de l'en-tête
        select A.soc_code, A.cam_code, A.nordre, A.per_codeass, A.per_codecom, A.cli_ref, B.cli_code, A.cen_ref, C.cen_code, A.sco_code,
                A.trp_code, A.trs_code, A.crt_code, A.dev_code, A.dev_tx, A.tvr_code, A.inc_code, D.inc_desc, A.inc_lieu,
                A.instructions_logistique,	A.trs_ville, A.crt_bta_code, A.ref_cli, E.per_prenom,	 F.per_prenom,
                A.depdatp, A.livdatp, A.remsf_tx, A.remhf_tx, A.crt_pu, A.ttr_code, G.ttr_desc, A.ref_logistique, A. ref_document
        into is_soc_code, is_cam_code, is_nordre, is_per_codeass, is_per_codecom, is_cli_ref, is_cli_code, is_cen_ref, is_cen_code, is_sco_code,
                is_trp_code, is_trs_code, is_crt_code, is_dev_code, id_dev_tx, is_tvr_code, is_inc_code, is_inc_desc, is_inc_lieu,
                is_instructions_logistique, is_trs_ville,is_crt_bta_code, is_ref_cli, is_per_codeass_prenom, is_per_codecom_prenom,
                idt_depdatp, idt_datlivp, id_remsf_tx, id_remhf_tx, id_crt_tx, is_ttr_code, is_ttr_desc, ls_ref_logistique_bw, ls_ref_document_bw
        from geo_ordre A, geo_client B, geo_entrep C, geo_incote D, geo_person E, geo_person F, geo_typtrp G
        where A.ord_ref = is_ord_ref and A.cli_ref = B.cli_ref (+) and A.cen_ref = C.cen_ref (+) and A.inc_code = D.inc_code (+)
            and A.per_codeass = E.per_code (+) and A.per_codecom = F.per_code (+)  and G.ttr_code (+) = A.ttr_code;

    exception when others then
        If mode_auto = 'N' then
            msg := msg || ' select geo_ordre ordre ' || is_ord_ref || ' inconnu - erreur ' || SQLERRM;
        end if;
        res := 2;
        return;
    end;

    -- This.Title	= 'documents proposés pour l'ordre ' + is_nordre + ' flux ' + is_flu_code
    if is_soc_code is null then is_soc_code := ''; end if;
    if is_cam_code is null then is_cam_code := ''; end if;
    if is_nordre is null then is_nordre := ''; end if;
    if is_per_codeass is null then is_per_codeass := ''; end if;
    if is_per_codecom is null then is_per_codecom := ''; end if;
    if is_cli_ref is null then is_cli_ref := ''; end if;
    if is_cli_code is null then is_cli_code := ''; end if;
    if is_cen_ref is null then is_cen_ref := ''; end if;
    if is_cen_code is null then is_cen_code := ''; end if;
    if is_sco_code is null then is_sco_code := ''; end if;
    if is_trp_code is null then is_trp_code := ''; end if;
    if is_trs_code is null then is_trs_code := ''; end if;
    if is_crt_code is null then is_crt_code := ''; end if;
    if is_dev_code is null then is_dev_code := ''; end if;
    if id_dev_tx is null then id_dev_tx := 0; end if;
    if is_tvr_code is null then is_tvr_code := ''; end if;
    if is_inc_code is null then is_inc_code := ''; end if;
    if is_inc_desc is null then is_inc_desc := ''; end if;
    if is_inc_lieu is null then is_inc_lieu := ''; end if;
    if is_instructions_logistique is null then is_instructions_logistique := ''; end if;
    if is_trs_ville is null then is_trs_ville := ''; end if;
    if is_crt_bta_code is null then is_crt_bta_code := ''; end if;
    if is_ref_cli is null then is_ref_cli := ''; end if;
    if is_per_codeass_prenom is null then is_per_codeass_prenom := ''; end if;
    if is_per_codecom_prenom is null then is_per_codecom_prenom := ''; end if;
    if id_remsf_tx is null then id_remsf_tx := 0; end if;
    if id_remhf_tx is null then id_remhf_tx := 0; end if;
    if id_crt_tx is null then id_crt_tx := 0; end if;
    if is_ttr_code is null then is_ttr_code := ''; end if;
    if is_ttr_desc is null then is_ttr_desc := ''; end if;

    -- is_ref_doc et log sont cosntitués des ref de geo_ordre et de geo_ordlog mis bout à bout
    if ls_ref_logistique_bw is not null  then is_ref_logistique := ls_ref_logistique_bw || ' '; end if;
    if ls_ref_document_bw is not null  then is_ref_document := ls_ref_document_bw || ' '; end if;

    -- on récupère l'imprimante de l'assistant(e)
    -- is_imprimante	= gs_user.imp_ref
    begin
        select gu.imp_ref into is_imprimante
        from geo_user gu
        where gu.nom_utilisateur = arg_nom_utilisateur;
        select imp_id into is_imp_full from geo_imprim where imp_ref = is_imprimante;
    exception when others then
        is_imp_full	:= '';
    end;

    -- On liste les tiers impliqués
    declare
        ls_TIE_CODE varchar2(50);
        ls_LIT_REF varchar2(50);
        ls_LIT_REF_sav varchar2(50);
        ls_ORL_REF varchar2(50);
    begin
        select NUM_VERSION
        into li_num_version
        from GEO_LITIGE
        where ORD_REF_ORIGINE = is_ord_ref;

        case li_num_version
            when 2 then
            declare
                cursor CO is
                    SELECT DISTINCT L.TIE_CODE, LI.LIT_REF
                    FROM GEO_LITLIG L, GEO_LITIGE LI, GEO_ORDRE O
                    WHERE  L.LIT_REF = LI.LIT_REF AND
                                LI.ORD_REF_ORIGINE = O.ORD_REF AND
                                O.ord_ref = is_ord_ref and
                                is_flu_code in ('INCLIT','RESLIT') and
                                L.TYT_CODE = 'F'
                    union
                    SELECT DISTINCT OL.PROPR_CODE, LI.LIT_REF
                    FROM GEO_LITLIG L, GEO_LITIGE LI, GEO_ORDRE O, GEO_ORDLIG OL
                    WHERE  L.LIT_REF = LI.LIT_REF 					AND
                                LI.ORD_REF_ORIGINE = O.ORD_REF 	AND
                                O.ord_ref = is_ord_ref 					and
                                is_flu_code in ('INCLIT','RESLIT')			and
                                L.TYT_CODE <> 'F' 						and
                                O.ORD_REF = OL.ORD_REF 				and
                                L.ORL_REF  =  OL.ORL_REF 			and
                                L.RES_DEV_PU*L.RES_QTE > 0 ;
                cursor C1 is
                    -- On cherche un contact pour chaque tiers
                    SELECT K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1 , K.con_fluvar, K.con_prenom, K.con_nom
                    FROM GEO_CONTAC K
                    WHERE  K.CON_TIERS = ls_TIE_CODE AND K.FLU_CODE = is_flu_code AND K.valide = 'O' and K.CON_TYT='F' and is_flu_code ='INCLIT';
                 cursor C12 is
                    SELECT K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1 , K.con_fluvar, K.con_prenom, K.con_nom
                    FROM GEO_CONTAC K
                    WHERE K.CON_TIERS = ls_TIE_CODE AND K.FLU_CODE = is_flu_code AND K.valide = 'O';
                is_con_ref varchar2(50) := '';
            begin
                open CO;
                fetch CO into ls_TIE_CODE, ls_LIT_REF;

                ls_LIT_REF_sav := ls_LIT_REF;
                loop

                    is_con_ref := '';

                    UPDATE GEO_LITLIG
                    SET IND_ENV_INC ='O'
                    where LIT_REF = ls_LIT_REF and
                            TIE_CODE	= ls_TIE_CODE and
                            is_flu_code ='INCLIT';
                    commit;

                    open C1;
                    begin
                        fetch C1 	INTO is_con_tyt, is_con_tiers,is_con_ref, is_moc_code, is_con_acces1,is_con_fluvar, is_con_prenom, is_con_nom;
                    exception when no_data_found then
                        is_con_tyt := '';
                        is_con_tiers := ls_TIE_CODE;
                        is_con_ref := '0';
                        is_moc_code := 'IMP';
                        is_con_acces1 := is_imprimante;
                        is_con_fluvar := 'I';
                        is_con_prenom := '';
                        is_con_nom := '';
                        of_insert_envois(
                            is_ord_ref,
                            is_flu_code,
                            mode_auto,
                            is_con_tyt,
                            is_con_tiers,
                            is_con_ref,
                            is_moc_code,
                            is_con_acces1,
                            null,
                            is_con_fluvar,
                            is_con_prenom,
                            is_con_nom,
                            null,
                            null,
                            is_imprimante,
                            null,
                            arg_nom_utilisateur,
                            res,
                            msg,
                            ls_env_code
                        );
                    end;

                    loop
                        of_insert_envois(
                            is_ord_ref,
                            is_flu_code,
                            mode_auto,
                            is_con_tyt,
                            is_con_tiers,
                            is_con_ref,
                            is_moc_code,
                            is_con_acces1,
                            null,
                            is_con_fluvar,
                            is_con_prenom,
                            is_con_nom,
                            null,
                            null,
                            is_imprimante,
                            null,
                            arg_nom_utilisateur,
                            res,
                            msg,
                            ls_env_code
                        );
                        fetch C1 	INTO is_con_tyt, is_con_tiers,is_con_ref, is_moc_code, is_con_acces1,is_con_fluvar, is_con_prenom, is_con_nom;
                        exit when C1%notfound;
                    end loop;
                    close C1;

                    -- Dans le cas de CDV on va ajouter les contactes de la station expéditrice
                    if ls_TIE_CODE = 'CDV' /*and  ib_envoi_cdv = FALSE*/  then

                        -- ib_envoi_cdv = TRUE

                        select     FOU_CODE into ls_TIE_CODE
                        from  GEO_LITLIG L, GEO_LITIGE LI, GEO_ORDRE O, GEO_ORDLIG OL
                        WHERE  L.LIT_REF = LI.LIT_REF 						AND
                            LI.ORD_REF_ORIGINE = O.ORD_REF 		AND
                            L.ORL_REF = OL.ORL_REF				and
                            O.ord_ref = is_ord_ref 						and
                            O.ord_ref = OL.ORD_REF 				and
                            is_flu_code in ('INCLIT','RESLIT') and
                            OL.PROPR_CODE= ls_TIE_CODE and
                            rownum = 1;

                        is_con_ref := '';

                        open C12;
                        fetch C12 	INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;

                        loop
                            -- Il faut forcer le tiers d'origine on garde que les contacts
                            is_con_tiers := 'CDV';
                            of_insert_envois(
                                is_ord_ref,
                                is_flu_code,
                                mode_auto,
                                is_con_tyt,
                                is_con_tiers,
                                is_con_ref,
                                is_moc_code,
                                is_con_acces1,
                                null,
                                is_con_fluvar,
                                is_con_prenom,
                                is_con_nom,
                                null,
                                null,
                                is_imprimante,
                                null,
                                arg_nom_utilisateur,
                                res,
                                msg,
                                ls_env_code
                            );
                            fetch C12 	INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
                            EXIT WHEN C12%notfound;
                        end loop;
                        close C12;
                    end if;

                    fetch CO into ls_TIE_CODE, ls_LIT_REF;
                    EXIT WHEN CO%notfound;
                end loop;
                close CO;
            end;
        else
            declare
                cursor C2 is
                    SELECT DISTINCT L.TIE_CODE, LI.LIT_REF
                    FROM GEO_LITLIG L, GEO_LITIGE LI, GEO_ORDRE O
                    WHERE L.LIT_REF = LI.LIT_REF AND LI.ORD_REF_ORIGINE = O.ORD_REF AND O.ord_ref = is_ord_ref;
                cursor C3 is
                    -- On cherche un contact pour chaque tiers
                    SELECT K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1 , K.con_fluvar, K.con_prenom, K.con_nom
                    FROM GEO_CONTAC K
                    WHERE K.CON_TIERS = ls_TIE_CODE AND K.FLU_CODE = is_flu_code AND K.valide = 'O';
                cursor C31 is
                    SELECT K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1 , K.con_fluvar, K.con_prenom, K.con_nom
                    FROM GEO_CONTAC K
                    WHERE K.CON_TIERS = ls_TIE_CODE AND K.FLU_CODE = is_flu_code AND K.valide = 'O';
            begin
                open C2;
                fetch C2 into ls_TIE_CODE, ls_LIT_REF;
                ls_LIT_REF_sav :=ls_LIT_REF;
                loop

                    is_con_ref := '';

                    open C3;
                    begin
                        fetch C3 	INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
                    exception when no_data_found then
                        is_con_tyt := '';
                        is_con_tiers := ls_TIE_CODE;
                        is_con_ref := '0';
                        is_moc_code := 'IMP';
                        is_con_acces1 := is_imprimante;
                        is_con_fluvar := 'I';
                        is_con_prenom := '';
                        is_con_nom := '';

                        of_insert_envois(
                            is_ord_ref,
                            is_flu_code,
                            mode_auto,
                            is_con_tyt,
                            is_con_tiers,
                            is_con_ref,
                            is_moc_code,
                            is_con_acces1,
                            null,
                            is_con_fluvar,
                            is_con_prenom,
                            is_con_nom,
                            null,
                            null,
                            is_imprimante,
                            null,
                            arg_nom_utilisateur,
                            res,
                            msg,
                            ls_env_code
                        );
                    end;

                    loop
                        of_insert_envois(
                            is_ord_ref,
                            is_flu_code,
                            mode_auto,
                            is_con_tyt,
                            is_con_tiers,
                            is_con_ref,
                            is_moc_code,
                            is_con_acces1,
                            null,
                            is_con_fluvar,
                            is_con_prenom,
                            is_con_nom,
                            null,
                            null,
                            is_imprimante,
                            null,
                            arg_nom_utilisateur,
                            res,
                            msg,
                            ls_env_code
                        );
                        fetch C3 	INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
                        exit when C3%notfound;
                    end loop;
                    close C3;

                    -- Dans le cas de CDV on va ajouter les contactes de la station expéditrice
                    if ls_TIE_CODE = 'CDV' then
                        select     FOU_CODE into ls_TIE_CODE
                        from  GEO_LITLIG L, GEO_LITIGE LI, GEO_ORDRE O, GEO_ORDLIG OL
                        WHERE  L.LIT_REF = LI.LIT_REF 						AND
                                    LI.ORD_REF_ORIGINE = O.ORD_REF 		AND
                                    L.ORL_REF = OL.ORL_REF				and
                                    O.ord_ref = is_ord_ref 						and
                                    O.ord_ref = OL.ORD_REF 				and
                                is_flu_code in ('INCLIT','RESLIT') and
                                rownum = 1;

                        is_con_ref := '';

                        open C31;
                        begin
                            fetch C31 	INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
                        exception when no_data_found then
                            is_con_tiers := 'CDV';
                            of_insert_envois(
                                is_ord_ref,
                                is_flu_code,
                                mode_auto,
                                is_con_tyt,
                                is_con_tiers,
                                is_con_ref,
                                is_moc_code,
                                is_con_acces1,
                                null,
                                is_con_fluvar,
                                is_con_prenom,
                                is_con_nom,
                                null,
                                null,
                                is_imprimante,
                                null,
                                arg_nom_utilisateur,
                                res,
                                msg,
                                ls_env_code
                            );
                        end;

                        loop
                            -- Il faut forcer le tiers d'origine on garde que les contacts
                            is_con_tiers := 'CDV';
                            of_insert_envois(
                                is_ord_ref,
                                is_flu_code,
                                mode_auto,
                                is_con_tyt,
                                is_con_tiers,
                                is_con_ref,
                                is_moc_code,
                                is_con_acces1,
                                null,
                                is_con_fluvar,
                                is_con_prenom,
                                is_con_nom,
                                null,
                                null,
                                is_imprimante,
                                null,
                                arg_nom_utilisateur,
                                res,
                                msg,
                                ls_env_code
                            );
                            fetch C31 	INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
                            EXIT WHEN C31%notfound;
                        end loop;
                        close C31;
                    end if;

                    fetch C2 into ls_TIE_CODE, ls_LIT_REF;
                    exit when C2%notfound;
                end loop;
                close C2;
            end;

        End case;
    end;

    res := 1;
end OF_GENERE_ENVOIS_LITIGE_AUTOM;
/

