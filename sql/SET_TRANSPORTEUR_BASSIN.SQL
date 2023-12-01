CREATE OR REPLACE PROCEDURE GEO_ADMIN."SET_TRANSPORTEUR_BASSIN" (
    arg_orl_ref GEO_ORDLIG.orl_ref%type,
    res out number,
    msg out varchar2
) AS
    ls_bac_code varchar2(50);
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
    ls_min_num_ligne varchar2(2);


    ls_trp_bac_code_ordre varchar2(50);
    ls_inc_code varchar2(50);
    li_ret_forfait number;
    current_transporteur varchar2(50);
    ld_res number;
    ls_msg varchar2(50);
    ls_soc_code GEO_SOCIETE.soc_code%type;
begin
/*****************************************************************************/
/*  AUTEUR : B.AMADEI                                        DATE : 28/09/23    */
/*  BUT     : définir le transporteur et les tarifs par defaut                */
/*  PARAMETRE                                                                */
/*           arg_orl_ref                                                     */
/* RETOUR                                                                       */
/****************************************************************************/

    msg := '';
    res := 0;

    select o.typ_ordre,o.cen_ref,o.ord_ref,ol.orl_lig, o.trp_bac_code,o.inc_code,f.bac_code,o.soc_code
    into ls_typ_ordre,ls_cen_ref,ls_ord_ref,ls_num_ligne,ls_trp_bac_code_ordre,ls_inc_code,ls_bac_code,ls_soc_code
    from geo_ordlig ol, geo_ordre o,geo_fourni f
    where ol.ord_ref = o.ord_ref
    and orl_ref = arg_orl_ref
    and ol.fou_code = f.fou_code;

    -- MAIl LEA du 09/02/2023 - Pb transporteurs par bassin (bloquant)
    -- Il faut que le transporteur par bassin s’alimente en auto en fonction du bassin du fournisseur
    -- de la première ligne et uniquement la première ligne.
    -- Peu importe le mode d’ajout de cette ligne. ??
    declare
        lignes_count number;
    begin

        select count(*)
        into lignes_count
        from geo_ordlig ol, geo_ordre o
        where ol.ord_ref = o.ord_ref
        AND ol.ord_ref  = (SELECT distinct ORD_REF FROM GEO_ORDLIG WHERE ORL_REF = arg_orl_ref);

        select MIN(orl_lig)
        into ls_min_num_ligne
        from geo_ordlig ol, geo_ordre o
        where ol.ord_ref = o.ord_ref
        AND ol.ord_ref  = (SELECT distinct ORD_REF FROM GEO_ORDLIG WHERE ORL_REF = arg_orl_ref);



        if (lignes_count > 1 and ls_num_ligne <> ls_min_num_ligne) or (ls_bac_code = ls_trp_bac_code_ordre and ls_trp_bac_code_ordre IS NOT NULL)    then
                  msg := 'Cette ligne n''est pas la premiere ligne, annulation de  l''assignation du transporteur par bassin';
            res := 1;
            return;
        end if;

        -- CDT 07/03/2023
        -- Si le transporteur de l'ordre est défini, ne pas l’écraser

            begin
            SELECT GEO_ORDRE.trp_code
            INTO current_transporteur
            FROM GEO_ORDRE, GEO_ORDLIG
            WHERE GEO_ORDRE.ord_ref = GEO_ORDLIG.ord_ref
            AND GEO_ORDLIG.orl_ref = arg_orl_ref;
            exception when no_data_found then
                    null;
            end;

        select dev_code
        into ls_soc_dev_code
        from GEO_SOCIETE
        where soc_code = ls_soc_code;

        begin
        select fn.bac_code into ls_bac_code
        from GEO_ORDLIG ol, GEO_FOURNI fn
        WHERE fn.FOU_CODE = ol.FOU_CODE
        and orl_ref = arg_orl_ref;
        exception when no_data_found then
            null;

       end;
        If ls_soc_code <> 'BUK' and  ls_typ_ordre <> 'RGP' Then
            begin
             select trp_code, trp_bta_code, trp_dev_code, trp_pu, dev_tx
             into ls_trp_code, ls_trp_bta_code, ls_trp_dev_code, ld_trp_dev_pu, ld_dev_tx
             from     geo_ent_trp_bassin,
                      geo_devise_ref
             where     cen_ref = ls_cen_ref and
                       bac_code = ls_bac_code and
                       trp_dev_code = dev_code and
                       dev_code_ref = ls_soc_dev_code;
             exception when no_data_found then
                    msg := 'pas de transporteur';
                    res := 1;
                    return;
            end;

            -- C'est le meme transporteur, inutile d'aller plus loin
            if current_transporteur = ls_trp_code then
                msg := 'transporteur identique';
                res := 1;
                return;
            end if;

			If ls_trp_code <> '-' THEN

				if (ls_trp_bac_code_ordre IS NULL) OR (ls_bac_code <> ls_trp_bac_code_ordre) OR (ls_bac_code  =  ls_trp_bac_code_ordre and ( current_transporteur is null or current_transporteur ='-' ))  THEN

				f_return_forfaits_trp(ls_cen_ref,ls_inc_code,ld_trp_dev_pu,ls_trp_bta_code,ls_trp_dev_code,ls_typ_ordre,ld_res,ls_msg,li_ret_forfait);

					if li_ret_forfait > 0 Then
						select   dev_tx into  ld_dev_tx
						from  geo_devise_ref
						where dev_code = ls_trp_dev_code and
						dev_code_ref = ls_soc_dev_code;
					end if;

					update geo_ordre
					set trp_code = ls_trp_code,
					trp_bac_code = ls_bac_code,
					trp_pu = ld_dev_tx * ld_trp_dev_pu,
					trp_bta_code = ls_trp_bta_code,
					trp_dev_code = ls_trp_dev_code,
					trp_dev_pu = ld_trp_dev_pu,
					trp_dev_taux = ld_dev_tx
					where ord_ref = ls_ord_ref;
					commit;
				 end if;
			else
					f_return_forfaits_trp(ls_cen_ref,ls_inc_code,ld_trp_dev_pu,ls_trp_bta_code,ls_trp_dev_code,ls_typ_ordre,ld_res,ls_msg,li_ret_forfait);


					if li_ret_forfait > 0 Then

						select   dev_tx into  ld_dev_tx
						from  geo_devise_ref
						where dev_code = ls_trp_dev_code and
						dev_code_ref = ls_soc_dev_code;

						update geo_ordre
						set trp_bac_code = ls_bac_code,
						trp_pu = ld_dev_tx * ld_trp_dev_pu,
						trp_bta_code = ls_trp_bta_code,
						trp_dev_code = ls_trp_dev_code,
						trp_dev_pu = ld_trp_dev_pu,
						trp_dev_taux = ld_dev_tx
						where ord_ref = ls_ord_ref;
						commit;

					end if;
			end if;
        end if;

    end;

    msg := 'OK';
    res := 1;
    return;
end;
/

