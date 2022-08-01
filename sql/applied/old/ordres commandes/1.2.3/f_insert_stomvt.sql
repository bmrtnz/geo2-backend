CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_INSERT_STOMVT (
	arg_sto_ref IN GEO_STOMVT.STO_REF%TYPE,
	arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	arg_art_ref IN GEO_ARTICLE.ART_REF%TYPE,
	arg_orl_ref IN GEO_ORDLIG.ORL_REF%TYPE,
	arg_mvt_qte IN NUMBER,
	arg_desc IN GEO_STOMVT.STM_DESC%TYPE,
	arg_username IN GEO_STOMVT.NOM_UTILISATEUR%TYPE,
	res OUT NUMBER,
	msg OUT varchar2
)
AS
	ll_stm_ref NUMBER;
	ls_stm_ref varchar2(20);
BEGIN
	-- correspond à f_insert_stomvt
	res := -1;
	msg := '';

	begin
		select seq_stm_num.nextval into ll_stm_ref from dual;
		ls_stm_ref := to_char(ll_stm_ref, 'FM099999');
	
			-- voir trigger GEO_STOMVT_BEF_INS qui actualise aussi geo_stock ainsi que les champs manquant de stomvt
		insert into geo_stomvt (stm_ref, sto_ref, nom_utilisateur, mvt_type, mvt_qte, ord_ref, art_ref, orl_ref, stm_desc)
		values(ls_stm_ref, arg_sto_ref, arg_username, 'R', arg_mvt_qte, arg_ord_ref, arg_art_ref, arg_orl_ref, arg_desc);
		
		res := 0;
		commit;

	EXCEPTION WHEN NO_DATA_FOUND THEN 
		res := -1;
		msg := 'erreur sur création de réservation orl_ref=' + arg_orl_ref + ' ' + arg_desc;
		rollback;		
	end;
	
END F_INSERT_STOMVT;
/

