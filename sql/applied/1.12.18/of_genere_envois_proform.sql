CREATE OR REPLACE PROCEDURE "GEO_ADMIN".OF_GENERE_ENVOIS_PROFORM (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
    arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    is_soc_code GEO_SOCIETE.SOC_CODE%TYPE;
    is_cam_code GEO_CAMPAG.CAM_CODE%TYPE;
    is_nordre GEO_ORDRE.NORDRE%TYPE;
    is_per_codeass GEO_ORDRE.PER_CODEASS%TYPE;
    is_per_codecom GEO_ORDRE.PER_CODECOM%TYPE;
    is_cli_ref GEO_CLIENT.CLI_REF%TYPE;
    is_cli_code GEO_CLIENT.CLI_CODE%TYPE;
    is_cen_ref GEO_ORDRE.CEN_REF%TYPE;
    is_cen_code GEO_ORDRE.CEN_CODE%TYPE;
    is_sco_code GEO_ORDRE.SCO_CODE%TYPE;
    is_trp_code GEO_ORDRE.TRP_CODE%TYPE;
    is_trs_code GEO_ORDRE.TRS_CODE%TYPE;
    is_crt_code GEO_ORDRE.CRT_CODE%TYPE;
    is_dev_code GEO_ORDRE.DEV_CODE%TYPE;
    id_dev_tx GEO_ORDRE.DEV_TX%TYPE;
    is_tvr_code GEO_ORDRE.TVR_CODE%TYPE;
    is_inc_code GEO_INCOTE.INC_CODE%TYPE;
    is_inc_desc GEO_INCOTE.INC_DESC%TYPE;
    is_inc_lieu GEO_ORDRE.INC_LIEU%TYPE;
    is_instructions_logistique GEO_ORDRE.INSTRUCTIONS_LOGISTIQUE%TYPE;
    is_trs_ville GEO_ORDRE.TRS_CODE%TYPE;
    is_crt_bta_code GEO_ORDRE.CRT_BTA_CODE%TYPE;
    is_ref_cli GEO_ORDRE.REF_CLI%TYPE;
    is_per_codeass_prenom GEO_PERSON.PER_PRENOM%TYPE;
    ls_mail_ass GEO_PERSON.PER_EMAIL%TYPE;
    is_per_codecom_prenom GEO_PERSON.PER_PRENOM%TYPE;
    ls_mail_com GEO_PERSON.PER_EMAIL%TYPE;
    idt_depdatp GEO_ORDRE.DEPDATP%TYPE;
    idt_datlivp GEO_ORDRE.LIVDATP%TYPE;
    id_remsf_tx GEO_ORDRE.REMSF_TX%TYPE;
    id_remhf_tx GEO_ORDRE.REMHF_TX%TYPE;
    id_crt_tx GEO_ORDRE.CRT_PU%TYPE;
    is_ttr_code GEO_TYPTRP.TTR_CODE%TYPE;
    is_ttr_desc GEO_TYPTRP.TTR_DESC%TYPE;
    ls_ref_logistique_bw GEO_ORDRE.REF_LOGISTIQUE%TYPE;
    ls_ref_document_bw GEO_ORDRE.REF_DOCUMENT%TYPE;

    is_con_tyt varchar2(50);
    is_con_tiers varchar2(50);
    is_con_ref varchar2(50);
    is_moc_code varchar2(50);
    is_con_acces1 varchar2(50);
    is_con_fluvar varchar2(50);
    is_con_prenom varchar2(50);
    is_con_nom varchar2(50);
    is_ref_logistique varchar2(80);
    is_ref_document varchar2(80);
    is_imprimante varchar2(35);
    is_imp_full varchar2(50);
    ls_env_code geo_envois.env_code%TYPE;

    CURSOR C1 (code_cli GEO_CLIENT.CLI_CODE%TYPE, code_flu GEO_FLUX.FLU_CODE%TYPE)
    IS
        SELECT K.con_tyt, K.con_tiers, K.con_ref, K.moc_code, K.con_acces1 , K.con_fluvar, K.con_prenom, K.con_nom
        FROM GEO_CONTAC K
        WHERE K.CON_TIERS = code_cli AND K.FLU_CODE = code_flu AND K.valide = 'O' and CON_TYT ='C';
BEGIN
    -- correspond à of_genere_envois_proform.pbl
    msg := '';
    res := 0;

    -- on récupère les différents intervenants de l'en-tête
    select A.soc_code, A.cam_code, A.nordre, A.per_codeass, A.per_codecom, A.cli_ref, B.cli_code, A.cen_ref, C.cen_code, A.sco_code,
           A.trp_code, A.trs_code, A.crt_code, A.dev_code, A.dev_tx, A.tvr_code, A.inc_code, D.inc_desc, A.inc_lieu,
           A.instructions_logistique,	A.trs_ville, A.crt_bta_code, A.ref_cli, E.per_prenom, E.per_email,F.per_prenom, F.per_email,
           A.depdatp, A.livdatp, A.remsf_tx, A.remhf_tx, A.crt_pu, A.ttr_code, G.ttr_desc, A.ref_logistique, A. ref_document
    into is_soc_code, is_cam_code, is_nordre, is_per_codeass, is_per_codecom, is_cli_ref, is_cli_code, is_cen_ref, is_cen_code, is_sco_code,
        is_trp_code, is_trs_code, is_crt_code, is_dev_code, id_dev_tx, is_tvr_code, is_inc_code, is_inc_desc, is_inc_lieu,
        is_instructions_logistique, is_trs_ville,is_crt_bta_code, is_ref_cli, is_per_codeass_prenom,ls_mail_ass, is_per_codecom_prenom,ls_mail_com,
        idt_depdatp, idt_datlivp, id_remsf_tx, id_remhf_tx, id_crt_tx, is_ttr_code, is_ttr_desc, ls_ref_logistique_bw, ls_ref_document_bw
    from geo_ordre A, geo_client B, geo_entrep C, geo_incote D, geo_person E, geo_person F, geo_typtrp G
    where A.ord_ref = is_ord_ref and A.cli_ref = B.cli_ref (+) and A.cen_ref = C.cen_ref (+) and A.inc_code = D.inc_code (+)
      and A.per_codeass = E.per_code (+) and A.per_codecom = F.per_code (+)  and G.ttr_code (+) = A.ttr_code;

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

    -- is_ref_doc et log sont constitués des ref de geo_ordre et de geo_ordlog mis bout à bout
    if ls_ref_logistique_bw is not null then
        is_ref_logistique := ls_ref_logistique_bw || ' ';
    end if;
    if ls_ref_document_bw is not null then
        is_ref_document := ls_ref_document_bw || ' ';
    end if;

    -- on récupère l'imprimante de l'assistant(e)
    begin
        select gu.imp_ref into is_imprimante
        from geo_user gu
        where gu.nom_utilisateur = arg_nom_utilisateur;
        select imp_id into is_imp_full from geo_imprim where imp_ref = is_imprimante;
    exception when others then
        is_imp_full	:= '';
    end;

    -- On cherche un contact pour chaque tiers
    OPEN C1(is_cli_code, is_flu_code);
    fetch C1 INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;

    if C1%notfound then
        is_con_tyt := 'C';
        is_con_tiers := is_cli_code;
        is_con_ref := '';
        is_con_acces1 := is_imprimante;

        If is_per_codeass_prenom is not null then
            is_con_prenom := is_per_codeass_prenom;
            is_con_acces1 := ls_mail_ass;
            is_moc_code := 'MAI';

            OF_INSERT_ENVOIS(
                is_ord_ref, is_flu_code, 'N', is_con_tyt,
                is_con_tiers, is_con_ref, is_moc_code, is_con_acces1,
                null, is_con_fluvar, is_con_prenom, is_con_nom,
                null, null, is_imprimante, null,
                arg_nom_utilisateur, res, msg, ls_env_code
            );
        End If;

        If is_per_codecom_prenom is not null then
            is_con_prenom := is_per_codecom_prenom;
            is_con_acces1 := ls_mail_com;
			is_moc_code := 'MAI';

            OF_INSERT_ENVOIS(
                is_ord_ref, is_flu_code, 'N', is_con_tyt,
                is_con_tiers, is_con_ref, is_moc_code, is_con_acces1,
                null, is_con_fluvar, is_con_prenom, is_con_nom,
                null, null, is_imprimante, null,
                arg_nom_utilisateur, res, msg, ls_env_code
            );
        end IF;
    end if;
    LOOP
        OF_INSERT_ENVOIS(
            is_ord_ref, is_flu_code, 'N', is_con_tyt,
            is_con_tiers, is_con_ref, is_moc_code, is_con_acces1,
            null, is_con_fluvar, is_con_prenom, is_con_nom,
            null, null, is_imprimante, null,
            arg_nom_utilisateur, res, msg, ls_env_code
        );

        fetch C1 INTO is_con_tyt, is_con_tiers, is_con_ref, is_moc_code, is_con_acces1, is_con_fluvar, is_con_prenom, is_con_nom;
        EXIT WHEN C1%notfound;
    END LOOP;
    CLOSE C1;

    -- décision du 28/09/06 on supprime les AR inutiles (l'algorithme a l'air stable)

    msg := 'OK';
    res := 1;
END OF_GENERE_ENVOIS_PROFORM;
/
