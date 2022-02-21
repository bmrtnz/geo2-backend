-- BAM le 19/10/2017
-- A partir de 14h30, bloquer les ordres dont le départ est aujourd'hui  

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_INIT_BLOCAGE_ORDRE" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_user in GEO_USER.NOM_UTILISATEUR%TYPE,
	res out number,
    msg out varchar2,
	bloquer out char
)
AS
	ls_a_bloquer varchar2(50) := 'N';
	ls_typ_ordre varchar2(50);
begin

	--select  'O' into  :ls_a_bloquer
	--from GEO_ORDRE,GEO_CLIENT
	--where  ((to_date(DEPDATP,'DD/MM/YY') = to_date(sysdate,'DD/MM/YY') and
	--			( TO_CHAR(sysdate,'HH24:MI:SS') ) > '14:30' ) OR DEPDATP< sysdate - 1  )  and 
	--		   ORD_REF =:is_cur_ord_ref  and 
	--		   GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF and 			
	--		GEO_CLIENT.IND_COMM_DEBLOQ  = 'N'		
	--using sqlca;

	res := 0;
	msg := '';

	declare
		user_cli varchar2(50);
	begin
		select geo_client
		into user_cli
		from GEO_USER
		where NOM_UTILISATEUR = arg_user;

		If user_cli = '2' then
			bloquer := 'N';
			res := 1;
			return; 
		End If;
	end;

	select TYP_ORDRE into ls_typ_ordre
	from GEO_ORDRE
	where ORD_REF = arg_ord_ref;

	select  'O' into  ls_a_bloquer
	from GEO_ORDRE
	where  ((to_date(DEPDATP,'DD/MM/YY') = to_date(sysdate,'DD/MM/YY') and
				( TO_CHAR(sysdate,'HH24:MI:SS') ) > '15:00' ) OR (to_date(DEPDATP,'DD/MM/YY') < to_date(sysdate,'DD/MM/YY'))  )  and 
			ORD_REF = arg_ord_ref  and 
			not exists (select 1  from GEO_CLIENT
						where GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF and 
								IND_COMM_DEBLOQ ='O')  and
			not exists (select 1  from GEO_CLIENT
						where GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF and 
								IND_MODIF_DETAIL ='O')  and						
			exists (select 1
						from GEO_ENVOIS
						WHERE GEO_ENVOIS.FLU_CODE ='ORDRE' AND  
								GEO_ENVOIS.ORD_REF = GEO_ORDRE.ORD_REF);

	If ls_a_bloquer is null  Then ls_a_bloquer := ''; end if;

	If ls_a_bloquer = 'O' then
		-- st_info_delai_depasse.visible = TRUE
		bloquer := 'O';
		res := 1;
		return;
	else 
		-- st_info_delai_depasse.visible = FALSE
		If ls_typ_ordre ='RPF' or ls_typ_ordre='REP' THEN
			bloquer := 'O';
			res := 1;
			return;
		ELSE
			bloquer := 'N';
			res := 1;
			return;
		ENd If;
	End If;
end;