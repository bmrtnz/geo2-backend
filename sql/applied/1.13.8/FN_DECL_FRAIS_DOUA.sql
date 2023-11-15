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




    
    
BEGIN
    res := 0;
    msg := '';

    case arg_soc_code
        when 'BUK' then
            select O.CLI_REF, E.IND_MOD_LIV,O.TYP_ORDRE into ls_cli_ref,ls_ind_mod_liv,ls_typ_ordre
            from GEO_ORDRE O, GEO_ENTREP E
            where O.ORD_REF = arg_ord_ref and
                    O.CEN_REF = E.CEN_REF;

            If ls_typ_ordre <> 'ORI' and ls_ind_mod_liv ='S' and ls_cli_ref ='007396' Then
                FN_DECL_FRAIS_DOUA_MANU(arg_ord_ref,arg_soc_code,res,msg);
			 
            End If;
    
        when 'SA' then

            select O.CLI_REF, O.CEN_REF,O.TYP_ORDRE into ls_cli_ref,ls_cen_ref,ls_typ_ordre
            from GEO_ORDRE O
            where O.ORD_REF = arg_ord_ref ;

         If ls_typ_ordre ='ORD' and  ls_cli_ref ='007488' and ls_cen_ref ='015461'  Then
                FN_DECL_FRAIS_DOUA_MANU(arg_ord_ref,arg_soc_code,res,msg);         
         end IF;
		else 
			NULL;
    end case;

    res := 1;
END;
/
