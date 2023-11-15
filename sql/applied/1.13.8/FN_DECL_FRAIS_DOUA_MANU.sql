CREATE OR REPLACE PROCEDURE GEO_ADMIN.FN_DECL_FRAIS_DOUA_MANU (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_decl_doua varchar2(50);
    ls_typ_frais varchar2(50);
    ls_transp varchar2(50);
    ll_nb_exp_pal number;
    ll_nb_cmd_pal number;
    ld_doua_pu_tot_new_dev number;
    ld_doua_pu_tot_new number;
    ld_doua_pu_tot_new_dev_dedexp number;
    ld_doua_pu_tot_new_dedexp number;
    ld_doua_pu_tot_new_dev_gmvs number;
    ld_doua_pu_tot_new_gmvs number;
    
    ls_doua_dev_code  varchar2(3);
    ld_doua_dev_tx number;
    
    
BEGIN
    res := 0;
    msg := '';

    case arg_soc_code
        when 'BUK' then
            ls_typ_frais := 'DEDIMP';
 
            select sum(L.EXP_NB_PAL),sum(L.CDE_NB_PAL) into ll_nb_exp_pal, ll_nb_cmd_pal
            from GEO_ORDLIG L , GEO_ARTICLE A
            where L.ORD_REF =arg_ord_ref and
                        L.ART_REF = A.ART_REF and
                        A.ORI_CODE ='F';

                If ll_nb_exp_pal = 0 Then ll_nb_exp_pal:=ll_nb_cmd_pal ; end if;
                
                If ll_nb_exp_pal = 0 Then return; end if;
                begin
                select T.TRS_CODE,O.trp_code into ls_decl_doua, ls_transp
                from GEO_TRANSI T ,GEO_ORDRE O
                where   O.ORD_REF =arg_ord_ref and
                            O.TRP_CODE like T.TRS_CODE||'%'  and
                                T.IND_DECL_DOUANIER ='O';

                --Demande de SQ le 31/10/2022
                --Si transporteur PELLIET alors d?claration de douane effectu? par LGLCUSTOMS
                if ls_transp = 'PELLIET' OR ls_transp ='LGLINTER' then ls_decl_doua := 'LGLCUSTOMS'; end if;
                exception when no_data_found then
                    if ls_transp = 'PELLIET' OR ls_transp ='LGLINTER' then
                        ls_decl_doua := 'LGLCUSTOMS';
                    else
                        ls_decl_doua := 'BOLLORE';
                    end if;
                end;
/*                
                ld_doua_pu_tot := ll_nb_exp_pal*2;

                insert INTO GEO_ORDFRA (FRA_CODE ,ORD_REF, MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS)
                SELECT   ls_typ_frais,arg_ord_ref, ld_doua_pu_tot ,'GBP',1,ls_decl_doua
                from DUAL
                where not exists (select 1
                                        FROM GEO_ORDFRA
                                        where FRA_CODE=ls_typ_frais and
                                                ORD_REF=arg_ord_ref);*/
                begin                                
                select (GEO_FRAIS_TYP.ACH_DEV_PU /26)* ll_nb_exp_pal,GEO_FRAIS_TYP.ACH_DEV_CODE,GEO_DEVISE_REF.DEV_TX,(GEO_FRAIS_TYP.ACH_DEV_PU /26)* ll_nb_exp_pal*GEO_DEVISE_REF.DEV_TX
                into ld_doua_pu_tot_new_dev, ls_doua_dev_code,ld_doua_dev_tx,ld_doua_pu_tot_new
                from GEO_FRAIS_TYP,GEO_DEVISE_REF
                where FRA_CODE = ls_typ_frais and
                      TYT_CODE = 'S' and
                      FRA_TIERS_CODE = ls_decl_doua and
                      GEO_DEVISE_REF.DEV_CODE_REF = 'GBP' and
                      GEO_DEVISE_REF.DEV_CODE = GEO_FRAIS_TYP.ACH_DEV_CODE;
                                                
                insert INTO GEO_ORDFRA
                (ORD_REF,FRA_CODE,ACH_DEV_PU,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS,ACH_QTE,ACH_PU,MONTANT_TOT)
                VALUES (arg_ord_ref,ls_typ_frais,ld_doua_pu_tot_new_dev,ld_doua_pu_tot_new_dev,ls_doua_dev_code,ld_doua_dev_tx,ls_decl_doua,1,ld_doua_pu_tot_new,ld_doua_pu_tot_new);
                exception when no_data_found then
                    insert INTO GEO_ORDFRA
                    (ORD_REF,FRA_CODE,DEV_CODE,DEV_TX,TRP_CODE_PLUS) VALUES (arg_ord_ref,'DEDIMP','GBP',1,ls_decl_doua);
         end;

    
        when 'SA' then
            ls_typ_frais := 'DEDEXP';



                select sum(L.EXP_NB_PAL) into ll_nb_exp_pal
                from GEO_ORDLIG L , GEO_ARTICLE A
                where L.ORD_REF =arg_ord_ref and
                        L.ART_REF = A.ART_REF and
                        A.ORI_CODE ='F';

                If ll_nb_exp_pal = 0 Then return; end if;
                begin
                    select T.TRS_CODE, O.trp_code into ls_decl_doua, ls_transp
                    from GEO_TRANSI T ,GEO_ORDRE O
                    where   O.ORD_REF =arg_ord_ref and
                                O.TRP_CODE like T.TRS_CODE||'%'  and
                                    T.IND_DECL_DOUANIER ='O';

                    --Demande de SQ le 31/10/2022
                    --Si transporteur PELLIET alors d?claration de douane effectu? par LGLCUSTOMS
                    if ls_transp = 'PELLIET' OR ls_transp ='LGLINTER' then ls_decl_doua := 'LGLCUSTOMS'; end if;
                    exception when no_data_found then
                    if ls_transp = 'PELLIET' OR ls_transp ='LGLINTER' then
                        ls_decl_doua := 'LGLCUSTOMS';
                    else
                        ls_decl_doua := 'BOLLORE';
                    end if;
                end;


                begin
                select (GEO_FRAIS_TYP.ACH_DEV_PU/26)*ll_nb_exp_pal ,GEO_FRAIS_TYP.ACH_DEV_CODE,GEO_DEVISE_REF.DEV_TX,(GEO_FRAIS_TYP.ACH_DEV_PU *GEO_DEVISE_REF.DEV_TX/26)*ll_nb_exp_pal
                into ld_doua_pu_tot_new_dev_dedexp, ls_doua_dev_code,ld_doua_dev_tx,ld_doua_pu_tot_new_dedexp
                from GEO_FRAIS_TYP,GEO_DEVISE_REF
                where FRA_CODE =ls_typ_frais and
                      TYT_CODE = 'S' and
                      FRA_TIERS_CODE = ls_decl_doua and
                      GEO_DEVISE_REF.DEV_CODE_REF = 'EUR' and
                      GEO_DEVISE_REF.DEV_CODE = GEO_FRAIS_TYP.ACH_DEV_CODE;
				
				delete  GEO_ORDFRA where ORD_REF =arg_ord_ref and FRA_CODE =ls_typ_frais;
                insert INTO GEO_ORDFRA
                    (ORD_REF,FRA_CODE,ACH_DEV_PU,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS,ACH_QTE,ACH_PU,MONTANT_TOT)
                    VALUES (arg_ord_ref,'DEDEXP',ld_doua_pu_tot_new_dev_dedexp,ld_doua_pu_tot_new_dev_dedexp,ls_doua_dev_code,ld_doua_dev_tx,ls_decl_doua,1,ld_doua_pu_tot_new_dedexp,ld_doua_pu_tot_new_dedexp);

                exception when no_data_found then
                    insert INTO GEO_ORDFRA
                    (ORD_REF,FRA_CODE,DEV_CODE,DEV_TX,TRP_CODE_PLUS) VALUES (arg_ord_ref,ls_typ_frais,'EUR',1,ls_decl_doua);
                end;
                delete  GEO_ORDFRA where ORD_REF =arg_ord_ref and FRA_CODE ='GMVS';

                begin
                select GEO_FRAIS_TYP.ACH_DEV_PU ,GEO_FRAIS_TYP.ACH_DEV_CODE,GEO_DEVISE_REF.DEV_TX,GEO_FRAIS_TYP.ACH_DEV_PU *GEO_DEVISE_REF.DEV_TX
                into ld_doua_pu_tot_new_dev_gmvs, ls_doua_dev_code,ld_doua_dev_tx,ld_doua_pu_tot_new_gmvs
                from GEO_FRAIS_TYP,GEO_DEVISE_REF
                where FRA_CODE ='GMVS' and
                      TYT_CODE = 'T' and
                      FRA_TIERS_CODE = ls_transp and
                      GEO_DEVISE_REF.DEV_CODE_REF = 'EUR' and
                      GEO_DEVISE_REF.DEV_CODE = GEO_FRAIS_TYP.ACH_DEV_CODE;

                insert INTO GEO_ORDFRA
                    (ORD_REF,FRA_CODE,ACH_DEV_PU,MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS,ACH_QTE,ACH_PU,MONTANT_TOT)
                    VALUES (arg_ord_ref,'GMVS',ld_doua_pu_tot_new_dev_gmvs,ld_doua_pu_tot_new_dev_gmvs,ls_doua_dev_code,ld_doua_dev_tx,ls_transp,1,ld_doua_pu_tot_new_gmvs,ld_doua_pu_tot_new_gmvs);

                exception when no_data_found then
                    res := res;
                end;
    end case;

    res := 1;
END;
/
