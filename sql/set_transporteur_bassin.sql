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
    ls_num_ligne varchar2(2);
begin

    msg := '';
    res := 0;

    select o.typ_ordre,o.cen_ref,o.ord_ref,ol.orl_lig
    into ls_typ_ordre,ls_cen_ref,ls_ord_ref,ls_num_ligne
    from geo_ordlig ol, geo_ordre o
    where ol.ord_ref = o.ord_ref
    and orl_ref = arg_orl_ref;

    -- MAIl LEA du 09/02/2023 - Pb transporteurs par bassin (bloquant)
    -- Il faut que le transporteur par bassin s’alimente en auto en fonction du bassin du fournisseur
    -- de la première ligne et uniquement la première ligne.
    -- Peu importe le mode d’ajout de cette ligne. 😉
    declare
        lignes_count number;
    begin
        select count(*)
        into lignes_count
        from geo_ordlig ol, geo_ordre o
        where ol.ord_ref = o.ord_ref
        AND ol.ord_ref  = (SELECT ORD_REF FROM GEO_ORDLIG WHERE ORL_REF = arg_orl_ref);
        if lignes_count > 1 or ls_num_ligne <> '01' then
            msg := 'Cette ligne n''est pas la premiere ligne, annulation de  l''assignation du transporteur par bassin';
            res := 1;
            return;
        end if;
    end;

    -- CDT 07/03/2023
    -- Si le transporteur de l'ordre est défini, ne pas l’écraser
    declare
        current_transporteur varchar2(50);
    begin
        SELECT GEO_ORDRE.trp_code
        INTO current_transporteur
        FROM GEO_ORDRE, GEO_ORDLIG
        WHERE GEO_ORDRE.ord_ref = GEO_ORDLIG.ord_ref
        AND GEO_ORDLIG.orl_ref = arg_orl_ref;

        if current_transporteur is not null and current_transporteur <> '-' then
            msg := 'Un transporteur est déjà assigné sur cette ordre, annulation de  l''assignation du transporteur par bassin';
            res := 1;
            return;
        end if;
    end;

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

