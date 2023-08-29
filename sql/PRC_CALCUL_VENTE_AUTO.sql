CREATE OR REPLACE PROCEDURE GEO_ADMIN.PRC_CALCUL_VENTE_AUTO(
    arg_ord_ref IN varchar2,
    arg_fou_code IN varchar2,
    arg_process IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
ls_trp_bta_code         GEO_ORDRE.TRP_BTA_CODE%TYPE;
ls_ach_bta_code         GEO_ORDLIG.ACH_BTA_CODE%TYPE;
ls_orl_ref                GEO_ORDLIG.ORL_REF%TYPE;
ld_ach_pu                GEO_ORDLIG.ACH_PU%TYPE;
ld_pdnet_client         GEO_ARTICLE_COLIS.PDNET_CLIENT%TYPE;
ld_trp_pu                GEO_ORDRE.TRP_PU%TYPE;
ld_tot_montant_frais    GEO_ORDLIG.VTE_PU%TYPE;
ld_prix_vente            GEO_ORDLIG.VTE_PU%TYPE;
ll_cde_totpal            GEO_ORDLIG.EXP_NB_PAL%TYPE;
ll_pal_nb_col            GEO_ORDLIG.PAL_NB_COL%TYPE;
ll_exp_totpal            GEO_ORDLIG.EXP_NB_PAL%TYPE;
ll_totpal                GEO_ORDLIG.EXP_NB_PAL%TYPE;
ls_sql varchar(5000);
ll_lig_cde_totpal        GEO_ORDLIG.EXP_NB_PAL%TYPE;
ll_lig_exp_totpal        GEO_ORDLIG.EXP_NB_PAL%TYPE;
cur_req_prix SYS_REFCURSOR;

BEGIN
    res := 0;
    msg := '';
/*
Calcul du prix de vente en automatique
---------------------------------------------
FORFAIT TRANSPORT AU CAMION                                                
-----------------------------------------                                                
   PDV au COLIS= PRIX D'ACHAT  (unité colis) + ( frais de transport / nombre de palette total de la commande /" col/pal") + (frais dans "ordre +" / nbre de pal total de la commande / "col/pal")                                                
   PDV au COLIS= PRIX D'ACHAT  (unité kilo) x poids net de l'article + ( frais de transport / nombre de palette total de la commande /" col/pal") + (frais dans "ordre +" / nbre de pal total de la commande / "col/pal")                                                
                                                
FORFAIT TRANSPORT A LA PALETTE                                                 
-------------------------------------------                                                
    pdv AU colis= PRIX D'ACHAT (unité colis) + ( frais de transport  /" col/pal")+ (frais dans "ordre +" / nbre de pal total de la commande / "col/pal")                                                
    pdv AU colis= PRIX D'ACHAT (unité kilo) x poids net de l'article  + ( frais de transport  /" col/pal")+ (frais dans "ordre +" / nbre de pal total de la commande / "col/pal")                                                
*/
ls_sql :=     ' select O.trp_bta_code, L.ach_pu, L.ach_bta_code, C.pdnet_client, O.trp_pu, L.pal_nb_col, L.orl_ref, sum(P.montant), sum(L.cde_nb_pal), sum(L.exp_nb_pal), S.cde_nb_pal, S.exp_nb_pal ';
ls_sql := ls_sql || ' from geo_ordre O, geo_ordlig L, geo_article_colis C, geo_ordfra P, ';
ls_sql := ls_sql || ' (SELECT sum(cde_nb_pal) as cde_nb_pal , sum(exp_nb_pal) as exp_nb_pal FROM GEO_ORDLIG where ord_ref = ''' || arg_ord_ref || ''') S ';
ls_sql := ls_sql || ' where O.ord_ref = ''' || arg_ord_ref || ''' ';
ls_sql := ls_sql || ' and O.ord_ref = L.ord_ref ';
ls_sql := ls_sql || ' and L.art_ref = C.art_ref ';
ls_sql := ls_sql || ' and P.ord_ref (+)= O.ord_ref ';
if arg_process = 'DETAIL' then
        ls_sql := ls_sql ||  ' and L.fou_code = '''|| arg_fou_code ||''' ';
end if;
ls_sql := ls_sql ||     ' group by O.trp_bta_code, L.ach_pu, L.ach_bta_code, C.pdnet_client, O.trp_pu, L.pal_nb_col, L.orl_ref, S.cde_nb_pal, S.exp_nb_pal ';

open cur_req_prix for ls_sql;

loop
        fetch cur_req_prix INTO ls_trp_bta_code, ld_ach_pu, ls_ach_bta_code, ld_pdnet_client, ld_trp_pu, ll_pal_nb_col, ls_orl_ref, ld_tot_montant_frais, ll_lig_cde_totpal, ll_lig_exp_totpal, ll_cde_totpal, ll_exp_totpal;
        
        exit when cur_req_prix%notfound;
        
        ld_prix_vente := 0;
        
        if ld_tot_montant_frais IS NULL  then 
            ld_tot_montant_frais := 0;
         end If;
        
        if ll_cde_totpal IS NULL then ll_cde_totpal := 0;
        end If;
        if ll_exp_totpal IS NULL then ll_exp_totpal := 0;
        end If;
        if ll_lig_cde_totpal IS NULL  then ll_lig_cde_totpal := 0;
        end If;
        if ll_lig_exp_totpal IS NULL  then ll_lig_exp_totpal := 0;
        end If;
        if ls_trp_bta_code IS NULL then 
			ld_trp_pu := 0;
			ls_trp_bta_code := 'PAL';
			
         end If;
           
        if arg_process = 'CONFIRM' then
            ll_totpal := ll_cde_totpal;
        else /* arg_process ='DETAIL*/
            ll_totpal := ll_exp_totpal;
        end if;

        if ll_totpal > 0 and  ll_pal_nb_col > 0 then
       
            case ls_trp_bta_code
                WHEN 'CAMION' then
                     case ls_ach_bta_code
                        when 'KILO' then
                            ld_prix_vente := (ld_ach_pu * ld_pdnet_client) + ((ld_trp_pu / ll_totpal) / ll_pal_nb_col) + ((ld_tot_montant_frais / ll_totpal) / ll_pal_nb_col);
                        when 'COLIS' then
                            ld_prix_vente := ld_ach_pu + ((ld_trp_pu / ll_totpal) / ll_pal_nb_col) + ((ld_tot_montant_frais / ll_totpal) / ll_pal_nb_col);
                    end case;
                    
                WHEN 'PAL' then
                    case ls_ach_bta_code
                        when 'KILO' then
                            ld_prix_vente := (ld_ach_pu * ld_pdnet_client) + (ld_trp_pu / ll_pal_nb_col) + ((ld_tot_montant_frais / ll_totpal) / ll_pal_nb_col);
                        when 'COLIS' then
                            ld_prix_vente := ld_ach_pu + (ld_trp_pu / ll_pal_nb_col)  + ((ld_tot_montant_frais / ll_totpal) / ll_pal_nb_col);
                    end case;
            end case;
           
        end if;
        if ld_prix_vente is not null and ld_prix_vente > 0 then
                update geo_ordlig
                set vte_pu = ld_prix_vente, vte_pu_net = ld_prix_vente, vte_bta_code = 'COLIS' 
                where ord_ref = arg_ord_ref and orl_ref = ls_orl_ref;
      
				commit;
        end if;


end loop;
close cur_req_prix;

res := 1;
msg := 'OK';
return;    

    
END PRC_CALCUL_VENTE_AUTO;
/