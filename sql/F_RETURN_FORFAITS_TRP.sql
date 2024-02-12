CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_RETURN_FORFAITS_TRP(
    arg_cen_ref IN varchar2,
    arg_inc_code IN varchar2,
    arg_trp_dev_pu OUT number,
    arg_bta_code OUT varchar2,
    arg_dev_code OUT varchar2,
    arg_typ_ordre IN varchar2,
    res OUT number,
    msg OUT varchar2,
    li_ret OUT number
)
AS
    ls_soc_code varchar2(50);
    ls_sco_code varchar2(50);
    ls_gcl_code varchar2(50);
    ls_ind_forftrp varchar2(50);
    trp_dev_pu number;
BEGIN
    res := 0;
    msg := '';
    li_ret := 0 ;

/***************************************************************************/
/*  AUTEUR : B.AMADEI                                        DATE : 20/12/23    */
/*  BUT     : Donner le forfait transport  qui s'applique à l'ordre            */
/*  PARAMETRE                                                            */
/*            arg_cen_ref IN varchar2                                    */
/*    arg_inc_code IN varchar2                                            */
/*    arg_trp_dev_pu OUT number                                            */
/*    arg_bta_code OUT varchar2                                            */
/*    arg_dev_code OUT varchar2                                            */
/*    arg_typ_ordre IN varchar                                            */
/*    res OUT number                                                    */
/*    msg OUT varchar2                                                    */
/*    li_ret OUT number                                                    */
/* RETOUR                                                                */
/*         li_ret > 0 alors le forfait s'applique                                */
/****************************************************************************/


    begin
	
	
        select  C.GCL_CODE,C.SOC_CODE,C.SCO_CODE into ls_gcl_code,ls_soc_code, ls_sco_code
        from  GEO_CLIENT C,GEO_ENTREP E
        where      C.CLI_REF  = E.CLI_REF and
                    E.CEN_REF =arg_cen_ref;
    exception when no_data_found then
        ls_gcl_code := '';
    end;

    If arg_typ_ordre is not null and arg_typ_ordre <> ''  then

        select IND_FORFTRP into ls_ind_forftrp
        FROM GEO_TYPORD
        where TYP_ORD =arg_typ_ordre ;

        if ls_ind_forftrp = 'N'  then
            res := 1;
            return;
        End If;

    End If;

    IF arg_cen_ref ='000628' then
        res := 1;
        return;
    end if;

    If ls_soc_code <>'SA'  Then
        res := 1;
        return;
    end if;

    If ls_sco_code <> 'F'  Then
        res := 1;
        return;
    end if;

    begin
        select count(*), TRP_PU  into li_ret,  trp_dev_pu
        from GEO_FORFAITS_TRP
        where    GCL_CODE =ls_gcl_code and
                    INC_CODE =arg_inc_code and
                    VALIDE ='O'
        group by TRP_PU;
    exception when no_data_found then
       li_ret := 0 ;
    end;

    If li_ret= 0 then
        If ls_sco_code  = 'F' THEN
            ls_gcl_code :='NONDEF';
        End If;
        begin
            select count(*), TRP_PU into li_ret,  trp_dev_pu
            from GEO_FORFAITS_TRP
            where    GCL_CODE =ls_gcl_code and
                        INC_CODE =arg_inc_code and
                        VALIDE ='O'
            group by TRP_PU;
        exception when no_data_found then
            li_ret := 0;
        end;

    ENd IF;

    If li_ret >0 and  trp_dev_pu > 0 Then
        select TRP_UNITE,DEV_CODE,TRP_PU into arg_bta_code, arg_dev_code,arg_trp_dev_pu
        from GEO_FORFAITS_TRP
        where    GCL_CODE =ls_gcl_code and
                    INC_CODE =arg_inc_code and
                    VALIDE ='O';
    ELSE
        arg_bta_code :='KILO';
        arg_dev_code :='EUR';
    End If;


    if arg_trp_dev_pu is null then
        res := 2;
        msg := 'Forfait épuisé';
        return;
    end if;

    res := 1;

end;
/
