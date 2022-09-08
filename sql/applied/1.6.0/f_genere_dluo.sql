--f_genere_dluo

	-- retourne un texte de DLUO à partir du paramètre passé en entrée
	-- Casino = emballé le %DEdd/mm/yy% = date expédition
	-- Scafruit = %DLddmm% = date livraison
	-- Carrefour %DLMdd% = date livraison (mois sous forme de lettre de A à L, suivi du quantièmlme - ex F21 fonne 21 juin)
	-- AR 01/08/12 création

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_GENERE_DLUO" (
    arg_param in varchar2,
    arg_datexp in date,
    arg_datliv in date,
	arg_dluo out varchar2,
	res out number,
    msg out varchar2
)
AS
	ll_pos_deb number;
	ll_pos_fin number;
	ll_pos_m number;
	ls_rc varchar2(50);
	ls_param varchar2(50);
	ls_wrk varchar2(50);
	ls_tmp varchar2(50);
	ls_mois_alpha varchar2(50);
	ld_date date;
begin

	res := 0;
	msg := '';

		-- on isole d'abord la section paramètre
	ll_pos_deb	:= instr(arg_param, '%');
	if ll_pos_deb = 0 then
		arg_dluo := arg_param;
		return;
	end if;
	ll_pos_fin	:= instr(arg_param, '%', ll_pos_deb+1);
	if ll_pos_deb > 1 then
			--recupère le texte de début
		ls_rc	:= substr(arg_param, 1, ll_pos_deb - 1);
	end if;
		-- on traite la partie paramètre proprement dit
	ls_param	:=	substr(arg_param, ll_pos_deb + 1, ll_pos_fin - ll_pos_deb - 1);
		-- de quel type de date s'agit il ?
	case substr(ls_param, 0, 2)
		when 'DE'			-- data expédition
		then ld_date := arg_datexp;
		when 'DL'			-- date livraison
		then ld_date := arg_datliv;
		else
			arg_dluo := ls_rc;
			return;
	end case;
		-- on enlève le type de date
	ls_param	:= substr(ls_param, 3);
	ll_pos_m	:=  instr(ls_param, 'M');
	if ll_pos_m > 0 then
			-- il y a un mois transformé en A ... L (A=janvier ... L=décembre)
			-- on le supprime de la chaîne tout en gardant sa position
		ls_wrk	:= substr(ls_param, 0, ll_pos_m - 1) || substr(ls_param, ll_pos_m + 1);
			-- on convertit la date en string en supposant que les caractères standards sont utilisés (dd mm yy ou yyyy)
		ls_tmp	:=	to_char(ld_date, ls_wrk);
			-- on réintègre le mois converti à sa place initiale en supposant que la chaine formattée est de même structure que le format
		ls_mois_alpha := CHR(64 + TO_NUMBER(ltrim(TO_CHAR(ld_date, 'mm'), '0')));

		-- Cas particulier de mai et juin
		if ls_mois_alpha = 'E' then
			ls_mois_alpha := 'X';
		end if;
		if ls_mois_alpha = 'F' then
			ls_mois_alpha := 'Y';
		end if;
		ls_rc :=	ls_rc || substr(ls_tmp, 0, ll_pos_m -1) || ls_mois_alpha || substr(ls_tmp, ll_pos_m);
	else
			-- il s'agit d'une date au format standard utilisant dd mm yy etc ...
		ls_rc :=	ls_rc || to_char(ld_date, ls_param);
	end if;
		-- on ajoute la fin (si il y a une fin
	ls_rc := ls_rc ||	substr(arg_param, ll_pos_fin + 1);

	arg_dluo := ls_rc;

	res := 1;
	msg := 'OK';
	return;

end;
/

