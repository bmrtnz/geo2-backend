CREATE OR REPLACE PROCEDURE F_CHGT_QTE_ART_RET (
	arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
	res OUT NUMBER,
	msg OUT varchar2
)
IS
	ls_art_ref_transp varchar2(10) := '007859';
	ls_art_ref_indem varchar2(10) := '053442';
	ld_vte_qte NUMBER;
	ld_ach_qte NUMBER;
BEGIN
	-- correspond à f_chgt_qte_art_ret.pbl
	res := 0;
	msg := '';

	select sum(VTE_QTE), sum(ACH_QTE)  into ld_vte_qte, ld_ach_qte
	from geo_ordlig
	where ord_ref = arg_ord_ref and art_ref not in (ls_art_ref_transp, ls_art_ref_indem);

	update geo_ordlig set VTE_QTE = ld_vte_qte where ord_ref= arg_ord_ref and art_ref = ls_art_ref_transp;
	update geo_ordlig set VTE_QTE = ld_vte_qte, ACH_QTE = ld_ach_qte where ord_ref= arg_ord_ref and art_ref = ls_art_ref_indem;
	COMMIT;

	msg := 'OK';
	res := 1;

	RETURN;
END F_CHGT_QTE_ART_RET;
/

