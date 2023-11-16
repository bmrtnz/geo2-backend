CREATE OR REPLACE PROCEDURE OF_GENERE_ENVOI_DETAIM(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
    mode_auto IN char,
    arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,

    res OUT number,
    msg OUT varchar2
)
AS
    ls_ref_logistique varchar2(50);
    ls_ref_document varchar2(50);
    ls_ref_logistique_bw varchar2(70);
    ls_ref_document_bw varchar2(70);
    ls_fou_code varchar2(50);
    ls_fou_ref_doc varchar2(50);
    ls_instructions varchar2(50);
    ls_grp_code varchar2(50);
    ls_trp_code varchar2(50);
    ls_chg_code varchar2(50);
    ldt_datdep_fou_p timestamp;
    ldt_datdep_fou_r timestamp;
    ldt_datdep_grp_p timestamp;
    ldt_datdep_grp_r timestamp;
    ldt_datdep_chg_p timestamp;
    ldt_datdep_chg_r timestamp;
    ll_ind number;
    ll_rc number;
    ll_count NUMBER;
    ls_ar_fou_code varchar2(50);
    ldt_ordre_mod_date timestamp;
    ldt_ordlig_mod_date timestamp;
    ldt_envois_mod_date timestamp;
    ls_contact varchar2(50);
    ls_debug varchar2(50);
    lsa_ar_fou_code p_str_tab_type := p_str_tab_type();
    lsa_ar_desc  p_str_tab_type := p_str_tab_type();
    ls_ar_desc varchar2(50);
    ll_ind2 NUMBER;
    -- s_par
    ls_ar_fou_code_en_trop varchar2(50); -- ceux qui ne devraient pas recevoir d'annule-et-remplace
    -- s_ret

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
    is_instructions_logistique varchar2(280);
    is_trs_ville varchar2(50);
    is_crt_bta_code varchar2(50);
    is_ref_cli varchar2(50);
    is_per_codeass_prenom varchar2(50);
    is_per_codecom_prenom varchar2(50);
    idt_depdatp timestamp;
    idt_datlivp timestamp;
    id_remsf_tx number;
    id_remhf_tx number;
    id_crt_tx number;
    is_ttr_code varchar2(50);
    is_ttr_desc varchar2(50);

    ls_TIE_CODE varchar2(50);

    is_con_tyt varchar2(1);
    is_con_tiers varchar2(18);
    is_con_ref varchar2(6);
    is_moc_code varchar2(3);
    is_con_acces1 varchar2(70);
    ls_con_access2 varchar2(70);
    is_con_fluvar varchar2(6);
    is_con_prenom varchar2(35);
    is_con_nom varchar2(35);
    is_con_dot varchar2(70);
    is_con_map varchar2(70);

    ls_mail_com GEO_PERSON.per_email%TYPE;
    ls_mail_ass GEO_PERSON.per_email%TYPE;

    ls_env_code geo_envois.env_code%TYPE;

    is_ref_logistique varchar2(70);
    is_ref_document varchar2(70);
    is_imprimante varchar2(35);
    is_imp_full varchar2(50);



