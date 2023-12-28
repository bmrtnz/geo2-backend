-- AR 06/09/05 plus de copie imprimante pour AFA et MAR
-- AR 03/07/08 pas de copie impirmante pour secteur ESP
-- AR 21/09/10 corrige bug recherche langue de l'entrepôt (remaniementt du code pour client et entrepot en prenant les bons codes dans l'ordre)
-- AR 27/09/10 pas d'imprimante pour flux confirmatoin prix d'achat
-- AR 06/04/11 goto
-- AR 21/04/11 on va faire une copie imprimante une seule fois pour un même transporteur
-- BA 29/04/16 ajout de la gestion proprietaire (nouveau flux)


CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_INSERT_ENVOIS" (
	is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
	ib_mode_auto IN char,
	-- contact
	is_con_tyt IN varchar2,
	is_con_tiers IN varchar2,
	is_con_ref IN varchar2,
	is_moc_code IN varchar2,
	is_con_acces1 IN varchar2,
	ls_con_access2 IN varchar2,
	is_con_fluvar IN varchar2,
	is_con_prenom IN varchar2,
	is_con_nom IN varchar2,
	is_con_dot IN varchar2,
	is_con_map IN varchar2,
	is_imprimante IN varchar2,
	is_tiers_bis_code IN varchar2,
	-- response
	res IN OUT number,
	msg IN OUT varchar2,
	ls_env_code OUT geo_envois.env_code%TYPE
)
AS
	is_lan_code varchar2(50);
	is_soc_code varchar2(50);
	is_cam_code varchar2(50);
	is_nordre varchar2(50);
	is_per_codeass varchar2(50);
	is_per_codecom varchar2(50);
	is_sco_code varchar2(50);

	ls_contact varchar2(50);
	ls_con_nom varchar2(50);
	ls_con_prenom varchar2(50);
	ls_con_tyt varchar2(50);
	lb_trp char(1) := 'N';		-- indicateur de présence d'un transporteur
	lb_imrimante_ini char(1) := 'N';
	ls_con_flucode varchar(50);
