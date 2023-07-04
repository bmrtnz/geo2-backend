CREATE OR REPLACE PROCEDURE GEO_ADMIN.PRC_GEN_FRAIS_DEDIMP (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
	ls_decl_doua varchar2(50);
	ld_doua_pu_tot number;
	ll_cde_nb_pal number;
	ll_exp_nb_pal number;
	li_indice number;
BEGIN
    res := 0;
    msg := '';

    begin
           select T.TRS_CODE into ls_decl_doua
			from GEO_TRANSI T ,GEO_ORDRE O
			where   O.ORD_REF =arg_ord_ref and
					O.TRP_CODE like T.TRS_CODE||'%'  and
					T.IND_DECL_DOUANIER ='O';
            EXCEPTION when others then
                ls_decl_doua := 'BOLLORETLS';
    end;

	select  sum(CDE_NB_PAL),sum(EXP_NB_PAL) into ll_cde_nb_pal, ll_exp_nb_pal
	from GEO_ORDLIG
	where ORD_REF=arg_ord_ref ;

	If ll_exp_nb_pal  IS NOT NULL and ll_exp_nb_pal > 0 Then
		ll_cde_nb_pal := ll_exp_nb_pal;
	End If;

	ld_doua_pu_tot := ll_cde_nb_pal*2;

	DELETE FROM GEO_ORDFRA
	where  ORD_REF =arg_ord_ref and
			FRA_CODE ='DEDIMP';

	INSERT INTO GEO_ORDFRA(ORD_REF,FRA_CODE,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS)
	VALUES (arg_ord_ref,'DEDIMP',ld_doua_pu_tot,'GBP',1,ls_decl_doua);

    msg := 'OK';
    res := 1;

end;
/

