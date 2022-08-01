-- f_verif_logistique_ordre

-- verification de la bonne conformité de la logistique
-- avant envoi de documents et validation bon à facturer
-- AR 02/09/05 refonte wf_verif_logistique en fonction externe
-- AR 29/06/06 implémente corrections automatique des erreurs
-- AR 16/08/06 
-- AR 08/10/10 simplification du code
-- AR 21/10/10 version finalisée
-- AR 26/10/10 retour sur la fonction - autorisa	tion des lignes sans fournisseurs (suppression du delete et introduction de clause is null dans le curseur)

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_VERIF_LOGISTIQUE_ORDRE" (
    arg_ord_ref GEO_ORDRE.ord_ref%type,
	res out number,
    msg out varchar2
) AS
    ll_rc number;
    ls_rc varchar2(50);
    ldt_depdatp timestamp;
    ls_fou_code varchar2(50);
    ls_grp_code varchar2(50);
    ls_orx_ref varchar2(50);
    ls_propr_code varchar2(50);
    ll_count_grp number; --LLEF compteur du nbre de grp identique 
    ls_inc_code_fourni varchar2(50);
    ls_inc_code_cli varchar2(50);
    ls_inc_code varchar2(50);
    ls_sco_code varchar2(50); --LLEF recuo le secteur pour incoterme fournisseur
begin

    msg := '';
    res := 0;

    declare
        -- on va supprimer les éléments logistiques orphelins (logistique sans correspondance dans les lignes
        cursor CTROP is
            /*	select X.orx_ref, X.grp_code
            from geo_ordlig L, geo_ordlog X
            where  X.ord_ref = :arg_ord_ref
            and L.ord_ref (+) = X.ord_ref
            and L.fou_code (+) = X.fou_code
            and L.fou_code is null
            and X.fou_code not in (select distinct grp_code from geo_ordlog where ord_ref = :arg_ord_ref); --LLEF Rajout condition  */
            select  distinct X.orx_ref, X.grp_code
            from geo_ordlig L, geo_ordlog X
            where  X.ord_ref = arg_ord_ref
            and L.ord_ref  (+)= X.ord_ref
            and L.fou_code (+)= X.fou_code
            and L.fou_code is  null
            and not exists (select 1 from geo_ordlog X2 where X2.ord_ref =arg_ord_ref and  X.fou_code = X2.grp_code); --LLEF
    begin
        for t in CTROP
        loop
            begin
                delete from geo_ordlog where orx_ref = t.orx_ref; 
                --deb llef
                select count(*) into ll_count_grp from geo_ordlog where ord_ref = arg_ord_ref and grp_code =  t.grp_code;
                if  ll_count_grp < 1 then 
                    declare
                        cursor CTROPSUITE is
                            select orx_ref from geo_ordlog where ord_ref = arg_ord_ref and fou_code =  t.grp_code;
                    begin
                        for s in CTROPSUITE
                        loop
                            begin
                                delete from geo_ordlog where orx_ref = s.orx_ref;
                            exception when others then
                                rollback;
                                msg := 'pb sur suppression logistique des grps en trop orx_ref=' || s.orx_ref || ' ' || SQLERRM;
                                return;
                            end;
                        end loop;
                        commit;	
                    end;
                end if;
                --fin llef
                commit;
            exception when others then
                rollback;
                msg := 'pb sur suppression logistique en trop orx_ref=' || t.orx_ref || ' ' || SQLERRM;
                return;
            end;
        end loop;
    end;

    declare
        ls_pk_orx_ref varchar2(50);
        cursor CFOU is
        select distinct(L.fou_code)
            from geo_ordlig L, geo_ordlog X
            where  L.ord_ref  = arg_ord_ref
            and X.ord_ref (+)  = arg_ord_ref
            and X.fou_code (+) = L.fou_code
            and X.fou_code is null;
    begin
        -- on va insérer les éléments logistiques manquants (si il y en a)
        -- on ne tient pas compte des lignes sans code fournisseur (26/10/10 )
        select depdatp, inc_code into ldt_depdatp, ls_inc_code_cli from geo_ordre where ord_ref = arg_ord_ref;
        for r in CFOU
        loop
            begin
                if not r.fou_code is null then	-- est-ce possible de ramener des rows avec L.fou_code à null (?)
                    select grp_code, inc_code into ls_grp_code, ls_inc_code_fourni from geo_fourni where fou_code = r.fou_code;
                    --deb llef incoterm fournisseur uniquement pour le MARITIME dans un premier temps
                    select sco_code into ls_sco_code from geo_ordre where ord_ref = arg_ord_ref;
                    if ls_sco_code = 'MAR' then
                        if ls_inc_code_fourni is null or ls_inc_code_fourni ='' then
                            ls_inc_code := ls_inc_code_cli;
                        else
                            ls_inc_code := ls_inc_code_fourni;
                        end if;
                    else
                        ls_inc_code := '';
                    end if;
                    --fin llef
                    --ls_pk_orx_ref = SQLCA.F_SEQ_ORX_SEQ();

                    insert into geo_ordlog 
                        (orx_ref, ord_ref, fou_code, datdep_fou_p, grp_code, datdep_grp_p, incot_fourn)
                        values (F_SEQ_ORX_SEQ, arg_ord_ref, r.fou_code, ldt_depdatp, ls_grp_code, ldt_depdatp, ls_inc_code);
                end if;
            exception when others then
                rollback;
                msg := 'pb sur insert logistique ' || r.fou_code || ' ' || SQLERRM;
                return;
            end;
        end loop;
        commit;
    end;

    msg := 'OK';
    res := 1;
    return;

end;