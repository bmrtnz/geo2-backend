CREATE OR REPLACE PROCEDURE GEO_ADMIN.OF_GENERE_ENVOIS_BUK_ORI(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_flu_code IN GEO_FLUX.FLU_CODE%TYPE,
    arg_nom_utilisateur IN GEO_USER.NOM_UTILISATEUR%TYPE
)
AS
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    
    is_con_fluvar varchar2(6);
    is_moc_code varchar2(3);
    is_con_acces1 varchar2(70);
    is_con_acces2 varchar2(70);
    is_con_tyt varchar2(1);
    is_con_tiers varchar2(18);
    is_con_ref varchar2(6);
    ls_con_access2 varchar2(70);
    is_con_prenom varchar2(35);
    is_con_nom varchar2(35);
    ls_con_dot varchar2(70);
    ls_con_map varchar2(70);
    is_tiers_bis_code varchar2(18);
    
    ls_ord_ref_cur GEO_ORDRE.ORD_REF%TYPE;
    ls_fou_code_cur GEO_ENTREP.CEN_CODE%TYPE;
    
    ls_env_code geo_envois.env_code%TYPE;
	
	res  number;
    msg  varchar2(2000);
	
    
    cursor cur_mail (ref_ordre GEO_ORDRE.ORD_REF%TYPE, code_flux GEO_CONTAC.FLU_CODE%TYPE ) is  
    select distinct G1.ORD_REF_ORIG,O.CLI_CODE,C1.con_tyt, C1.con_tiers, C1.con_ref, C1.moc_code, C1.con_acces1, C1.con_acces2, C1.con_fluvar, C1.con_prenom, C1.con_nom 
    from  GEO_GEST_REGROUP G1, GEO_CONTAC C1, GEO_ORDRE O    
    where G1.ORD_REF_RGP  = ref_ordre and
          O.ORD_REF =G1.ORD_REF_ORIG and 
          C1.FLU_CODE =code_flux and 
          C1.CON_TIERS = O.CLI_CODE and 
          C1.SOC_CODE = O.SOC_CODE and
          C1.con_tyt = 'C' and
          C1.VALIDE ='O'
    union
    select distinct G1.ORD_REF_ORIG,O.CEN_CODE,C1.con_tyt, C1.con_tiers, C1.con_ref, C1.moc_code, C1.con_acces1, C1.con_acces2, C1.con_fluvar, C1.con_prenom, C1.con_nom 
    from  GEO_GEST_REGROUP G1, GEO_CONTAC C1, GEO_ORDRE O    
    where G1.ORD_REF_RGP  = ref_ordre and
          O.ORD_REF =G1.ORD_REF_ORIG and 
          C1.FLU_CODE =code_flux and 
          C1.CON_TIERS = O.CEN_CODE and 
          C1.SOC_CODE = O.SOC_CODE and
          C1.con_tyt = 'E' and
          C1.VALIDE ='O'

    union
    select distinct G1.ORD_REF_ORIG,G1.FOU_CODE_ORIG,C1.con_tyt, C1.con_tiers, C1.con_ref, C1.moc_code, C1.con_acces1, C1.con_acces2, C1.con_fluvar, C1.con_prenom, C1.con_nom 
    from GEO_GEST_REGROUP G1, GEO_CONTAC C1, GEO_ORDLIG L     
    where G1.ORD_REF_RGP  = ref_ordre and
          G1.ORD_REF_ORIG =  L.ORD_REF and 
          G1.FOU_CODE_ORIG = L.FOU_CODE and 
          G1.SOC_CODE_DETAIL = 'BUK' and 
          C1.FLU_CODE = code_flux and 
		  C1.CON_TIERS = L.FOU_CODE and
          C1.con_tyt = 'F' and
          C1.VALIDE ='O'
    union
    select distinct G1.ORD_REF_ORIG,O.TRP_CODE,C1.con_tyt, C1.con_tiers, C1.con_ref, C1.moc_code, C1.con_acces1, C1.con_acces2, C1.con_fluvar, C1.con_prenom, C1.con_nom 
    from  GEO_GEST_REGROUP G1, GEO_CONTAC C1, GEO_ORDRE O    
    where G1.ORD_REF_RGP  = ref_ordre and
          O.ORD_REF =G1.ORD_REF_ORIG and 
          C1.FLU_CODE =code_flux and 
          C1.CON_TIERS = O.TRP_CODE and
          C1.con_tyt = 'T' and
          C1.VALIDE ='O' and
		  exists (select 1 
				  from GEO_ENVOIS E
				  where E.ORD_REF = G1.ORD_REF_RGP and
						E.FLU_CODE = C1.FLU_CODE and
						E.TYT_CODE ='T' and 
						E.DEMDAT >= sysdate - 0.003); 
          
BEGIN
    res := 0;
    msg := '';
    
    
    
    If is_flu_code <> 'ORDRE' and is_flu_code <> 'DETAIL' Then
        return;
    End IF;
    
    select TYP_ORDRE into ls_typ_ordre
    from GEO_ORDRE
    where ORD_REF = is_ord_ref;
    
    If ls_typ_ordre <> 'RGP'  Then
        return;
    End IF;
    
    
    

          
    
    open cur_mail (is_ord_ref,is_flu_code);
    loop
            fetch cur_mail     INTO ls_ord_ref_cur,ls_fou_code_cur, is_con_tyt, is_con_tiers,is_con_ref, is_moc_code, is_con_acces1,is_con_acces2,is_con_fluvar, is_con_prenom, is_con_nom;
            EXIT WHEN cur_mail%notfound;
            of_insert_envois(
            ls_ord_ref_cur,
            is_flu_code,
            'O',
            is_con_tyt,
            is_con_tiers,
            is_con_ref,
            is_moc_code,
            is_con_acces1,
            is_con_acces2,
            is_con_fluvar,
            is_con_prenom,
            is_con_nom,
            null,
            null,
            null,
            null,
            arg_nom_utilisateur,
            res,
            msg,
            ls_env_code
        );
        update GEO_ENVOIS SET TRAIT_EXP ='N' where ORD_REF =ls_ord_ref_cur and TRAIT_EXP ='A';
           
    end loop;
    close cur_mail;
    commit;
    
END OF_GENERE_ENVOIS_BUK_ORI;
/
