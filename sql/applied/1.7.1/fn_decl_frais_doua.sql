CREATE OR REPLACE PROCEDURE GEO_ADMIN.FN_DECL_FRAIS_DOUA (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_cli_ref varchar2(50);
    ls_ind_mod_liv varchar2(50);
    ls_typ_ordre varchar2(50);
    ls_cen_ref varchar2(50);
    ls_decl_doua varchar2(50);
    ls_typ_frais varchar2(50);
    ls_transp varchar2(50);
    ll_nb_exp_pal number;
    ld_doua_pu_tot number;
BEGIN
    res := 0;
    msg := '';

    case arg_soc_code
        when 'BUK' then
            ls_typ_frais := 'DEDIMP';
            select O.CLI_REF, E.IND_MOD_LIV,O.TYP_ORDRE into ls_cli_ref,ls_ind_mod_liv,ls_typ_ordre
            from GEO_ORDRE O, GEO_ENTREP E
            where O.ORD_REF = arg_ord_ref and
                    O.CEN_REF = E.CEN_REF;

            If ls_typ_ordre <> 'ORI' and ls_ind_mod_liv ='S' and ls_cli_ref ='007396' Then
                select sum(L.EXP_NB_PAL) into ll_nb_exp_pal
                from GEO_ORDLIG L , GEO_ARTICLE A
                where L.ORD_REF =arg_ord_ref and
                        L.ART_REF = A.ART_REF and
                        A.ORI_CODE ='F';

                If ll_nb_exp_pal = 0 Then return; end if;

                select T.TRS_CODE,O.trp_code into ls_decl_doua, ls_transp
                from GEO_TRANSI T ,GEO_ORDRE O
                where   O.ORD_REF =arg_ord_ref and
                            O.TRP_CODE like T.TRS_CODE||'%'  and
                                T.IND_DECL_DOUANIER ='O';

                --Demande de SQ le 31/10/2022
                --Si transporteur PELLIET alors déclaration de douane effectué par LGLCUSTOMS
                if ls_transp = 'PELLIET' then ls_decl_doua := 'LGLCUSTOMS'; end if;

                If  ls_decl_doua is null or ls_decl_doua='' Then ls_decl_doua := 'BOLLORETLS'; end if;

                ld_doua_pu_tot := ll_nb_exp_pal*2;

                insert INTO GEO_ORDFRA (FRA_CODE ,ORD_REF, MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS)
                SELECT   ls_typ_frais,arg_ord_ref, ld_doua_pu_tot ,'GBP',1,ls_decl_doua
                from DUAL
                where not exists (select 1
                                        FROM GEO_ORDFRA
                                        where FRA_CODE=ls_typ_frais and
                                                ORD_REF=arg_ord_ref);

            End If;



        when 'SA' then
            ls_typ_frais := 'DEDEXP';
            select O.CLI_REF, O.CEN_REF,O.TYP_ORDRE into ls_cli_ref,ls_cen_ref,ls_typ_ordre
            from GEO_ORDRE O
            where O.ORD_REF = arg_ord_ref ;

            If ls_typ_ordre ='ORD' and  ls_cli_ref ='007488' and ls_cen_ref ='015461'  Then
                select sum(L.EXP_NB_PAL) into ll_nb_exp_pal
                from GEO_ORDLIG L , GEO_ARTICLE A
                where L.ORD_REF =arg_ord_ref and
                        L.ART_REF = A.ART_REF and
                        A.ORI_CODE ='F';

                If ll_nb_exp_pal = 0 Then return; end if;

                select T.TRS_CODE, O.trp_code into ls_decl_doua, ls_transp
                from GEO_TRANSI T ,GEO_ORDRE O
                where   O.ORD_REF =arg_ord_ref and
                            O.TRP_CODE like T.TRS_CODE||'%'  and
                                T.IND_DECL_DOUANIER ='O';

                --Demande de SQ le 31/10/2022
                --Si transporteur PELLIET alors déclaration de douane effectué par LGLCUSTOMS
                if ls_transp = 'PELLIET' then ls_decl_doua := 'LGLCUSTOMS'; end if;

                If ls_decl_doua is null or ls_decl_doua='' Then ls_decl_doua := 'BOLLORETLS'; end if;

                ld_doua_pu_tot := ll_nb_exp_pal*3.5;

                insert INTO GEO_ORDFRA (FRA_CODE ,ORD_REF, MONTANT,DEV_CODE,DEV_TX,TRP_CODE_PLUS)
                SELECT   ls_typ_frais,arg_ord_ref, ld_doua_pu_tot ,'GBP',1,ls_decl_doua
                from DUAL
                where not exists (select 1
                                        FROM GEO_ORDFRA
                                        where FRA_CODE=ls_typ_frais and
                                                ORD_REF =arg_ord_ref);

            ENd IF;

    end case;

    res := 1;
END;
/

