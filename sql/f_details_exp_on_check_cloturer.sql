CREATE OR REPLACE PROCEDURE F_EXP_ON_CHECK_CLOTURER (
    arg_orx_ref IN geo_ordlog.ORX_REF%TYPE,
    arg_devalexp_ref IN GEO_HISTO_ORDLOG_DECLO.REF_DEVALEXP%TYPE,
    arg_username IN GEO_USER.NOM_UTILISATEUR%TYPE,
    arg_soc_code IN GEO_ORDRE.SOC_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
   ls_cloture varchar2(50);
   ls_ord_ref varchar2(50);
   ls_fou_code varchar2(50);
   ls_cli_ref varchar2(50);
   ls_modif_detail_client varchar2(50);
   ls_modif_detail varchar2(50);
   ls_sco_code varchar2(50);
   ls_typ_ordre varchar2(50);
   ls_ok_detail varchar2(50);

   ls_profile_client varchar2(50);
   ls_geo_client varchar2(50);

   ld_pal_nb_sol number;
   ld_pal_nb_pb100x120 number;
   ld_pal_nb_pb80x120 number;
   ld_pal_nb_pb60x80 number;

    -- Par defaut
    ll_check_chep number := 1;
    ib_entrep_chep varchar2(50);
BEGIN
    msg := '';
    res := 0;

    SELECT orx.ord_ref, orx.flag_exped_fournni, orx.fou_code, o.cli_ref
    into ls_ord_ref, ls_cloture, ls_fou_code, ls_cli_ref
    from geo_ordlog orx
    left join geo_ordre o on o.ord_ref = orx.ord_ref
    where orx_ref = arg_orx_ref;

    SELECT profile_client, geo_client
    into ls_profile_client, ls_geo_client
    from geo_user
    where nom_utilisateur = arg_username;

    SELECT e.gest_ref
    into ib_entrep_chep
    from geo_entrep e
    left join geo_ordre o on o.cen_ref = e.cen_ref
    where ord_ref = ls_ord_ref;

    if ls_cloture = 'N' then

		select IND_MODIF_DETAIL into ls_modif_detail_client
		from GEO_CLIENT
		where CLI_REF =ls_cli_ref;

		select IND_MODIF_DETAIL into ls_modif_detail
		from GEO_FOURNI
		where FOU_CODE =ls_fou_code;

		select O.SCO_CODE, O.TYP_ORDRE  into ls_sco_code, ls_typ_ordre --llef modif pour recup le typ_ordre
		from GEO_ORDRE O
		where O.ord_ref = ls_ord_ref ;

        begin
            select distinct 'OK_DETAIL' into ls_ok_detail
            from GEO_ORDLIG L , GEO_ARTICLE_COLIS A
            where  L.ORD_REF = ls_ord_ref and
                        L.FOU_CODE = ls_fou_code and
                    L.ART_REF 	= A.ART_REF and
                        A.IND_MODIF_DETAIL ='O' ;
        exception when no_data_found then
		    ls_ok_detail := '';
        end;


--		If ld_date_today> ld_date_modif  and ls_profile_client <> 'ADMIN'  and ls_geo_client <>'2' and arg_soc_code <>'IMP' and arg_soc_code <> 'IUK' and  ls_modif_detail <> 'O' and ls_modif_detail_client <> 'O'  and  ls_sco_code <>'PAL'  and ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR'  and ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR'  AND ls_typ_ordre <> 'RDF'  and ls_ok_detail <> 'OK_DETAIL' Then
		If  ls_profile_client <> 'ADMIN'  and ls_geo_client <>'2' and arg_soc_code <>'IMP' and arg_soc_code <> 'IUK' and  ls_modif_detail <> 'O' and ls_modif_detail_client <> 'O'  and  ls_sco_code <>'PAL'  and ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR'  and ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR'  AND ls_typ_ordre <> 'RDF'  and ls_ok_detail <> 'OK_DETAIL' Then
			-- messagebox("Erreur","Fonction non autorisée",stopsign!)
            msg := 'Erreur, Fonction non autorisée';
            res := 0;
			return;
		end If;

		-- open(w_saisie_comm_devalid_ordlog)

		-- arg_devalexp_ref = Message.StringParm
		-- If arg_devalexp_ref ='CANCEL' Then
		-- 	return 2
		-- End If

		insert into GEO_HISTO_ORDLOG_DECLO(ORX_REF,FLAG_EXPED_FOURNNI,REF_DEVALEXP)
		VALUES (arg_orx_ref,ls_cloture,arg_devalexp_ref);
        commit;

		--if ls_geo_client = '2' then
        update geo_ordlog set datdep_fou_r = null where orx_ref = arg_orx_ref;


		--else
		--	MessageBox('Opération impossible','Vous ne pouvez pas décloturer cet ordre', StopSign!)
		--	return 2
		--end if
	elsif ls_cloture = 'O' then

		select IND_MODIF_DETAIL into ls_modif_detail_client
		from GEO_CLIENT
		where CLI_REF = ls_cli_ref;

		select IND_MODIF_DETAIL into ls_modif_detail
		from GEO_FOURNI
		where FOU_CODE =ls_fou_code;

		select O.SCO_CODE, O.TYP_ORDRE  into ls_sco_code, ls_typ_ordre --llef modif pour recup le typ_ordre
		from GEO_ORDRE O
		where O.ord_ref = ls_ord_ref;

		select distinct  'OK_DETAIL' into ls_ok_detail
		from GEO_ORDLIG L , GEO_ARTICLE_COLIS A
		where  L.ORD_REF = ls_ord_ref and
                    L.FOU_CODE = ls_fou_code and
		           L.ART_REF 	= A.ART_REF and
            		  A.IND_MODIF_DETAIL ='O' ;

		if ls_ok_detail is null then ls_ok_detail :=''; end if;

		If ls_profile_client <> 'ADMIN'  and ls_geo_client <>'2' and arg_soc_code <>'IMP' and arg_soc_code <> 'IUK' and  ls_modif_detail <> 'O'  and ls_modif_detail_client <>'O' and  ls_sco_code <>'PAL'  and ls_typ_ordre <> 'REP'  and ls_typ_ordre <> 'RPO' and ls_typ_ordre <> 'RPR' and ls_typ_ordre <> 'RPF' and ls_ok_detail <> 'OK_DETAIL'    Then
            msg := 'Erreur, Fonction non autorisée';
            res := 0;
			return;
		end If;

        SELECT pal_nb_sol, pal_nb_pb100x120, pal_nb_pb80x120, pal_nb_pb60x80
        into ld_pal_nb_sol, ld_pal_nb_pb100x120, ld_pal_nb_pb80x120, ld_pal_nb_pb60x80
        from geo_ordlog
        where orx_ref = arg_orx_ref;

		if ld_pal_nb_sol is null then ld_pal_nb_sol := 0; end if;
		if ld_pal_nb_pb100x120 is null then ld_pal_nb_pb100x120 := 0; end if;
		if ld_pal_nb_pb80x120 is null then ld_pal_nb_pb80x120 := 0; end if;
		if ld_pal_nb_pb60x80 is null then ld_pal_nb_pb60x80 := 0; end if;

		if ld_pal_nb_sol = 0 then
            msg := 'Erreur de saisie: nombre de palettes au sol OBLIGATOIRE !';
            res := 0;
			return;
		end if;

		if arg_soc_code = 'BWS' then
            declare
                ll_nb_pal_chep number;
            begin
                select count(*)
                into ll_nb_pal_chep
                from geo_ordlig OL, geo_ordre O, geo_palett P
                where
                    O.ord_ref = ls_ord_ref and
                    OL.ORD_REF = O.ORD_REF AND
                    OL.FOU_CODE = ls_fou_code AND
                    OL.PAL_CODE = P.PAL_CODE AND
                    P.GEST_CODE = 'CHEP';

                if ll_nb_pal_chep = 0 then
                    -- Dans ce cas on vérifie pas
                    ll_check_chep := 0;
                end if;
            end;
		end if;

		if ib_entrep_chep is not null and ll_check_chep = 1 then
			if ld_pal_nb_pb100x120 = 0 and ld_pal_nb_pb80x120 = 0 and ld_pal_nb_pb60x80 = 0 then
                msg := 'Erreur de saisie: vous devez saisir la quantité de palettes bleues expédiées par type de palettes (y compris les palettes intermédiaires)';
                res := 0;
                return;
			end if;
		end if;

        update geo_ordlog set datdep_fou_r = current_timestamp where orx_ref = arg_orx_ref;

		-- lst_detail_entete.s_ordref = ls_ord_ref
		-- lst_detail_entete.s_fou_code = ls_fou_code

		select TYP_ORDRE into ls_typ_ordre
		from GEO_ORDRE
		where ORD_REF =ls_ord_ref;

		If ls_typ_ordre ='RGP' Then
			declare
                cursor cur_ordre_orig is
                    select  distinct ORD_REF_ORIG
                    from geo_gest_regroup R
                    where R.ord_ref_rgp = ls_ord_ref 	and
                            R.fou_code_orig =ls_fou_code;
            begin
                for r in cur_ordre_orig
                loop
                    f_tracabilite_cloturer(r.ord_ref_orig, 'O', res, msg);
                end loop;
            end;
		End If;
	end  if;

	f_cloture_log_grp(ls_ord_ref, ls_fou_code, ls_cloture, res, msg);

    res := 1;
    msg := 'OK';
END;
/