BEGIN
    res := 0;
    msg := '';
    begin
        -- on récupère les différents intervenants de l'en-tête
        select
            a.soc_code, a.cam_code, a.nordre, a.per_codeass, a.per_codecom, a.cli_ref, b.cli_code, a.cen_ref, c.cen_code, a.sco_code,
            a.trp_code, a.trs_code, a.crt_code, a.dev_code, a.dev_tx, a.tvr_code, a.inc_code, d.inc_desc, a.inc_lieu, a.instructions_logistique,
            a.trs_ville, a.crt_bta_code, a.ref_cli, e.per_prenom, e.per_email, f.per_prenom, f.per_email, a.depdatp, a.livdatp, a.remsf_tx, a.remhf_tx,
            a.crt_pu, a.ttr_code, g.ttr_desc, a.ref_logistique, a.ref_document
        into
            is_soc_code, is_cam_code, is_nordre, is_per_codeass, is_per_codecom, is_cli_ref, is_cli_code, is_cen_ref, is_cen_code, is_sco_code,
            is_trp_code, is_trs_code, is_crt_code, is_dev_code, id_dev_tx, is_tvr_code, is_inc_code, is_inc_desc, is_inc_lieu, is_instructions_logistique,
            is_trs_ville, is_crt_bta_code, is_ref_cli, is_per_codeass_prenom, ls_mail_ass, is_per_codecom_prenom, ls_mail_com, idt_depdatp, idt_datlivp, id_remsf_tx, id_remhf_tx,
            id_crt_tx, is_ttr_code, is_ttr_desc, ls_ref_logistique_bw, ls_ref_document_bw
        from geo_ordre a, geo_client b, geo_entrep c, geo_incote d, geo_person e, geo_person f, geo_typtrp g
        where
                a.ord_ref = is_ord_ref
          and a.cli_ref = b.cli_ref (+)
          and a.cen_ref = c.cen_ref (+)
          and a.inc_code = d.inc_code (+)
          and a.per_codeass = e.per_code (+)
          and a.per_codecom = f.per_code (+)
          and g.ttr_code (+) = a.ttr_code;

    exception when others then
        if mode_auto = 'N' then
            msg := msg || ' select geo_ordre ordre ' || is_ord_ref || ' inconnu - erreur : ' || SQLERRM;
        end if;
        res := 2;
        return;
    end;

    -- This.Title  = 'documents proposés pour l''ordre ' || is_nordre || ' flux ' + is_flu_code
    if is_soc_code is null then is_soc_code := ''; end if ;
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
    if ls_ref_logistique_bw is not NULL then is_ref_logistique := ls_ref_logistique_bw || ' ' ; end if;
    if ls_ref_document_bw is not NULL then is_ref_document := ls_ref_document_bw || ' ' ; end if;

    -- on récupère l'imprimante de l'assistant(e)
    -- is_imprimante    = gs_user.imp_ref
    BEGIN
        select gu.imp_ref into is_imprimante
        from geo_user gu
        where gu.nom_utilisateur = arg_nom_utilisateur;
        select imp_id into is_imp_full from geo_imprim where imp_ref = is_imprimante;
    exception when others then
        is_imp_full := '';
    end;

    -- On liste les tiers impliqués
    BEGIN
        SELECT
            GEO_ORDLIG.FOU_CODE
        INTO
            ls_TIE_CODE
        FROM
            GEO_HISTO_MODIF_DETAIL,
            GEO_ORDLIG
        WHERE
                GEO_HISTO_MODIF_DETAIL.ORD_REF = is_ord_ref
          AND GEO_ORDLIG.ORD_REF = GEO_HISTO_MODIF_DETAIL.ORD_REF
          AND GEO_ORDLIG.ORL_REF = GEO_HISTO_MODIF_DETAIL.ORL_REF
          AND GEO_HISTO_MODIF_DETAIL.MOD_DATE = (
            SELECT
                max(MOD_DATE)
            FROM
                GEO_HISTO_MODIF_DETAIL
            WHERE
                    ORD_REF = is_ord_ref
        )
          AND GEO_HISTO_MODIF_DETAIL.ORL_REF = GEO_ORDLIG.ORL_REF;

    END;



    BEGIN
        DECLARE
            cursor C1 is
                -- On cherche un contact pour chaque tiers
                SELECT K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1, K.con_fluvar, K.con_prenom, K.con_nom
                FROM GEO_CONTAC K
                WHERE K.CON_TIERS = ls_TIE_CODE
                  AND K.FLU_CODE = is_flu_code
                  AND K.valide = 'O';

        begin

            is_con_ref := '';

            open C1;
            begin
                fetch C1 into is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
            exception when no_data_found then
                /*
                    is_con_tyt = ''
                    is_con_tiers = ls_TIE_CODE
                    is_con_ref = '0'
                    is_moc_code = 'IMP'
                    is_con_acces1 = is_imprimante
                    is_con_fluvar = 'I'
                    is_con_prenom  = ''
                    is_con_nom = ''

                    il_row  = of_insert_envoi()*/

                is_con_tyt := 'F';
                is_con_tiers := ls_TIE_CODE;
                is_con_ref := '0';
                is_con_acces1 := is_imprimante;

                if is_per_codeass_prenom <> '' then
                    is_con_prenom := is_per_codeass_prenom;
                    is_con_acces1 := ls_mail_ass;
                    is_moc_code := 'MAI';

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
                end if;

                if is_per_codecom_prenom <> '' then
                    is_con_prenom := is_per_codecom_prenom;
                    is_con_acces1 := ls_mail_com;
                    is_moc_code := 'MAI';

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
                end if;
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

                fetch C1    into is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
                exit when C1%notfound;
            end loop;
            close C1;
        end;
    end;

    res := 1;

end OF_GENERE_ENVOI_DETAIM;
/
