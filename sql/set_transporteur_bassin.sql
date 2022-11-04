--Vérification que le bassin de la station est en phase avec le bassin du transport par défaut souhaité par l'entrepôt
--Table GEO_ENT_TRP_BASSIN

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."SET_TRANSPORTEUR_BASSIN" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    arg_soc_code GEO_SOCIETE.soc_code%type,
	res out number,
    msg out varchar2
) AS
    ls_bac_code varchar2(50);
    is_bassin varchar2(50);
    ls_trp_code varchar2(50);
    ls_trp_bta_code varchar2(50);
    ls_trp_dev_code varchar2(50);
    ld_trp_dev_pu number;
    ld_dev_tx number;
    ld_trp_pu number;
    ls_typ_ordre varchar2(50);
    ls_soc_dev_code varchar2(50);
    ls_cen_ref varchar2(50);
    ls_ord_ref varchar2(50);
begin

    msg := '';
    res := 0;

    select o.typ_ordre,o.cen_ref,o.ord_ref
    into ls_typ_ordre,ls_cen_ref,ls_ord_ref
    from geo_ordlig ol, geo_ordre o
    where ol.ord_ref = o.ord_ref
    and orl_ref = arg_orl_ref;

    select dev_code
    into ls_soc_dev_code
    from GEO_SOCIETE
    where soc_code = arg_soc_code;

    select bac_code into ls_bac_code from geo_ordlig where orl_ref = arg_orl_ref;
    If arg_soc_code <> 'BUK' and  ls_typ_ordre <> 'RGP' Then

        if  is_bassin is null or is_bassin = '' then

            begin
                select trp_code, trp_bta_code, trp_dev_code, trp_pu, dev_tx
                into ls_trp_code, ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_pu, ld_dev_tx
                from 	geo_ent_trp_bassin,
                        geo_devise_ref
                where 	cen_ref = ls_cen_ref and
                            bac_code = ls_bac_code and
                            trp_dev_code = dev_code and
                            dev_code_ref = ls_soc_dev_code;

                update geo_ordre
                set
                    trp_pu = ld_dev_tx * ld_trp_dev_pu,
                    trp_code = ls_trp_code,
                    trp_bta_code = ls_trp_bta_code,
                    trp_dev_code = ls_trp_dev_code,
                    trp_dev_pu = ld_trp_dev_pu,
                    trp_dev_taux = ld_dev_tx,
                    trp_bac_code = ls_bac_code
                where ord_ref = ls_ord_ref;
                commit;
                is_bassin := ls_bac_code;
            exception when others then
                msg := msg || ' Pas de transporteur par défaut pour ce bassin/entrepôt';
            end;

        end if;
    End  If;

    msg := 'OK';
    res := 1;
    return;

exception when others then
    msg := 'Impossible d''assigner un transporteur par défaut ' || SQLERRM;
    res := 0;

end;
/