BEGIN

	res := 0;
	msg := '';

	select o.soc_code, o.cam_code, o.nordre, o.per_codeass, o.per_codecom, o.sco_code
    into is_soc_code, is_cam_code, is_nordre, is_per_codeass, is_per_codecom, is_sco_code
    from geo_ordre o
    where ord_ref = is_ord_ref;

	if is_con_prenom is null then
		ls_con_prenom := '';
	end if;
	if is_con_nom is null then
		ls_con_nom := '';
	end if;
	if ls_con_prenom = '' then
		if ls_con_nom = '' then
			ls_contact := '';
		else
			ls_contact := ls_con_nom;
		end if;
	else
		if ls_con_nom = '' then
			ls_contact := ls_con_prenom;
		else
			ls_contact := ls_con_prenom || ' ' || ls_con_nom;
		end if;
	end if;

	begin
		case is_con_tyt
			when 'C' then
				select C.lan_code into is_lan_code
				from geo_client C, geo_ordre O
				where C.cli_ref = O.cli_ref
				and O.ord_ref = is_ord_ref;
			when 'E' then
				select E.lan_code into is_lan_code
				from geo_entrep E, geo_ordre O
				where O.ord_ref = is_ord_ref
				and E.cli_ref = O.cli_ref
				and E.cen_code = is_con_tiers;
			when 'T' then
				lb_trp	:= 'O';
			else
				is_lan_code := 'FR';
		end case;
	EXCEPTION WHEN OTHERS THEN
		is_lan_code := 'FR';
	end;

	begin
		select F_SEQ_ORX_SEQ into ls_env_code from dual;
		insert into geo_envois (
			env_code,
			trait_exp,
			soc_code,
			cam_code,
			nordre,
			ord_ref,
			flu_code
		)
		values (
			ls_env_code,
			'A',
			is_soc_code,
			is_cam_code,
			is_nordre,
			is_ord_ref,
			is_flu_code
		);
		commit;
	end;

	If is_con_tyt ='P' and is_flu_code ='ORDRE' Then
		update geo_envois
		set flu_code = 'ACHMAR'
		where env_code = env_code;
	End If;

	--Deb LLEF mise en place des fiches palettes lors de lieux de groupage existants
	if is_con_tyt = 'F' and  is_flu_code ='ORDRE' Then
		select FLU_CODE into ls_con_flucode from geo_contac where CON_REF = is_con_ref;
		if ls_con_flucode = 'PALGRP' then
			update geo_envois
			set flu_code = 'PALGRP'
			where env_code = ls_env_code;
		end if;
	end if;
	--Fin LLEF

	-- flu_code_compl tjs à null
	update geo_envois
	set
		fluvar = is_con_fluvar,
		moc_code = is_moc_code,
		acces1 = is_con_acces1
	where env_code = ls_env_code;
	-- acces2 null
	update geo_envois
	set
		tyt_code = is_con_tyt,
		tie_code = is_con_tiers,
		per_codeass = is_per_codeass,
		per_codecom = is_per_codecom
	where env_code = ls_env_code;
	-- dw_table.SetItem(il_row,'demdat',today())
	--       timestamp effectué par la trigger insert dans la base pour unicité du temps de référence
	--		on peut ainsi faire des stats fiables sur les délais
	--		il faut veiller à synchroniser l'heure des serveurs de comm sur celle du serveur de base
	update geo_envois
	set
		con_ref = is_con_ref,
		sco_code = is_sco_code
	where env_code = ls_env_code;

	if is_moc_code <> 'IMP' then
		if (is_sco_code = 'IND' or is_sco_code = 'PAL' or  is_sco_code = 'F' or is_sco_code = 'GB' or is_sco_code = 'AFA' or is_sco_code = 'MAR' or is_sco_code = 'SCA' or is_sco_code = 'EUR' or is_sco_code = 'ESP') and is_flu_code = 'ORDRE' and is_con_tyt <> 'T' then
			-- pas d'imprimante pour secteur F,GB,AFA,MAR,SCA,EUR confirmation d'ordre sauf transport
			update geo_envois
			set imp_id = ''
			where env_code = ls_env_code;
		elsif is_flu_code = 'MINI' then
			-- pas d'imprimante pour flux confirmatoin prix d'achat
			update geo_envois
			set imp_id = ''
			where env_code = ls_env_code;
		elsif (is_sco_code = 'IND' or is_sco_code = 'PAL' or  is_sco_code = 'AFA' or is_sco_code = 'MAR' or is_sco_code = 'SCA' or is_sco_code = 'EUR' or is_sco_code = 'ESP') and is_flu_code = 'DETAIL' and is_con_tyt <> 'T' then
			-- pas d'imprimante pour secteur AFA,MAR,SCA,EUR détail expédition sauf transport
			update geo_envois
			set imp_id = ''
			where env_code = ls_env_code;
		else
			update geo_envois
			set imp_id = is_imprimante
			where env_code = ls_env_code;
		end if;
	end if;

	if ib_mode_auto = 'O' then
		-- pas d'imprimante pour envoi auto des BL
		update geo_envois
		set imp_id = ''
		where env_code = ls_env_code;
	end if;

	if is_lan_code is null then
		is_lan_code := 'FR';
	end if;

	update geo_envois
	set 
		contact = ls_contact,
		lan_code = is_lan_code,
		tie_bis_code = is_tiers_bis_code
	where env_code = ls_env_code;
	--LLEF

	-- manque IMP_ID
	-- on va faire une copie imprimante une seule fois pour un même transporteur
	if lb_trp = 'O' then
		declare
			cursor cur_envois is
				select
					env_code,
					tyt_code,
					moc_code,
					tie_code,
					imp_id
				from geo_envois
				where ord_ref = is_ord_ref;
		begin
			for r in cur_envois
			loop

				if r.tyt_code = 'T' and  is_con_tiers =  r.tie_code and  (r.moc_code ='FTP' or lb_imrimante_ini = 'O') and  r.moc_code <>'IMP'  then
					update geo_envois
					set imp_id = ''
					where env_code = r.env_code;
				End If;
				
				--Suite à la demande de chantal le 14/12/2020
				if is_sco_code = 'GB' then
					update geo_envois
					set imp_id = ''
					where env_code = r.env_code;
				End If;
				
				If is_con_tiers =  r.tie_code Then 
					If r.imp_id is not null and (r.moc_code ='MAI' OR r.moc_code ='FAX') Then
						lb_imrimante_ini := 'O';
					end if;
				end if;
			end loop; 
		end;
	end if;

	commit;

	res := 1;
	msg := 'OK';

END;
/
