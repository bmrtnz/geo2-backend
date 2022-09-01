CREATE OR REPLACE PROCEDURE OF_AR_ENVOIS (
	is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	res IN OUT number,
	msg IN OUT varchar2
)
AS
	ll_rc number;
	ll_ind number;
	ls_fra_desc varchar2(50);
	ls_flag_annul varchar2(50);
	lb_transport_en_cours boolean;
	ll_trp_approc number;
BEGIN

	res := 0;
	msg := '';

	-- clear "buffer"
	delete from geo_envois
	where ord_ref = is_ord_ref
	and (trait_exp = 'R' or trait_exp = 'A');

	-- on tente de caractériser les annule et remplace si flux ordre
	-- BAM le 21/09/2016

	begin
		SELECT  FRA_DESC
		INTO 	ls_fra_desc
		FROM 	GEO_ORDFRA
		WHERE 	ORD_REF = is_ord_ref and
					FRA_CODE ='FRET' and
					ROWNUM <= 1;
	exception when no_data_found then
		ls_fra_desc := '';
	end;

	--Deb LLEF recup de la valeur du flag_annul de l'ordre
	select flannul
	into ls_flag_annul
	from geo_ordre
	where ord_ref = is_ord_ref;
	--Fin LLEF

	declare
		ar_desc varchar2(100);
		ls_env_code varchar2(50);
		res_use_trp boolean;
		cursor an_date is
			SELECT
				geo_envois.tyt_code as tyt_code,
				geo_envois.LAN_CODE as lan_code,
				geo_envois.SCO_CODE as sco_code,
				geo_envois.cam_code as cam_code,
				geo_ordlig.fou_code as fou_code,
				max(geo_ordre.mod_date) as date_entete,
				max(geo_ordlig.mod_date) as date_lignes,
				max(geo_envois.demdat) as date_envois,
				geo_envois.moc_code as moc_code,
				geo_envois.acces1 as acces1,
				geo_envois.soc_code as soc_code,
				geo_envois.nordre as nordre,
				geo_envois.per_codeass as ass_code,
				geo_envois.per_codecom as com_code,
				geo_envois.con_ref as con_ref,
				geo_envois.doc_filename as doc_filename
			FROM
				geo_envois,
				geo_ordlig,
				geo_ordre
			WHERE
				( geo_envois.tie_code = geo_ordlig.fou_code)
				AND
					( geo_envois.ord_ref = geo_ordlig.ord_ref )
				AND
					( geo_ordre.ord_ref = geo_ordlig.ord_ref )
				AND
					( ( geo_ordlig.ord_ref = is_ord_ref )
					AND
					( geo_envois.flu_code = 'ORDRE' )
						AND
					( geo_envois.tyt_code = 'F' ) )
			GROUP BY
				geo_envois.tyt_code,
				geo_ordlig.fou_code,
				geo_envois.lan_code,
				geo_envois.sco_code,
				geo_envois.cam_code,
				geo_envois.moc_code,
				geo_envois.acces1,
				geo_envois.soc_code,
                geo_envois.nordre,
                geo_envois.per_codeass,
                geo_envois.per_codecom,
				geo_envois.con_ref,
				geo_envois.doc_filename
			UNION
			SELECT
				geo_envois.tyt_code as tyt_code,
				geo_envois.LAN_CODE as lan_code,
				geo_envois.SCO_CODE as sco_code,
				geo_envois.cam_code as cam_code,
				geo_envois.tie_code as fou_code,
				max(geo_ordre.mod_date) as date_entete,
				NULL as date_lignes,
				max(geo_envois.demdat) as date_envois,
                geo_envois.moc_code as moc_code,
				geo_envois.acces1 as acces1,
				geo_envois.soc_code as soc_code,
				geo_envois.nordre as nordre,
				geo_envois.per_codeass as ass_code,
				geo_envois.per_codecom as com_code,
				geo_envois.con_ref as con_ref,
				geo_envois.doc_filename as doc_filename
			FROM
				geo_envois,
				geo_ordre
			WHERE
				( geo_ordre.ord_ref = is_ord_ref )
				AND
														( geo_envois.ord_ref = geo_ordre.ord_ref )
				AND
										( geo_envois.flu_code = 'ORDRE' )
				AND
						( geo_envois.tyt_code = 'C' )
				AND
															(geo_envois.tie_code = geo_ordre.cli_code)
			GROUP BY
				geo_envois.tyt_code,
				geo_envois.tie_code,
				geo_envois.lan_code,
				geo_envois.sco_code,
				geo_envois.cam_code,
                geo_envois.moc_code,
				geo_envois.acces1,
				geo_envois.soc_code,
                geo_envois.nordre,
                geo_envois.per_codeass,
                geo_envois.per_codecom,
				geo_envois.con_ref,
				geo_envois.doc_filename
			UNION
			SELECT
				geo_envois.tyt_code as tyt_code,
				geo_envois.LAN_CODE as lan_code,
				geo_envois.SCO_CODE as sco_code,
				geo_envois.cam_code as cam_code,
				geo_envois.tie_code as fou_code,
				max(geo_ordre.mod_date) as date_entete,
				NULL as date_lignes,
				max(geo_envois.demdat) as date_envois,
                geo_envois.moc_code as moc_code,
				geo_envois.acces1 as acces1,
				geo_envois.soc_code as soc_code,
				geo_envois.nordre as nordre,
				geo_envois.per_codeass as ass_code,
				geo_envois.per_codecom as com_code,
				geo_envois.con_ref as con_ref,
				geo_envois.doc_filename as doc_filename
			FROM
				geo_envois,
				geo_ordre
			WHERE
				( geo_ordre.ord_ref = is_ord_ref )
				AND
														( geo_envois.ord_ref = geo_ordre.ord_ref )
				AND
										( geo_envois.flu_code = 'ORDRE' )
				AND
						( geo_envois.tyt_code = 'E' )
				AND
														(geo_envois.tie_code = geo_ordre.cen_code)
			GROUP BY
				geo_envois.tyt_code,
				geo_envois.tie_code,
				geo_envois.lan_code,
				geo_envois.sco_code,
				geo_envois.cam_code,
                geo_envois.moc_code,
				geo_envois.acces1,
				geo_envois.soc_code,
                geo_envois.nordre,
                geo_envois.per_codeass,
                geo_envois.per_codecom,
				geo_envois.con_ref,
				geo_envois.doc_filename
			UNION
			SELECT
				geo_envois.tyt_code as tyt_code,
				geo_envois.LAN_CODE as lan_code,
				geo_envois.SCO_CODE as sco_code,
				geo_envois.cam_code as cam_code,
				geo_envois.tie_code as fou_code,
				max(geo_ordre.mod_date) as date_entete,
				NULL as date_lignes,
				max(geo_envois.demdat) as date_envois,
                geo_envois.moc_code as moc_code,
				geo_envois.acces1 as acces1,
				geo_envois.soc_code as soc_code,
				geo_envois.nordre as nordre,
				geo_envois.per_codeass as ass_code,
				geo_envois.per_codecom as com_code,
				geo_envois.con_ref as con_ref,
				geo_envois.doc_filename as doc_filename
			FROM
				geo_envois,
				geo_ordre
			WHERE
				( geo_ordre.ord_ref = is_ord_ref )
				AND
														( geo_envois.ord_ref = geo_ordre.ord_ref )
				AND
										( geo_envois.flu_code = 'ORDRE' )
				AND ( geo_envois.tyt_code = 'T' )
			GROUP BY
				geo_envois.tyt_code,
				geo_envois.tie_code,
				geo_envois.lan_code,
				geo_envois.sco_code,
				geo_envois.cam_code,
                geo_envois.moc_code,
				geo_envois.acces1,
				geo_envois.soc_code,
                geo_envois.nordre,
                geo_envois.per_codeass,
                geo_envois.per_codecom,
				geo_envois.con_ref,
				geo_envois.doc_filename
			UNION
			SELECT
				geo_envois.tyt_code as tyt_code,
				geo_envois.LAN_CODE as lan_code,
				geo_envois.SCO_CODE as sco_code,
				geo_envois.cam_code as cam_code,
				geo_ordlog.grp_code as fou_code,
				max(geo_ordre.mod_date) as date_entete,
				NULL as date_lignes,
				max(geo_envois.demdat) as date_envois,
                geo_envois.moc_code as moc_code,
				geo_envois.acces1 as acces1,
				geo_envois.soc_code as soc_code,
				geo_envois.nordre as nordre,
				geo_envois.per_codeass as ass_code,
				geo_envois.per_codecom as com_code,
				geo_envois.con_ref as con_ref,
				geo_envois.doc_filename as doc_filename
			FROM
				geo_envois,
				geo_ordlog,
				geo_ordre
			WHERE
				( geo_envois.tie_code = geo_ordlog.grp_code)
				AND
					( geo_envois.ord_ref = geo_ordlog.ord_ref )
				AND
					( geo_ordre.ord_ref = geo_ordlog.ord_ref )
				AND
					( ( geo_ordlog.ord_ref = is_ord_ref )
					AND
					( geo_envois.flu_code = 'ORDRE' )
						AND
					( geo_envois.tyt_code IN ('G', 'S', 'O' )) )
			GROUP BY
				geo_envois.tyt_code,
				geo_ordlog.grp_code,
				geo_envois.lan_code,
				geo_envois.sco_code,
				geo_envois.cam_code,
                geo_envois.moc_code,
				geo_envois.acces1,
				geo_envois.soc_code,
                geo_envois.nordre,
                geo_envois.per_codeass,
                geo_envois.per_codecom,
				geo_envois.con_ref,
				geo_envois.doc_filename;
	begin
		for r in an_date
		loop

			If r.tyt_code = 'T' and ls_fra_desc <> ''  and  ls_flag_annul <> 'O' then
				--dw_table.SetItem(ar_desc, 'ar_desc', ls_fra_desc)
				select count(*) into ll_trp_approc from geo_ordlog where ord_ref = is_ord_ref and trp_code = r.fou_code;
				if ll_trp_approc > 0 then
					ar_desc := '';
				else
					ar_desc := ls_fra_desc;
				end if;
			end if;

			If r.tyt_code = 'T'  Then
				f_is_transporteur_use(is_ord_ref,r.fou_code,res,msg,res_use_trp);

				If res_use_trp = FALSE Then
					ar_desc := 'COMMANDE ANNULEE';
				End If;
			End If;

			select F_SEQ_ORX_SEQ into ls_env_code from dual;
            select f_seq_end_num() into ls_end_code from dual;
			-- On se sert de la table geo_envois comme table tampon
			insert into geo_envois (
				env_code,
				flu_code,
				trait_exp,
				ord_ref,
				lan_code,
				sco_code,
				cam_code,
				tyt_code, -- tyt_code
				tie_code, -- fou_code
				demdat, -- date_entete
				soudat, -- date_lignes
				envdat, -- date_envois
				env_desc, -- ar_desc
                moc_code,
                acces1,
                soc_code,
                nordre,
                per_codeass,
                per_codecom,
				con_ref,
				doc_filename
			)
			values (
				ls_env_code,
				'ORDRE',
				'R',
				is_ord_ref,
				r.lan_code,
				r.sco_code,
				r.cam_code,
				r.tyt_code,
				r.fou_code,
				r.date_entete,
				r.date_lignes,
				r.date_envois,
				ar_desc,
				r.moc_code,
				r.acces1,
                r.soc_code,
                r.nordre,
                r.ass_code,
                r.com_code,
				r.con_ref,
				r.doc_filename
			);
			commit;
		end loop;
	end;

	--	dw_table.SetItem(ll_rc, 'flag_entete', lsa_flag_entete[ll_ind])
	--	dw_table.SetItem(ll_rc, 'flag_lignes', lsa_flag_lignes[ll_ind])

	res := 1;
	msg := 'OK';
	return;

END;
/

